package pt.lzgpom.bot.commands.tierlist.normal;

import static java.lang.Thread.sleep;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.entities.User;
import pt.lzgpom.bot.commands.Command;
import pt.lzgpom.bot.model.Bot;
import pt.lzgpom.bot.model.Group;
import pt.lzgpom.bot.model.Person;
import pt.lzgpom.bot.util.Converter;

public class Sort implements Command {

  private static final String REACTION_A = "🇦";
  private static final String REACTION_B = "🇧";
  private static final String REACTION_CANCEL = "❌";
  private static final String REACTION_CONTINUE = "✅";

  @Override
  public List<String> getCommands() {
    List<String> commands = new ArrayList<>();
    commands.add("sort");
    commands.add("s");
    return commands;
  }

  @Override
  public String getDescription() {
    return "It is used to help make a tier list more easily with choices.";
  }

  @Override
  public void run(String[] args, Bot bot, MessageChannel channel, Member user) {
    ExecutorService executor = Executors.newSingleThreadExecutor();

    executor.submit(() ->
    {
      Group group;
      boolean tierlist = false;

      if (args.length == 0) {
        group = bot.getTierListManager().getGroup();
        tierlist = true;
      } else {
        group = bot.getGroupByName(args[0]);
      }

      if (group == null) {
        channel.sendMessage("There is no tierlist going or the name of the group is invalid")
            .queue();
        return;
      }

      channel.sendMessage(String
          .format("%s, the sort of the group %s has been started on your PMs.", user.getUser().getName(),
              group.getName())).queue();

      MessageChannel privateChannel = user.getUser().openPrivateChannel().complete();
      Message beginMessage = privateChannel.sendMessage("Sort:").complete();

      List<Person> people = new ArrayList<>(group.getPeople());
      List<Decision> decisions = new ArrayList<>();

      boolean swap = true;

      for (int i = 0; (i < people.size() - 1 && swap); i++) {
        swap = false;
        for (int j = 0; j < people.size() - i - 1; j++) {
          Person p1 = people.get(j);
          Person p2 = people.get(j + 1);
          Decision decision = getDecisionFromList(decisions, p1, p2);

          if (decision == null) {
            Message message = privateChannel.sendMessage(makeDecisionMessage(p1, p2)).complete();
            message.addReaction(REACTION_A).queue();
            message.addReaction(REACTION_B).queue();
            message.addReaction(REACTION_CANCEL).queue();

            while (true) {
              try {
                if ((decision = getDecisionFromMessage(privateChannel, message.getIdLong(), p1, p2))
                    != null) {
                  break;
                }
              } catch (IllegalArgumentException ex) {
                Message tmp = privateChannel.sendMessage("The sort was canceled.").complete();
                try {
                  sleep(3000);
                } catch (InterruptedException e) {
                  e.printStackTrace();
                }

                beginMessage.delete().queue();
                message.delete().queue();
                tmp.delete().queue();
                return;
              }
            }

            decisions.add(decision);
            message.delete().queue();
          }

          if (decision.getResult(p1, p2)) {
            people.set(j, p2);
            people.set(j + 1, p1);
            swap = true;
          }
        }
      }

      beginMessage.delete().queue();
      privateChannel.sendMessage(Converter.peopleListToMessage(people)).queue();

      if (tierlist) {
        Message endMessage = privateChannel.sendMessage("Is this the tierlist you want?")
            .complete();
        endMessage.addReaction(REACTION_CONTINUE).queue();
        endMessage.addReaction(REACTION_CANCEL).queue();

        while (true) {
          if (hasReacted(privateChannel, endMessage.getIdLong(), REACTION_CONTINUE)) {
            try {
              bot.getTierListManager().autoCompleteWithSort(user.getUser(), people);
            } catch (IllegalArgumentException ex) {
              privateChannel.sendMessage(ex.getMessage()).queue();
            }
            break;
          }

          if (hasReacted(privateChannel, endMessage.getIdLong(), REACTION_CANCEL)) {
            break;
          }
        }
      }

      executor.shutdown();
    });
  }

  @Override
  public MessageEmbed getHelpMessage() {
    return null;
  }

  /**
   * Finds a decision in a list by the people.
   *
   * @param decisions The list of decisions.
   * @param p1 A person.
   * @param p2 Another person.
   * @return The decision if found, otherwise null.
   */
  private static Decision getDecisionFromList(List<Decision> decisions, Person p1, Person p2) {
    for (Decision decision : decisions) {
      if (decision.isThisDecision(p1, p2)) {
        return decision;
      }
    }

    return null;
  }


  /**
   * Creates a {@link MessageEmbed} with the people of the decision.
   *
   * @param p1 A person.
   * @param p2 Another person.
   * @return The {@link MessageEmbed} created.
   */
  private static MessageEmbed makeDecisionMessage(Person p1, Person p2) {
    EmbedBuilder eb = new EmbedBuilder();
    eb.setColor(Color.WHITE);
    eb.addField("A", p1.getName(), true);
    eb.addField("B", p2.getName(), true);
    return eb.build();
  }

  /**
   * Gets the decision of the user between the two people.
   *
   * @param channel The private channel of the user.
   * @param messageId The message id with the reactions.
   * @param p1 The first person.
   * @param p2 The second person.
   * @return The decision of the user, if not decided yet returns null.
   */
  private static Decision getDecisionFromMessage(MessageChannel channel, long messageId, Person p1,
      Person p2) {
    List<MessageReaction> reactions = channel.getMessageById(messageId).complete().getReactions();

    for (MessageReaction reaction : reactions) {
      if (reaction.getReactionEmote().getName().equals(REACTION_A)) {
        if (reaction.getUsers().complete().size() > 1) {
          return new Decision(p1, p2, false);
        }
      }

      if (reaction.getReactionEmote().getName().equals(REACTION_B)) {
        if (reaction.getUsers().complete().size() > 1) {
          return new Decision(p1, p2, true);
        }
      }

      if (reaction.getReactionEmote().getName().equals(REACTION_CANCEL)) {
        if (reaction.getUsers().complete().size() > 1) {
          throw new IllegalArgumentException();
        }
      }
    }

    return null;
  }

  private static boolean hasReacted(MessageChannel channel, long messageId, String unicode) {
    List<MessageReaction> reactions = channel.getMessageById(messageId).complete().getReactions();

    for (MessageReaction reaction : reactions) {
      if (reaction.getReactionEmote().getName().equals(unicode)) {
        return reaction.getUsers().complete().size() > 1;
      }
    }

    return false;
  }

  /**
   * This class is a Decision between two people. It is used to know the author decision only
   * between this two people.
   */
  private static class Decision {

    private Person p1;
    private Person p2;
    private boolean decision;

    Decision(Person p1, Person p2, boolean decision) {
      this.p1 = p1;
      this.p2 = p2;
      this.decision = decision;
    }

    /**
     * Gets the result of the decision.
     *
     * @param p1 The Person being compared
     * @param p2 The Person to compare against.
     * @return The result of the decision.
     */
    boolean getResult(Person p1, Person p2) {
      if (this.p1.equals(p1) && this.p2.equals(p2)) {
        return decision;
      }

      if (this.p2.equals(p1) && this.p1.equals(p2)) {
        return !decision;
      }

      throw new IllegalArgumentException();
    }

    /**
     * Checks if this is the Decision that contains the answer fo the People.
     *
     * @param p1 A person.
     * @param p2 Another person.
     * @return True if this has the answer, otherwise false.
     */
    boolean isThisDecision(Person p1, Person p2) {
      if (this.p1.equals(p1) && this.p2.equals(p2)) {
        return true;
      }

      return this.p2.equals(p1) && this.p1.equals(p2);

    }
  }
}

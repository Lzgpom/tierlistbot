package pt.lzgpom.bot.commands;

import static pt.lzgpom.bot.lib.Config.FILE_MODIFIER;
import static pt.lzgpom.bot.lib.Config.GROUPS_MODIFIER;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.entities.User;
import pt.lzgpom.bot.lib.Config;
import pt.lzgpom.bot.model.Bot;
import pt.lzgpom.bot.model.Group;
import pt.lzgpom.bot.model.bracket.BracketSolo;
import pt.lzgpom.bot.model.bracket.Challenger;
import pt.lzgpom.bot.model.bracket.Duel;
import pt.lzgpom.bot.model.bracket.impl.DuelSolo;
import pt.lzgpom.bot.util.bracket.Utils;
import pt.lzgpom.bot.util.bracket.image.AIImageDuel;
import pt.lzgpom.bot.util.bracket.image.ImageBracket;

public class Bracket implements Command {

  private static final String REACTION_A = "🇦";
  private static final String REACTION_B = "🇧";
  private static final String REACTION_COUNTER = "😡";
  private static final String REACTION_SEE = "👀";
  private static final String REACTION_CANCEL = "❌";
  private static final String REACTION_CONTINUE = "✅";

  //Reaction Placings
  private static final String REACTION_FIRST = "🥇";
  private static final String REACTION_SECOND = "🥈";
  private static final String REACTION_THIRD = "🥉";

  private BracketSolo bracket;
  private Map<User, Integer> counters;
  private Map<User, Integer> minorDisagrees;
  private List<User> participants;
  private Map<User, Color> userColors;

  public Bracket() {
    this.bracket = null;
  }

  @Override
  public List<String> getCommands() {
    List<String> commands = new ArrayList<>();
    commands.add("bracket");
    commands.add("br");
    return commands;
  }

  @Override
  public String getDescription() {
    return "Creates a bracket in which people can participate.";
  }

  @Override
  public MessageEmbed getHelpMessage() {
    return null;
  }

  /**
   * Asks who wants to participate in the bracket.
   *
   * @param channel The message channel where it was invoked.
   * @param counters The number of counters all users have.
   * @param minorDisagrees The number of minor disagrees all user have.
   */
  private void start(MessageChannel channel, int counters, int minorDisagrees) {
    Message message = channel.sendMessage("React to participate!").complete();
    long id = message.getIdLong();
    message.addReaction("🤚").queue();
    Guild guild = message.getGuild();

    try {
      TimeUnit.SECONDS.sleep(Config.TIME_TO_REACT);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    participants = new ArrayList<>(
        channel.getMessageById(id).complete().getReactions().get(0).getUsers().complete());
    participants.remove(participants.size() - 1);
    Collections.shuffle(participants);

    this.counters = new HashMap<>();
    this.minorDisagrees = new HashMap<>();
    this.userColors = new HashMap<>();

    for (User user : participants) {
      this.counters.put(user, counters);
      this.minorDisagrees.put(user, minorDisagrees);
      this.userColors.put(user, guild.getMember(user).getColor());
    }
  }

  @Override
  public void run(String[] args, Bot bot, MessageChannel channel, User author) {
    if (isBracketRunning()) {
      channel.sendMessage("There is a bracket already going...").queue();
      return;
    }

    if (args.length < 6) {
      channel.sendMessage("There are not enough arguments").queue();
      return;
    }

    for (int i = 0; i < 4; i++) {
      if (!pt.lzgpom.bot.util.Utils.isNumeric(args[i])) {
        channel.sendMessage("Invalid first four params, they are not numbers...").queue();
        return;
      }
    }

    List<List<Challenger>> teams = startTeams(channel, args, bot);

    if (teams.size() <= 1) {
      channel.sendMessage("Not enough teams...").queue();
      return;
    }

    start(channel, Integer.parseInt(args[2]), Integer.parseInt(args[3]));

    if (participants.size() < 1) {
      channel.sendMessage("Not enough users...").queue();
      return;
    }

    bracket = new BracketSolo(teams, true);

    ExecutorService executor = Executors.newSingleThreadExecutor();

    executor.submit(() ->
    {
      while (bracket.hasNextDuel()) {
        DuelSolo duel = bracket.getNextDuel();
        User user = getNextUser();
        channel.sendFile(Utils.bufferedImageToInputStream(
            new AIImageDuel(duel, counters, minorDisagrees).createImage()), "duel.jpg").queue();
        Message message = channel.sendMessage(makeDuelMessage(duel, user)).complete();
        long messageId = message.getIdLong();

        Map<User, Integer> userWinners = doMinorDisagrees(user, duel);

        //Reactions for the user to interact with.
        message.addReaction(REACTION_A).queue();
        message.addReaction(REACTION_B).queue();
        message.addReaction(REACTION_SEE).queue();
        message.addReaction(REACTION_CANCEL).queue();

        int winner = -1;
        boolean hasShownBracket = false;

        while (winner == -1) {
          List<MessageReaction> reactions = channel.getMessageById(messageId).complete()
              .getReactions();

          //Show bracket.
          if (!hasShownBracket && getPeopleReacted(reactions, REACTION_SEE).size() > 0) {
            uploadBracket(channel);
            hasShownBracket = true;
          }

          //Cancel Bracket
          if (getPeopleReacted(reactions, REACTION_CANCEL).size() == participants.size()) {
            bracket = null;
            channel.sendMessage("Bracket canceled!").queue();
            return;
          }

          //Set winner to 0.
          if (hasUserReacted(user, reactions, REACTION_A)) {
            winner = 0;
          }

          //Set winner to 1.
          else if (hasUserReacted(user, reactions, REACTION_B)) {
            winner = 1;
          }
        }

        bracket.setDuelWinner(duel, user, winner);

        //Checks if someone disagreed and if all agreed checks if it was everyone.
        if (!checkMinorDisagrees(channel, duel, userWinners, winner)
            && userWinners.size() != participants.size() - 1) {
          doCounters(channel, user, duel, winner);
        }

        bracket.moveToNextPart();
      }

      end(channel);
      executor.shutdown();
    });
  }

  /**
   * Treats the start part of minor disagrees per round.
   *
   * @param user The user who is going to decide.
   * @param duel The duel to check the minor disagrees.
   * @return A map with the winners for each user. The map might be empty which mean no one had a
   * minor disagree.
   */
  private Map<User, Integer> doMinorDisagrees(User user, DuelSolo duel) {
    Map<User, Long> userMessages = new HashMap<>();

    //Sends the message to all user who can disagree.
    for (Map.Entry<User, Integer> entry : minorDisagrees.entrySet()) {
      if (!entry.getKey().equals(user)) {
        if (entry.getValue() > 0) {
          Message message = entry.getKey().openPrivateChannel().complete()
              .sendMessage(makeDuelMessage(duel, user)).complete();
          message.addReaction(REACTION_A).queue();
          message.addReaction(REACTION_B).queue();

          userMessages.put(entry.getKey(), message.getIdLong());
        }
      }
    }

    Map<User, Integer> userWinners = new HashMap<>();

    //If no one had disagrees available
    if (userMessages.isEmpty()) {
      return userWinners;
    }

    //Gets the results from the pm sent.
    while (!userMessages.isEmpty()) {
      for (Iterator<Map.Entry<User, Long>> it = userMessages.entrySet().iterator();
          it.hasNext(); ) {
        Map.Entry<User, Long> entry = it.next();

        List<MessageReaction> reactions = entry.getKey().openPrivateChannel().complete()
            .getMessageById(entry.getValue()).complete().getReactions();

        if (hasUserReacted(entry.getKey(), reactions, REACTION_A)) {
          userWinners.put(entry.getKey(), 0);
          it.remove();
        } else if (hasUserReacted(entry.getKey(), reactions, REACTION_B)) {
          userWinners.put(entry.getKey(), 1);
          it.remove();
        }
      }
    }

    return userWinners;
  }

  /**
   * Checks if someone disagreed with the previous answer.
   *
   * @param channel The message channel.
   * @param duel The duel to counter.
   * @param userWinners The map with the user's choices.
   * @param winner The winner selected by the user.
   * @return If someone disagreed, other false.
   */
  private boolean checkMinorDisagrees(MessageChannel channel, DuelSolo duel,
      Map<User, Integer> userWinners, int winner) {
    for (Map.Entry<User, Integer> entry : userWinners.entrySet()) {
      if (entry.getValue() != winner) {
        minorDisagrees.replace(entry.getKey(), minorDisagrees.get(entry.getKey()) - 1);
        bracket.addDuelCounter(duel, entry.getKey());
        channel.sendMessage(String.format("The user %s disagrees...", entry.getKey().getName()))
            .queue();
        return true;
      }
    }

    return false;
  }

  /**
   * After the duel it awaits for someone to counter or for all the participants to continue.
   *
   * @param channel The message channel.
   * @param user The {@link User} who decided this round.
   * @param duel The {@link Duel} of this encounter.
   * @param winner The winner of the round.
   */
  private void doCounters(MessageChannel channel, User user, DuelSolo duel, int winner) {
    if (hasAnyoneCounter(user)) {
      Message message = channel.sendMessage(String.format("%s choose %s, do you wish to counter?",
          user.getName(), (winner == 0) ? REACTION_A : REACTION_B)).complete();
      long messageId = message.getIdLong();

      message.addReaction(REACTION_COUNTER).queue();
      message.addReaction(REACTION_SEE).queue();
      message.addReaction(REACTION_CONTINUE).queue();

      boolean hasShownBracket = false;
      while (true) {
        List<MessageReaction> reactions = channel.getMessageById(messageId).complete()
            .getReactions();

        if (getPeopleReacted(reactions, REACTION_COUNTER).size() > 0) {
          User counterUser = addCounter(user, reactions, duel);

          if (counterUser != null) {
            channel.sendMessage(String.format("%s countered the duel!!!", counterUser.getName()))
                .queue();
            return;
          }
        }

        //Show bracket.
        if (!hasShownBracket && getPeopleReacted(reactions, REACTION_SEE).size() > 0) {
          uploadBracket(channel);
          hasShownBracket = true;
        }

        List<User> users = getPeopleReacted(reactions, REACTION_CONTINUE);
        users.remove(user);

        if (users.size() == participants.size() - 1) {
          return;
        }
      }
    }
  }

  /**
   * Checks if someone tried to add a counter. If someone did it adds it.
   *
   * @param user The user who is voting.
   * @param reactions The reactions of the message.
   * @param duel The deul to add the counter.
   * @return If a counter was added, otherwise false.
   */
  private User addCounter(User user, List<MessageReaction> reactions, DuelSolo duel) {
    List<User> users = getPeopleReacted(reactions, REACTION_COUNTER);

    if (users.size() > 0) {
      for (User counterUser : users) {
        if (counterUser != user) {
          try {
            int n = counters.get(counterUser);

            if (n > 0) {
              bracket.addDuelCounter(duel, counterUser);
              counters.replace(counterUser, n - 1);

              return counterUser;
            }
          } catch (NullPointerException ignored) {

          }
        }
      }
    }

    return null;
  }

  /**
   * Makes the teams according to the args of the command.
   *
   * @param channel The message channel.
   * @param args The arguments of the command.
   * @param bot The bot.
   * @return The list of teams.
   */
  private List<List<Challenger>> startTeams(MessageChannel channel, String[] args, Bot bot) {
    List<List<Challenger>> teams = new ArrayList<>();

    int teamSize = Integer.parseInt(args[0]);
    int numberTeams = Integer.parseInt(args[1]);

    if (teamSize <= 0) {
      channel.sendMessage("Team size cannot be 0 or less.").queue();
      return teams;
    }

    if (numberTeams <= 1) {
      channel.sendMessage("The number of teams cannot be 1 or less.").queue();
      return teams;
    }

    List<Challenger> challengers;

    if (args[4].equalsIgnoreCase(FILE_MODIFIER)) {
      challengers = Utils.readChallengersFile(args[5]);

      if (challengers.isEmpty()) {
        channel.sendMessage("Invalid file or file is empty.").queue();
        return teams;
      }
    } else if (args[4].equalsIgnoreCase(GROUPS_MODIFIER)) {
      List<Group> groups = new ArrayList<>();

      for (int i = 5; i < args.length; i++) {
        Group group = bot.getGroupByName(args[i]);

        if (group == null) {
          channel.sendMessage("Group " + args[i] + " doesn't exist...").queue();
        } else {
          groups.add(group);
        }
      }

      if (groups.isEmpty()) {
        channel.sendMessage("There were imputed 0 valid groups...").queue();
        return teams;
      }

      challengers = Utils.convertGroupsIntoChallengers(groups);
    } else {
      return teams;
    }

    return Utils.createTeams(challengers, teamSize, numberTeams);
  }

  private void end(MessageChannel channel) {
    channel.sendMessage(String.format("The final results are:%n%s %s%n%s %s%n%s %s", REACTION_FIRST,
        teamToString(bracket.getDuels().get(0).get(0).getWinner()),
        REACTION_SECOND, teamToString(bracket.getDuels().get(0).get(0).getLooser()), REACTION_THIRD,
        teamToString(bracket.getDuels().get(0).get(1).getWinner()))).queue();
    uploadBracket(channel);
    bracket = null;
    counters = null;
    minorDisagrees = null;
  }

  /**
   * Checks if there is a bracket running.
   *
   * @return If there is a bracket running returns true, otherwise false.
   */
  private boolean isBracketRunning() {
    return bracket != null;
  }

  /**
   * Creates a {@link MessageEmbed} of a duels.
   *
   * @param duel The {@link Duel} to convert to message.
   * @param user The {@link User} to answer this dual.
   * @return The {@link MessageEmbed} created.
   */
  private MessageEmbed makeDuelMessage(Duel duel, User user) {
    EmbedBuilder eb = new EmbedBuilder();
    eb.setColor(userColors.get(user));

    String fieldName;
    String phase = Utils.phases.get(duel.getRound());

    if (phase != null) {
      fieldName = phase;
    } else {
      fieldName = String.format("Round of %d", (int) Math.pow(2, duel.getRound() + 1));
    }

    eb.addField(String.format("%s%nYour turn:", fieldName), user.getName(), true);

    eb.addField("A", teamToStringEmbedded(duel.getFirstChallenger()), true);
    eb.addField("B", teamToStringEmbedded(duel.getSecondChallenger()), true);

    return eb.build();
  }

  /**
   * Creates a String of the team with the challenger and the link to a image search.
   *
   * @param team The team to create the string from.
   * @return The string created.
   */
  private static String teamToStringEmbedded(List<Challenger> team) {
    StringBuilder s = new StringBuilder();

    for (int i = 0; i < team.size(); i++) {
      s.append(String.format("[%s](%s)", team.get(i).getName(), team.get(i).getUrl()));

      if (i != team.size() - 1) {
        s.append("\n");
      }
    }

    return s.toString();
  }

  /**
   * Creates a String of the team with the challenger and the link to a image search.
   *
   * @param team The team to create the string from.
   * @return The string created.
   */
  private static String teamToString(List<Challenger> team) {
    StringBuilder s = new StringBuilder();

    for (int i = 0; i < team.size(); i++) {
      s.append(String.format("%s", team.get(i).getName()));

      if (i != team.size() - 1) {
        s.append(", ");
      }
    }

    return s.toString();
  }

  /**
   * Returns the next {@link User}.
   *
   * @return the next {@link User}.
   */
  private User getNextUser() {
    User user = participants.remove(0);
    participants.add(user);
    return user;
  }

  /**
   * Uploads the bracket in image form.
   *
   * @param channel The message channel where it was invoked.
   */
  private void uploadBracket(MessageChannel channel) {
    ImageBracket img = new ImageBracket(bracket, userColors);
    channel.sendFile(Utils.bufferedImageToInputStream(img.createImage()), "bracket.jpg").queue();
  }

  private static boolean hasUserReacted(User user, List<MessageReaction> reactions,
      String unicode) {
    for (MessageReaction reaction : reactions) {
      if (reaction.getReactionEmote().getName().equals(unicode)) {
        return reaction.getUsers().complete().contains(user);
      }
    }

    return false;
  }

  private static List<User> getPeopleReacted(List<MessageReaction> reactions, String unicode) {
    for (MessageReaction reaction : reactions) {
      if (reaction.getReactionEmote().getName().equals(unicode)) {
        List<User> users = new ArrayList<>(reaction.getUsers().complete());
        users.remove(users.size() - 1);
        return users;
      }
    }

    return new ArrayList<>();
  }

  /**
   * Checks if the other users have a counter left.
   *
   * @param user The user who voted this round.
   * @return True if the other users have at least one counter.
   */
  private boolean hasAnyoneCounter(User user) {
    for (User other : counters.keySet()) {
      if (!user.equals(other)) {
        if (counters.get(other) > 0) {
          return true;
        }
      }
    }

    return false;
  }
}

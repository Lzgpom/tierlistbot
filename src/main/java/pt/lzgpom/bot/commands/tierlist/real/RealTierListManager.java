package pt.lzgpom.bot.commands.tierlist.real;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.util.Pair;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.entities.User;
import pt.lzgpom.bot.commands.tierlist.normal.TierListManager;
import pt.lzgpom.bot.model.Bot;
import pt.lzgpom.bot.model.bracket.Challenger;
import pt.lzgpom.bot.model.realtierlist.ChallengerScore;
import pt.lzgpom.bot.model.realtierlist.RealTierList;
import pt.lzgpom.bot.model.realtierlist.Tier;
import pt.lzgpom.bot.util.SaveLoader;
import pt.lzgpom.bot.util.Utils;
import pt.lzgpom.bot.util.real.ImageRealTierList;

public class RealTierListManager {

  private static final int PLACES_REACTIONS = 5;

  private static final Logger LOGGER = Logger.getLogger(TierListManager.class.getSimpleName());
  private static final String PREVIEW_REACTION = "👍";

  private final Bot bot;
  private final List<Thread> threads = new ArrayList<>();
  private final List<RealTierList> lists = new ArrayList<>();
  private String id;
  private boolean hasStarted;
  private int numTiers;
  private Map<User, List<Pair<Challenger, Long>>> messages = new HashMap<>();

  public RealTierListManager(Bot bot) {
    this.bot = bot;
    this.hasStarted = false;
  }

  private static boolean hasUserReacted(List<MessageReaction> reactions) {
    for (MessageReaction reaction : reactions) {
      if (reaction.getReactionEmote().getName().equals(RealTierListManager.PREVIEW_REACTION)) {
        return reaction.getCount() > 1;
      }
    }

    return false;
  }

  /**
   * Checks whether a {@link pt.lzgpom.bot.model.realtierlist.RealTierList} has started.
   *
   * @return True if it has, otherwise false.
   */
  public boolean isRealTierListStarted() {
    return hasStarted;
  }

  void start(List<User> users, List<Challenger> challengers, String id, int numTiers) {
    this.hasStarted = true;
    this.numTiers = numTiers;
    this.id = id;

    LOGGER.log(Level.INFO, "Starting real tier list.");

    ExecutorService executor = Executors.newFixedThreadPool(3);

    for (User user : users) {
      LOGGER.log(Level.INFO, "Sending people to " + user.getName());

      executor.submit(() -> {
        messages.put(user, new ArrayList<>());
        MessageChannel privateChannel = user.openPrivateChannel().complete();

        List<Color> colors = Utils.rainbowColor(challengers.size());

        for (int i = 0; i < challengers.size(); i++) {
          Challenger challenger = challengers.get(i);

          LOGGER
              .log(Level.INFO,
                  String.format("Sending %s to %s", challenger.getName(), user.getName()));

          Message message = privateChannel
              .sendMessage(challengerToMessage(challenger, colors.get(i))).complete();
          messages.get(user).add(new Pair<>(challenger, message.getIdLong()));

          int j = 1;
          for (Tier tier : Utils.getTiers().values()) {
            message.addReaction(tier.getReaction()).queue();
            if(j >= numTiers){
              break;
            }

            j++;
          }

          for (j = 1; j <= PLACES_REACTIONS; j++) {
            message.addReaction(Utils.getReactionInPos(j)).queue();
          }
        }

        Thread previewThread = new Thread(() -> {
          List<Long> messageIds = new ArrayList<>();

          while (hasStarted) {
            Message message = privateChannel.sendMessage("Preview").complete();
            message.addReaction(PREVIEW_REACTION).queue();
            messageIds.add(message.getIdLong());

            while (!hasUserReacted(
                privateChannel.getMessageById(message.getIdLong()).complete().getReactions()
            )) {
              //Wait
            }

            for (long messageId : messageIds) {
              privateChannel.deleteMessageById(messageId).queue();
            }
            messageIds.clear();

            RealTierList realTierList = getRealTierList(privateChannel, user, id, true,
                messageIds);

            if (realTierList != null) {
              messageIds.add(privateChannel
                  .sendFile(pt.lzgpom.bot.util.bracket.Utils.bufferedImageToInputStream(
                      ImageRealTierList.createImage(realTierList)), "realTierList.jpg")
                  .complete().getIdLong());
            }
          }
        });

        previewThread.start();
        threads.add(previewThread);
      });
    }

    executor.shutdown();
  }

  public void end(MessageChannel channel) {
    lists.clear();

    List<Future<?>> tasks = new ArrayList<>();
    ExecutorService executor = Executors.newFixedThreadPool(3);

    for (User user : messages.keySet()) {
      tasks.add(executor.submit(() ->
      {
        RealTierList realTierList = getRealTierList(channel, user, id, false, new ArrayList<>());

        if (realTierList == null) {
          return;
        }

        lists.add(realTierList);
      }));
    }

    for (Future<?> task : tasks) {
      while (true) {
        if (task.isDone()) {
          break;
        }
      }
    }

    if (lists.size() != messages.size()) {
      channel.sendMessage("Someone didn't fill their real tier list properly...").queue();
      return;
    }

    for(RealTierList list : lists) {
      bot.addRealTierList(list);
    }

    executor.shutdown();

    RealTierList joined = RealTierList.join(id, lists);
    bot.addRealTierList(joined);
    LOGGER.log(Level.INFO, "Final real tier list: \n" + joined.toString());
    channel.sendFile(pt.lzgpom.bot.util.bracket.Utils.bufferedImageToInputStream(
        ImageRealTierList.createImage(joined)), "realTierList.jpg")
        .queue();
    SaveLoader.saveCentre(bot);
    clear();
  }

  private synchronized RealTierList getRealTierList(MessageChannel channel, User user, String id,
      boolean preview, List<Long> messageIds) {
    MessageChannel privateChannel = user.openPrivateChannel().complete();

    List<ChallengerScore> scores = new ArrayList<>();

    for (Pair<Challenger, Long> message : messages.get(user)) {
      Challenger challenger = message.getKey();
      List<MessageReaction> reactions = privateChannel.getMessageById(message.getValue())
          .complete().getReactions();

      int tierPlace = -1;
      int placeWithinTier = -1;

      for (MessageReaction reaction : reactions) {
        if (reaction.getCount() > 1) {
          int tier = Utils.getTierPositionFromReaction(reaction.getReactionEmote().getName());

          if (tier != -1) {
            if (tierPlace != -1) {
              messageIds.add(channel.sendMessage(String
                  .format("%s putted %s in two different tiers", user.getName(),
                      challenger.getName())).complete().getIdLong());
              return null;
            } else {
              tierPlace = tier;
            }
          } else {
            int place = Utils.getReactionValue(reaction.getReactionEmote().getName());

            if (place != -1) {
              if (placeWithinTier != -1) {
                messageIds.add(channel.sendMessage(String
                    .format("%s putted %s in two different places within the same tier.",
                        user.getName(), challenger.getName())).complete().getIdLong());
                return null;
              } else {
                placeWithinTier = place;
              }
            }
          }
        }
      }

      if (tierPlace == -1) {
        messageIds.add(
            channel.sendMessage(String.format("%s didn't place %s in any tier.", user.getName(),
                message.getKey().getName())).complete().getIdLong());

        if (preview) {
          continue;
        }

        return null;
      }

      if (placeWithinTier != -1) {
        scores.add(new ChallengerScore(challenger, tierPlace, placeWithinTier));
      } else {
        scores.add(new ChallengerScore(challenger, tierPlace));
      }
    }

    Collections.shuffle(scores);
    Collections.sort(scores);
    return new RealTierList(id + "_" + user.getName(), scores, numTiers);
  }

  /**
   * Clears all the things for a fresh start of new real tier list.
   */
  public void clear() {
    for (Thread thread : threads) {
      thread.interrupt();
    }

    threads.clear();
    lists.clear();
    messages.clear();
    id = "";
    numTiers = 0;
    hasStarted = false;
  }

  private MessageEmbed challengerToMessage(Challenger challenger, Color color) {
    EmbedBuilder builder = new EmbedBuilder();
    builder.setColor(color);

    builder.addField(challenger.getName(),
        String.format("[... from %s](%s)", challenger.getExtraInfo(),
            challenger.getUrl()), true);

    return builder.build();
  }
}

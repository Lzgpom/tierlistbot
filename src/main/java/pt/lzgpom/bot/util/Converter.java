package pt.lzgpom.bot.util;

import java.awt.Color;
import java.util.Calendar;
import java.util.List;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import pt.lzgpom.bot.lib.Config;
import pt.lzgpom.bot.model.Group;
import pt.lzgpom.bot.model.Person;
import pt.lzgpom.bot.model.Score;
import pt.lzgpom.bot.model.TierList;
import pt.lzgpom.bot.model.Voter;

public class Converter {

  public static String createFinalScoresMessage(TierList tierList) {
    List<Score> scores = tierList.getFinalScores();

    StringBuilder out = new StringBuilder();

    //Setting the first place to compare with the others.
    int place = 1;
    int points = scores.get(0).getScore();
    out.append(String
        .format("%s %s - %d%n", Utils.getReactionInPos(place), scores.get(0).getPerson().getName(),
            points));

    for (int i = 1; i < scores.size(); i++) {
      Score score = scores.get(i);

      if (points != score.getScore()) {
        place++;
        points = score.getScore();
      }

      out.append(String
          .format("%s %s - %d%n", Utils.getReactionInPos(place), score.getPerson().getName(),
              points));
    }

    out.append(tierList.getUrl());

    return out.toString();
  }

  public static MessageEmbed votersToEmbededMessage(TierList tierList) {
    EmbedBuilder eb = new EmbedBuilder();
    eb.setTitle("Scores of " + tierList.getId(), null);
    eb.setColor(Color.YELLOW);
    eb.setAuthor("TierListBot", null, Config.ICON);
    List<Voter> voters = tierList.getVoterList();

    for (Person person : tierList.getGroup().getPeople()) {
      String fieldId = person.getName();
      String description = "";

      for (Voter voter : voters) {
        Score score = voter.getVote(person);

        if (score == null) {
          throw new IllegalArgumentException("Something wrong with the votes in this tier list.");
        }

        description += String
            .format("%s %s ", Utils.getReactionInPos(score.getScore()), voter.getName());
      }

      eb.addField(fieldId, description, false);
    }

    return eb.build();
  }

  public static MessageEmbed groupToMessage(Group group) {
    EmbedBuilder eb = new EmbedBuilder();
    eb.setTitle(group.getName());
    eb.setColor(Color.YELLOW);

    for (Person i : group.getPeople()) {
      Calendar cal = Calendar.getInstance();
      cal.setTime(i.getBirthDate());
      int year = cal.get(Calendar.YEAR);
      int month = cal.get(Calendar.MONTH) + 1;
      int day = cal.get(Calendar.DAY_OF_MONTH);
      eb.addField(i.getName(), String.format("Birthdate: %d/%d/%d", day, month, year), true);
    }

    return eb.build();
  }

  public static String peopleListToMessage(List<Person> people) {
    StringBuilder message = new StringBuilder();
    message.append("=============Results=============\n");

    for (int i = 0; i < people.size(); i++) {
      message
          .append(String.format("%s %s%n", Utils.getReactionInPos(i + 1), people.get(i).getName()));
    }

    return message.toString();
  }
}

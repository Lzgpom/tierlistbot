package pt.lzgpom.bot.model.realtierlist;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import pt.lzgpom.bot.model.Person;
import pt.lzgpom.bot.model.bracket.Challenger;
import pt.lzgpom.bot.util.Utils;

@XmlRootElement(name = "realtierlist")
@XmlAccessorType(XmlAccessType.FIELD)
public class RealTierList {

  static final int TIER_VALUE = 1000;

  private String id;
  private Map<String, TierPeople> tierList;

  protected RealTierList() {
    //Used for xml file.
  }

  public RealTierList(String id) {
    this.id = id;
    this.tierList = new LinkedHashMap<>();
    initiateMap();
  }

  public RealTierList(String id, List<ChallengerScore> scores) {
    this.id = id;
    this.tierList = new LinkedHashMap<>();
    initiateMap();
    fillMap(scores);
  }

  /**
   * Joins a collection of {@link RealTierList} into one.
   *
   * @param id The id of the new tierlist.
   * @param lists The {@link Collection} of {@link RealTierList}.
   * @return The new {@link RealTierList} created.
   */
  public static RealTierList join(String id, Collection<RealTierList> lists) {
    List<ChallengerScore> scores = new ArrayList<>();

    for (RealTierList list : lists) {

      int i = 0;
      for (TierPeople people : list.tierList.values()) {

        for (int j = 0; j < people.iterator().size(); j++) {
          ChallengerScore score = new ChallengerScore(people.iterator().get(j));
          int index = scores.indexOf(score);

          if (index == -1) {
            scores.add(score);
          } else {
            score = scores.get(index);
          }

          score.addToScore((i * TIER_VALUE) + j);
        }

        i++;
      }
    }

    scores.forEach(personScore -> personScore.average(lists.size()));
    Collections.shuffle(scores);
    Collections.sort(scores);

    return new RealTierList(id, scores);
  }

  private void fillMap(List<ChallengerScore> scores) {
    for (ChallengerScore score : scores) {
      int tier = score.getTier();

      String key = new ArrayList<>(this.tierList.keySet()).get(tier);
      this.tierList.get(key).add(score.getChallenger());
    }
  }

  public int getNumberOfTiers() {
    return tierList.size();
  }

  public int getMaxTierSize() {
    int max = -1;

    for (TierPeople list : tierList.values()) {
      if (max < list.iterator().size()) {
        max = list.iterator().size();
      }
    }

    return max;
  }

  public Map<String, TierPeople> map() {
    return this.tierList;
  }

  /**
   * Initiates the map with all tier available.
   */
  private void initiateMap() {
    for (String tier : Utils.getTiers().keySet()) {
      tierList.put(tier, new TierPeople());
    }
  }

  /**
   * Sums the total number of participants.
   * @return The total number of participants.
   */
  public int numberOfParticipants() {
    int total = 0;

    for(TierPeople tier : tierList.values()) {
      total += tier.iterator().size();
    }

    return total;
  }

  /**
   * Adds a person to a tier. <br/> The people should be added in order.
   *
   * @param tier The tier to add the {@link Challenger}.
   * @param person The {@link Person} to added.
   */
  public void addChallengerToTier(String tier, Challenger person) {
    tierList.get(tier).add(person);
  }

  /**
   * Returns the id of this {@link RealTierList}.
   *
   * @return the id of this {@link RealTierList}.
   */
  public String id() {
    return this.id;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();

    for (String tier : tierList.keySet()) {
      builder.append(tier).append(" -> ").append(tierList.get(tier).toString())
          .append(System.lineSeparator());
    }

    return builder.toString();
  }
}

package pt.lzgpom.bot.model.realtierlist;

import pt.lzgpom.bot.model.bracket.Challenger;

/**
 * This class is used to order peoples in a {@link RealTierList}.
 */
public class ChallengerScore implements Comparable<ChallengerScore> {

  private static final int MAX_SCORE = 100;

  private int score;
  private Challenger person;

  /**
   * This is used for people with no score associated.
   *
   * @param person The {@link Challenger}.
   */
  public ChallengerScore(Challenger person) {
    this.person = person;
    this.score = 0;
  }

  /**
   * This is used for people with a tier associated.
   *
   * @param person The {@link Challenger}.
   * @param tier In witch tier the challenger is.
   */
  public ChallengerScore(Challenger person, int tier) {
    this.person = person;
    this.score = (tier * RealTierList.TIER_VALUE) + MAX_SCORE;
  }

  /**
   * This is used for people with a tier and place associated.
   *
   * @param person The {@link Challenger}.
   * @param tier In witch tier the challenger is.
   * @param place In witch place the challenger is within the tier.
   */
  public ChallengerScore(Challenger person, int tier, int place) {
    this.person = person;
    this.score = (tier * RealTierList.TIER_VALUE) + place;
  }

  /**
   * Adds a value to the score.
   *
   * @param value The value to add.
   */
  public void addToScore(int value) {
    this.score += value;
  }

  /**
   * Averages the score.
   *
   * @param n The number to divide by.
   */
  public void average(int n) {
    this.score /= n;
  }

  /**
   * Returns the tier of this person.
   *
   * @return the tier of this person.
   */
  public int getTier() {
    return ((int) Math.round((double) score / RealTierList.TIER_VALUE));
  }

  /**
   * Returns the {@link Challenger} associated with this.
   *
   * @return The {@link Challenger} associated with this.
   */
  public Challenger getChallenger() {
    return this.person;
  }

  @Override
  public int hashCode() {
    return person.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }

    if (!(obj instanceof ChallengerScore)) {
      return false;
    }

    ChallengerScore other = (ChallengerScore) obj;

    return this.person.equals(other.person);
  }

  @Override
  public int compareTo(ChallengerScore other) {
    return this.score - other.score;
  }

  @Override
  public String toString() {
    return String.format("%s -> %d", person.getName(), score);
  }
}

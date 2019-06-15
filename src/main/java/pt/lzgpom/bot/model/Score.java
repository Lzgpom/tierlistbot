package pt.lzgpom.bot.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "voter")
@XmlAccessorType(XmlAccessType.FIELD)
public class Score implements Comparable<Score> {

  private final Person person;
  private int score;

  public Score() {
    this.person = null;
    this.score = 0;
  }

  Score(Person person) {
    this.person = person;
    this.score = 0;
  }

  Score(Person person, int score) {
    this.person = person;
    this.score = score;
  }

  public Person getPerson() {
    return this.person;
  }

  public int getScore() {
    return this.score;
  }

  void addToScore(int toAdd) {
    this.score += toAdd;
  }

  @Override
  public int hashCode() {
    int hash = person.hashCode();
    return hash * 31 + score;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }

    if (!(obj instanceof Score)) {
      return false;
    }

    Score other = (Score) obj;

    return this.person.equals(other.person) && this.score == other.score;
  }

  @Override
  public int compareTo(Score o) {
    return this.getScore() - o.getScore();
  }

  @Override
  public String toString() {
    return person.getName() + ":" + score;
  }
}

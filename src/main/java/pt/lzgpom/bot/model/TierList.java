package pt.lzgpom.bot.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "tierlist")
@XmlAccessorType(XmlAccessType.FIELD)
public class TierList {

  private String id;
  private final String url;
  private final Group group;

  @XmlElementWrapper(name = "voters")
  @XmlElement(name = "voter")
  private final List<Voter> voters;

  public TierList() {
    this.id = null;
    this.group = null;
    this.url = null;
    this.voters = new ArrayList<>();
  }

  public TierList(String id, Group group, String url) {
    this.id = id;
    this.group = group;
    this.url = url;
    this.voters = new ArrayList<>();
  }

  public String getId() {
    return this.id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getUrl() {
    return this.url;
  }

  public Group getGroup() {
    return this.group;
  }

  public int getNumberVoters() {
    return this.voters.size();
  }

  public Voter getVoterById(long id) {
    for (Voter i : voters) {
      if (i.getId() == id) {
        return i;
      }
    }

    return null;
  }

  public Voter getVoterByName(String name) {
    for (Voter i : voters) {
      if (i.getName().equalsIgnoreCase(name)) {
        return i;
      }
    }

    return null;
  }

  public boolean addVoter(Voter voter) {
    if (!voters.contains(voter)) {
      return voters.add(voter);
    }

    return false;
  }

  public List<Voter> getVoterList() {
    return this.voters;
  }

  public boolean validateVotes() {
    for (Voter voter : voters) {
      if (!voter.validateVotes(group)) {
        return false;
      }
    }

    return true;
  }

  public List<Score> getFinalScores() {
    List<Score> finalScores = new ArrayList<>();

    for (Voter voter : voters) {
      for (Score voterScore : voter.getVotes()) {
        getScoreByPerson(finalScores, voterScore.getPerson()).addToScore(voterScore.getScore());
      }
    }

    Collections.sort(finalScores);

    return finalScores;
  }

  private Score getScoreByPerson(List<Score> scores, Person person) {
    for (Score i : scores) {
      if (i.getPerson().equals(person)) {
        return i;
      }
    }

    Score score = new Score(person);

    scores.add(score);
    return score;
  }

  public void clearVoters() {
    for (Voter voter : voters) {
      voter.clearVotes();
    }
  }
}

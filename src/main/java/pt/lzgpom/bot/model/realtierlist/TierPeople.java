package pt.lzgpom.bot.model.realtierlist;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import pt.lzgpom.bot.model.Person;
import pt.lzgpom.bot.model.bracket.Challenger;

/**
 * This main use for this is for easier xml mapping.
 */
@XmlRootElement(name = "tier")
@XmlAccessorType(XmlAccessType.FIELD)
public class TierPeople {

  private List<Challenger> people;

  public TierPeople() {
    people = new ArrayList<>();
  }

  /**
   * Adds a person to the list. <br/> The person should be added in order.
   *
   * @param person The {@link Person} to be added.
   */
  public void add(Challenger person) {
    this.people.add(person);
  }

  public List<Challenger> iterator() {
    return this.people;
  }

  @Override
  public String toString() {
    return this.people.toString();
  }
}

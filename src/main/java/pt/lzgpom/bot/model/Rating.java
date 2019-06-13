package pt.lzgpom.bot.model;

public class Rating {

  private final int place;
  private final Person person;

  public Rating(int place, Person person) {
    this.place = place;
    this.person = person;
  }

  public int getPlace() {
    return this.place;
  }

  public Person getPerson() {
    return this.person;
  }

  @Override
  public int hashCode() {
    int hash = place;
    hash = hash * 31 + person.hashCode();
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }

    if (!(obj instanceof Rating)) {
      return false;
    }

    Rating other = (Rating) obj;

    return this.person.equals(other.person) && this.place == other.place;
  }
}

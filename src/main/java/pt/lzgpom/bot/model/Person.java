package pt.lzgpom.bot.model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "person")
@XmlAccessorType(XmlAccessType.FIELD)
public class Person implements Comparable<Person> {

  private final String name;
  private Date date;

  public Person() {
    this.name = "";
  }

  public Person(String name, String date) throws ParseException {
    this.name = name;
    setDate(date);
  }

  private void setDate(String date) throws ParseException {
    DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
    this.date = formatter.parse(date);
  }

  public String getName() {
    return this.name;
  }

  public Date getBirthDate() {
    return this.date;
  }

  @Override
  public int hashCode() {
    int hash = name.hashCode();
    return hash * 31 + date.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }

    if (!(obj instanceof Person)) {
      return false;
    }

    Person other = (Person) obj;

    return this.name.equals(other.getName()) && this.date.equals(other.date);
  }

  @Override
  public int compareTo(Person o) {
    return this.date.compareTo(o.getBirthDate());
  }

  @Override
  public String toString() {
    return String.format("Name: %s; Birthdate: %s", name, date.toString());
  }
}

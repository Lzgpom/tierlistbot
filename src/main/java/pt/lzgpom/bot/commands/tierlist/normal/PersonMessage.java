package pt.lzgpom.bot.commands.tierlist.normal;

import pt.lzgpom.bot.model.Person;

public class PersonMessage {

  private final Person person;
  private final long messageId;

  PersonMessage(Person person, long messageId) {
    this.person = person;
    this.messageId = messageId;
  }

  long getMessageId() {
    return this.messageId;
  }

  public Person getPerson() {
    return this.person;
  }
}

package pt.lzgpom.bot.model.realtierlist;

public class Tier {
  private String reaction;
  private String color;

  public Tier(String reaction, String color) {
    this.reaction = reaction;
    this.color = color;
  }

  public String getColor() {
    return this.color;
  }

  public String getReaction() {
    return this.reaction;
  }
}

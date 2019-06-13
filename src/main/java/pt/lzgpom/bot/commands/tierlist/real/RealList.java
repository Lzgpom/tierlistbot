package pt.lzgpom.bot.commands.tierlist.real;

import java.util.ArrayList;
import java.util.List;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;
import pt.lzgpom.bot.commands.ListCommandAdapter;
import pt.lzgpom.bot.model.Bot;
import pt.lzgpom.bot.model.realtierlist.RealTierList;

public class RealList extends ListCommandAdapter<RealTierList> {

  @Override
  public List<String> getCommands() {
    List<String> commands = new ArrayList<>();
    commands.add("reallist");
    commands.add("rl");
    return commands;
  }

  @Override
  public String getDescription() {
    return "Shows all the real tier lists in the bot in pages.";
  }

  @Override
  public List<RealTierList> getElements(Bot bot) {
    return bot.getRealTierLists();
  }

  @Override
  public Converter<RealTierList> getConverter() {
    return (element, pos) -> {
      String id = String.format("%d - %s", pos + 1, element.id());
      String description = String
          .format("Total of %d participants.", element.numberOfParticipants());
      return new Field(id, description, false);
    };
  }

  @Override
  public String listTitle() {
    return "List of real tier lists:";
  }
}

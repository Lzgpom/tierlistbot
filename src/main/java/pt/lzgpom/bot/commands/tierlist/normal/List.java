package pt.lzgpom.bot.commands.tierlist.normal;

import java.util.ArrayList;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;
import net.dv8tion.jda.core.entities.User;
import pt.lzgpom.bot.commands.ListCommandAdapter;
import pt.lzgpom.bot.commands.utils.General;
import pt.lzgpom.bot.lib.Config;
import pt.lzgpom.bot.model.Bot;
import pt.lzgpom.bot.model.TierList;

public class List extends ListCommandAdapter<TierList> {

  @Override
  public java.util.List<String> getCommands() {
    java.util.List<String> commands = new ArrayList<>();
    commands.add("list");
    return commands;
  }

  @Override
  public String getDescription() {
    return "Shows all tierlists avaiable.";
  }

  @Override
  public java.util.List<TierList> getElements(Bot bot) {
    return bot.getTierLists();
  }

  @Override
  public Converter<TierList> getConverter() {
    return (element, pos) -> {
      String id = String.format("%d - %s", pos + 1, element.getId());
      String description = String.format("Group: %s, Voters: %d.", element.getGroup().getName(),
          element.getNumberVoters());
      return new Field(id, description, false);
    };
  }

  @Override
  public String listTitle() {
    return "List of tier lists:";
  }
}

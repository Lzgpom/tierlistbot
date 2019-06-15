package pt.lzgpom.bot.commands.group;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;
import net.dv8tion.jda.core.entities.User;
import pt.lzgpom.bot.commands.Command;
import pt.lzgpom.bot.commands.ListCommandAdapter;
import pt.lzgpom.bot.commands.utils.General;
import pt.lzgpom.bot.lib.Config;
import pt.lzgpom.bot.model.Bot;
import pt.lzgpom.bot.model.Group;

public class GroupList extends ListCommandAdapter<Group> {

  @Override
  public List<String> getCommands() {
    java.util.List<String> commands = new ArrayList<>();
    commands.add("grouplist");
    commands.add("gl");
    return commands;
  }

  @Override
  public String getDescription() {
    return "Lists all the groups in the system.";
  }

  @Override
  public List<Group> getElements(Bot bot) {
    return bot.getGroups();
  }

  @Override
  public Converter<Group> getConverter() {
    return (element, pos) -> {
      String id = String.format("%d - %s", pos + 1, element.getName());
      String description = String.format("Number of People: %d.", element.getNumberOfPeople());

      return new Field(id, description, false);
    };
  }

  @Override
  public String listTitle() {
    return "List of groups:";
  }
}

package pt.lzgpom.bot.commands.group;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.User;
import pt.lzgpom.bot.commands.Command;
import pt.lzgpom.bot.lib.Config;
import pt.lzgpom.bot.model.Bot;
import pt.lzgpom.bot.model.Group;
import pt.lzgpom.bot.util.Converter;
import pt.lzgpom.bot.util.Utils;

public class GroupGet implements Command {

  public GroupGet() {

  }

  @Override
  public List<String> getCommands() {
    java.util.List<String> commands = new ArrayList<>();
    commands.add("getgroup");
    commands.add("gg");
    return commands;
  }

  @Override
  public String getDescription() {
    return "Retrieves a group and shows it.";
  }

  @Override
  public void run(String[] args, Bot bot, MessageChannel channel, User user) {
    try {
      Group group;

      if (Utils.isNumeric(args[0])) {
        int index = Integer.parseInt(args[0]) - 1;

        if (index < bot.getGroups().size()) {
          group = bot.getGroups().get(index);
        } else {
          channel.sendMessage("Invalid group index.").queue();
          return;
        }

      } else {
        group = bot.getGroupByName(args[0]);

        if (group == null) {
          channel.sendMessage("Invalid group id.").queue();
          return;
        }
      }

      channel.sendMessage(Converter.groupToMessage(group)).queue();
    } catch (IndexOutOfBoundsException e) {
      channel.sendMessage("Not enough arguments.").queue();
    }
  }

  @Override
  public MessageEmbed getHelpMessage() {
    EmbedBuilder eb = new EmbedBuilder();
    eb.setTitle(Config.PREFIX + getCommandName(), null);
    eb.setColor(Color.YELLOW);

    eb.addField("Description:", getDescription(), false);
    eb.addField("Usage:", getCommandName() + " <group id>", false);
    eb.addField("Example: ", getCommandName() + " Twice", false);

    return eb.build();
  }
}

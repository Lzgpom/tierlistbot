package pt.lzgpom.bot.commands.group;

import java.awt.Color;
import java.text.ParseException;
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
import pt.lzgpom.bot.model.Person;
import pt.lzgpom.bot.util.Converter;
import pt.lzgpom.bot.util.SaveLoader;

public class GroupAdd implements Command {

  public GroupAdd() {

  }

  @Override
  public List<String> getCommands() {
    java.util.List<String> commands = new ArrayList<>();
    commands.add("addtogroup");
    commands.add("atg");
    return commands;
  }

  @Override
  public String getDescription() {
    return "Adds a person to the the group.";
  }

  @Override
  public void run(String[] args, Bot bot, MessageChannel channel, User user) {
    try {
      String groupName = args[0];

      if (bot.validateGroupName(groupName)) {
        channel.sendMessage("There is not a group with that name.").queue();
        return;
      }

      Person person = new Person(args[1], args[2]);
      Group group = bot.getGroupByName(groupName);
      group.addPerson(person);
      group.orderGroupByAge();

      channel.sendMessage(String.format("Added %s to %s", person.getName(), group.getName()))
          .queue();
      channel.sendMessage(Converter.groupToMessage(group)).queue();
      SaveLoader.saveCentre(bot);
    } catch (IndexOutOfBoundsException e) {
      channel.sendMessage("Invalid number of parameters!").queue();
    } catch (ParseException e) {
      channel.sendMessage("Invalid birthdate!").queue();
    }
  }

  @Override
  public MessageEmbed getHelpMessage() {
    EmbedBuilder eb = new EmbedBuilder();
    eb.setTitle(Config.PREFIX + getCommandName(), null);
    eb.setColor(Color.YELLOW);

    eb.addField("Description:", getDescription(), false);
    eb.addField("Usage:", getCommandName() + " <group name> <person name> <birth date>", false);
    eb.addField("Example: ", getCommandName() + " Twice Tzuyu 14/7/1999", false);

    return eb.build();
  }
}

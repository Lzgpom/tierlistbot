package pt.lzgpom.bot.commands.tierlist.normal;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.User;
import pt.lzgpom.bot.commands.Command;
import pt.lzgpom.bot.commands.utils.General;
import pt.lzgpom.bot.lib.Config;
import pt.lzgpom.bot.model.Bot;
import pt.lzgpom.bot.model.Group;

public class StartTierList implements Command {

  public StartTierList() {

  }

  @Override
  public List<String> getCommands() {
    java.util.List<String> commands = new ArrayList<>();
    commands.add("tierlist");
    commands.add("tl");
    return commands;
  }

  @Override
  public String getDescription() {
    return "It is used to start a tierlist.";
  }

  @Override
  public void run(String[] args, Bot bot, MessageChannel channel, Member user) {
    if (bot.getTierListManager().hasTierListStarted()) {
      channel.sendMessage("There is a tierlist in making already.").queue();
      return;
    }

    if (args.length < 3) {
      channel.sendMessage("Not enough arguments!").queue();
      return;
    }

    Group group = bot.getGroupByName(args[0]);

    if (group == null) {
      channel.sendMessage("Invalid group name!").queue();
      return;
    }

    if (bot.hasTierListWithId(args[1])) {
      channel.sendMessage("There is a tier already with that id.").queue();
      return;
    }

    List<User> voters = General.getVotersFromMessage(channel, null);

    if (voters.size() == 0) {
      channel.sendMessage("No one wanted to vote...").queue();
      return;
    }

    channel.sendMessage("Time's up, check your pm's.").queue();

    bot.getTierListManager().start(voters, args[1], group, args[2]);
  }

  @Override
  public MessageEmbed getHelpMessage() {
    EmbedBuilder eb = new EmbedBuilder();
    eb.setTitle(Config.PREFIX + "tierlist", null);
    eb.setColor(Color.YELLOW);

    eb.addField("Description:", "Starts the creation of a tierlist.", false);
    eb.addField("Usage:", getCommandName() + " <group id> <tierlist id> <url>", false);
    eb.addField("Example: ",
        getCommandName() + " Twice TWICE_NEW_MV https://www.youtube.com/watch?v=Fm5iP0S1z9w",
        false);

    return eb.build();
  }
}

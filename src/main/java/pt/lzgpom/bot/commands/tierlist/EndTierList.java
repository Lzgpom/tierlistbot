package pt.lzgpom.bot.commands.tierlist;

import java.util.ArrayList;
import java.util.List;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.User;
import pt.lzgpom.bot.commands.Command;
import pt.lzgpom.bot.commands.utils.General;
import pt.lzgpom.bot.model.Bot;

public class EndTierList implements Command {

  public EndTierList() {

  }

  @Override
  public List<String> getCommands() {
    java.util.List<String> commands = new ArrayList<>();
    commands.add("end");
    return commands;
  }

  @Override
  public String getDescription() {
    return "Terminates the tierlist in progress.";
  }

  @Override
  public void run(String[] args, Bot bot, MessageChannel channel, Member user) {

    if (bot.getTierListManager().hasTierListStarted()) {
      channel.sendMessage("Calculating scores...").queue();
      bot.getTierListManager().end(channel);
      return;
    }

    if(bot.getRealTierManager().isRealTierListStarted()) {
      channel.sendMessage("Calculating scores...").queue();
      bot.getRealTierManager().end(channel, user.getGuild());
    }

  }

  @Override
  public MessageEmbed getHelpMessage() {
    return General.buildHelpMessage(this);
  }
}

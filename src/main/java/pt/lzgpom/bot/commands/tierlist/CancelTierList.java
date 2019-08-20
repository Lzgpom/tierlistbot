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

public class CancelTierList implements Command {

  public CancelTierList() {

  }

  @Override
  public List<String> getCommands() {
    java.util.List<String> commands = new ArrayList<>();
    commands.add("cancel");
    return commands;
  }

  @Override
  public String getDescription() {
    return "Cancels the on going tierlist.";
  }

  @Override
  public void run(String[] args, Bot bot, MessageChannel channel, Member user) {
    if (bot.getTierListManager().hasTierListStarted()) {
      bot.getTierListManager().clear();
      channel.sendMessage("The tierlist was canceled.").queue();
      return;
    }

    if (bot.getRealTierManager().isRealTierListStarted()) {
      bot.getRealTierManager().clear();
      channel.sendMessage("The real tier list was canceled.").queue();
      return;
    }

    channel.sendMessage("There is not an on going tierlist.").queue();
  }

  @Override
  public MessageEmbed getHelpMessage() {
    return General.buildHelpMessage(this);
  }
}

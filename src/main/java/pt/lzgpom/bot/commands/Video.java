package pt.lzgpom.bot.commands;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.User;
import pt.lzgpom.bot.commands.utils.General;
import pt.lzgpom.bot.lib.Config;
import pt.lzgpom.bot.model.Bot;
import pt.lzgpom.bot.util.Utils;

public class Video implements Command {

  public Video() {

  }

  @Override
  public List<String> getCommands() {
    List<String> commands = new ArrayList<>();
    commands.add("video");
    return commands;
  }

  @Override
  public String getDescription() {
    return "Shows a random video.";
  }

  @Override
  public void run(String[] args, Bot bot, MessageChannel channel, User user) {
    try {
      channel.sendMessage(Utils.getRandomVideo()).queue();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  @Override
  public MessageEmbed getHelpMessage() {
    return General.buildHelpMessage(this);
  }

}

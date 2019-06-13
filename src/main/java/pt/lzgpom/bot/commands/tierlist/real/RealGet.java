package pt.lzgpom.bot.commands.tierlist.real;

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
import pt.lzgpom.bot.model.realtierlist.RealTierList;
import pt.lzgpom.bot.util.Utils;
import pt.lzgpom.bot.util.real.ImageRealTierList;

public class RealGet implements Command {

  @Override
  public List<String> getCommands() {
    List<String> commands = new ArrayList<>();
    commands.add("realget");
    commands.add("rget");
    return commands;
  }

  @Override
  public String getDescription() {
    return "Retrieves a real tier list from the bot.";
  }

  @Override
  public void run(String[] args, Bot bot, MessageChannel channel, User user) {
    try {
      RealTierList list;

      if (Utils.isNumeric(args[0])) {
        int index = Integer.parseInt(args[0]) - 1;

        if (index < bot.getGroups().size()) {
          list = bot.getRealTierLists().get(index);
        } else {
          channel.sendMessage("Invalid real tier list index.").queue();
          return;
        }

      } else {
        list = bot.getRealTierListById(args[0]);

        if (list == null) {
          channel.sendMessage("Invalid real tier list id.").queue();
          return;
        }
      }

      channel.sendFile(pt.lzgpom.bot.util.bracket.Utils.bufferedImageToInputStream(
          ImageRealTierList.createImage(list)), "realTierList.jpg")
          .queue();
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
    eb.addField("Usage:", getCommandName() + " <real tier list id>\n" +
        getCommandName() + "<index>", false);
    eb.addField("Example: ", getCommandName() + " Twice", false);

    return eb.build();
  }
}

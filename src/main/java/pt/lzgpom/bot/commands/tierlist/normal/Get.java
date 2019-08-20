package pt.lzgpom.bot.commands.tierlist.normal;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.User;
import pt.lzgpom.bot.commands.Command;
import pt.lzgpom.bot.lib.Config;
import pt.lzgpom.bot.model.Bot;
import pt.lzgpom.bot.model.TierList;
import pt.lzgpom.bot.util.Converter;
import pt.lzgpom.bot.util.Utils;

public class Get implements Command {

  private static final String SHOW_VOTES_PARAM = "show";

  public Get() {

  }

  @Override
  public List<String> getCommands() {
    java.util.List<String> commands = new ArrayList<>();
    commands.add("get");
    return commands;
  }

  @Override
  public String getDescription() {
    return "Retrieves a tierlist and shows it.";
  }

  @Override
  public void run(String[] args, Bot bot, MessageChannel channel, Member user) {
    try {
      TierList list;

      if (Utils.isNumeric(args[0])) {
        int index = Integer.parseInt(args[0]) - 1;

        if (index < bot.getTierLists().size()) {
          list = bot.getTierLists().get(index);
        } else {
          channel.sendMessage("Invalid Tier List index.").queue();
          return;
        }

      } else {
        list = bot.getTierListById(args[0]);

        if (list == null) {
          channel.sendMessage("Invalid Tier List id.").queue();
          return;
        }
      }

      if (args.length > 1) {
        if (args[1].equals(SHOW_VOTES_PARAM)) {
          channel.sendMessage(Converter.votersToEmbededMessage(list)).queue();
        }
      }

      channel.sendMessage(Converter.createFinalScoresMessage(list)).queue();
    } catch (IndexOutOfBoundsException e) {
      channel.sendMessage("Not enough arguments.").queue();
    }

  }

  @Override
  public MessageEmbed getHelpMessage() {
    EmbedBuilder eb = new EmbedBuilder();
    eb.setTitle(Config.PREFIX + "get", null);
    eb.setColor(Color.YELLOW);

    eb.addField("Description:", "Retrieves a tierlist and shows it.\n"
        + "Show: shows the places that the voters placed the menbers of the group.", false);
    eb.addField("Usage:", getCommandName() + " <tierlist id>", false);
    eb.addField("Example: ", getCommandName() + " TWICE_NEW_MV\n" +
        getCommandName() + " TWICE_NEW_MV show", false);

    return eb.build();
  }
}

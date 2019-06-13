package pt.lzgpom.bot.commands;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.User;
import pt.lzgpom.bot.lib.Config;
import pt.lzgpom.bot.model.Bot;

public class Help implements Command {

  private static final String INVALID_PAGE_NUMBER = "Invalid page number.";

  public Help() {

  }

  @Override
  public List<String> getCommands() {
    java.util.List<String> commands = new ArrayList<>();
    commands.add("help");
    return commands;
  }

  @Override
  public String getDescription() {
    return "This command is used to get help...";
  }

  @Override
  public void run(String[] args, Bot bot, MessageChannel channel, User user) {
    int totalPages = (int) Math
        .ceil((double) bot.getCommandManager().getCommandList().size() / Config.LISTS_PER_PAGE);
    int page = 1;

    if (args.length > 0) {
      try {
        page = Integer.parseInt(args[0]);

        if (page > totalPages || page < 1) {
          channel.sendMessage(INVALID_PAGE_NUMBER).queue();
          return;
        }
      } catch (NumberFormatException e) {
        Command command = bot.getCommandManager().getCommandById(args[0]);

        if (command != null) {
          channel.sendMessage(command.getHelpMessage()).queue();
          return;
        } else {
          channel.sendMessage("There is not such a command.").queue();
          return;
        }
      }
    }

    EmbedBuilder eb = new EmbedBuilder();

    eb.setTitle("List of commands:", null);
    eb.setColor(Color.YELLOW);

    int min = Config.LISTS_PER_PAGE * (page - 1);
    int max = Config.LISTS_PER_PAGE * page > bot.getCommandManager().getCommandList().size()
        ? bot.getCommandManager().getCommandList().size() : Config.LISTS_PER_PAGE * page;

    for (int i = min; i < max; i++) {
      Command command = bot.getCommandManager().getCommandList().get(i);
      String id = String.format("%d - %s", i + 1, command.getCommandName());
      String description = command.getDescription();
      eb.addField(id, description, false);
    }

    eb.setAuthor("TierListBot", null, Config.ICON);
    eb.setFooter(String.format("Page %d/%d", page, totalPages), null);

    channel.sendMessage(eb.build()).queue();
  }

  @Override
  public MessageEmbed getHelpMessage() {
    EmbedBuilder eb = new EmbedBuilder();
    eb.setTitle(Config.PREFIX + "help", null);
    eb.setColor(Color.YELLOW);

    eb.addField("Description:", "Shows how to use the commands", false);
    eb.addField("Usage:", getCommandName() + "\n" + getCommandName() + " <command>", false);
    eb.addField("Example: ", getCommandName() + " tierlist", false);

    return eb.build();
  }
}

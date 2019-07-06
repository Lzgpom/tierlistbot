package pt.lzgpom.bot.commands.tierlist.real;

import static pt.lzgpom.bot.lib.Config.FILE_MODIFIER;
import static pt.lzgpom.bot.lib.Config.GROUPS_MODIFIER;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.User;
import pt.lzgpom.bot.commands.Command;
import pt.lzgpom.bot.commands.utils.General;
import pt.lzgpom.bot.lib.Config;
import pt.lzgpom.bot.model.Bot;
import pt.lzgpom.bot.model.Group;
import pt.lzgpom.bot.model.bracket.Challenger;
import pt.lzgpom.bot.util.bracket.Utils;

public class StartRealTierList implements Command {

  @Override
  public List<String> getCommands() {
    List<String> commands = new ArrayList<>();
    commands.add("realtierlist");
    commands.add("rtl");
    return commands;
  }

  @Override
  public String getDescription() {
    return "It is used to start a REAL tier list.";
  }

  @Override
  public void run(String[] args, Bot bot, MessageChannel channel, User user) {
    if (bot.getRealTierManager().isRealTierListStarted()) {
      channel.sendMessage("There's already a real tier list going...").queue();
      return;
    }

    if (args.length < 4) {
      channel.sendMessage("Not enough arguments...").queue();
      return;
    }

    if (bot.hasRealTierListWithId(args[0])) {
      channel.sendMessage("There's already a real tier list with that id.").queue();
      return;
    }

    int numTiers;

    try {
      numTiers = Integer.parseInt(args[1]);
      if(numTiers < 1 || numTiers > pt.lzgpom.bot.util.Utils.getTiers().size()) {
        channel.sendMessage("Number of tiers too small or too big...").queue();
        return;
      }
    } catch (NumberFormatException e) {
      channel.sendMessage("Invalid number of tiers.").queue();
      return;
    }

    List<Challenger> challengers;

    if (args[2].equalsIgnoreCase(FILE_MODIFIER)) {
      challengers = Utils.readChallengersFile(args[3]);

      if (challengers.isEmpty()) {
        channel.sendMessage("Invalid file or file is empty.").queue();
        return;
      }

    } else if (args[2].equalsIgnoreCase(GROUPS_MODIFIER)) {
      List<Group> groups = new ArrayList<>();

      for (int i = 3; i < args.length; i++) {
        Group group = bot.getGroupByName(args[i]);

        if (group == null) {
          channel.sendMessage("Group " + args[i] + " doesn't exist...").queue();
        } else {
          groups.add(group);
        }
      }

      if (groups.isEmpty()) {
        channel.sendMessage("There were imputed 0 valid groups...").queue();
        return;
      }

      challengers = Utils.convertGroupsIntoChallengers(groups);
    } else {
      channel.sendMessage("Unknown modifier..").queue();
      return;
    }

    List<User> voters = General.getVotersFromMessage(channel);

    if (voters.isEmpty()) {
      channel.sendMessage("Not enough participants").queue();
      return;
    }

    bot.getRealTierManager().start(voters, challengers, args[0], numTiers);
  }

  @Override
  public MessageEmbed getHelpMessage() {
    EmbedBuilder eb = new EmbedBuilder();
    eb.setTitle(Config.PREFIX + "realtierlist", null);
    eb.setColor(Color.YELLOW);

    eb.addField("Description:", getDescription(), false);
    eb.addField("Usage:", getCommandName() + " <id> <number_tiers> -g <group_id>.. \n"
        + "getCommandName() + \" <id> <number_tiers> -f <filename>", false);
    eb.addField("Example: ",
        getCommandName() + " Twice TWICE_NEW_MV https://www.youtube.com/watch?v=Fm5iP0S1z9w",
        false);

    return eb.build();
  }
}

package pt.lzgpom.bot.commands.utils;

import java.awt.Color;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.User;
import pt.lzgpom.bot.commands.Command;
import pt.lzgpom.bot.lib.Config;

public class General {

  public static MessageEmbed buildHelpMessage(Command command) {
    EmbedBuilder eb = new EmbedBuilder();
    eb.setTitle(Config.PREFIX + command.getCommandName(), null);
    eb.setColor(Color.YELLOW);

    eb.addField("Description:", command.getDescription(), false);
    eb.addField("Usage:", command.getCommandName(), false);
    eb.addField("Example: ", command.getCommandName(), false);

    return eb.build();
  }

  public static MessageEmbed buildListHelpMessage(Command command) {
    EmbedBuilder eb = new EmbedBuilder();
    eb.setTitle(Config.PREFIX + command.getCommandName(), null);
    eb.setColor(Color.YELLOW);

    eb.addField("Description:", command.getDescription(), false);
    eb.addField("Usage:",
        command.getCommandName() + "\n" + command.getCommandName() + " <page number>", false);
    eb.addField("Example: ", command.getCommandName() + " 2", false);

    return eb.build();
  }

  public static List<User> getVotersFromMessage(MessageChannel channel, Map<User, Color> userColors) {
    Message message = channel.sendMessage("React to be a voter!").complete();
    long id = message.getIdLong();
    message.addReaction("🤚").queue();
    Guild guild = message.getGuild();

    try {
      TimeUnit.SECONDS.sleep(Config.TIME_TO_REACT);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    List<User> voters = channel.getMessageById(id).complete().getReactions().get(0).getUsers()
        .complete();
    voters.remove(voters.size() - 1);

    if(userColors != null) {
      for(User user : voters) {
        userColors.put(user, guild.getMember(user).getColor());
      }
    }

    return voters;
  }
}

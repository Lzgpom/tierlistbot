package pt.lzgpom.bot.commands;

import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.User;
import pt.lzgpom.bot.model.Bot;

public interface Command {

  /**
   * Returns a the name of the command.
   *
   * @return a the name of the command.
   */
  default String getCommandName() {
    return getCommands().get(0);
  }

  /**
   * Returns a list of all available options for the command.
   *
   * @return a list of all available options for the command.
   */
  java.util.List<String> getCommands();

  /**
   * Returns a brief description of the command.
   *
   * @return a brief description of the command.
   */
  String getDescription();

  /**
   * Runs the command.
   *
   * @param args The arguments passed to the command.
   * @param bot The bot with all the info.
   * @param channel The channel where the message was sent.
   * @param user The author of the message.
   */
  void run(String[] args, Bot bot, MessageChannel channel, User user);

  /**
   * Returns a message with the way the command is used.
   *
   * @return a message with the way the command is used.
   */
  MessageEmbed getHelpMessage();
}

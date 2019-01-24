package pt.lzgpom.bot.commands;

import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import pt.lzgpom.bot.model.Bot;

public interface Command 
{
	/**
	 * Returns a the name of the command.
	 * 
	 * @return a the name of the command.
	 */
	public default String getCommandName()
	{
		return getCommands().get(0);
	}
	
	/**
	 * Returns a list of all available options for the command.
	 * 
	 * @return a list of all available options for the command.
	 */
	public java.util.List<String> getCommands();
	
	/**
	 * Returns a brief description of the command.
	 * 
	 * @return a brief description of the command.
	 */
	public String getDescription();
	
	/**
	 * Runs the command.
	 * 
	 * @param args The arguments passed to the command.
	 * @param bot The bot with all the info.
	 * @param channel The channel where the message was sent.
	 */
	public void run(String[] args, Bot bot, MessageChannel channel);
	
	/**
	 * Returns a message with the way the command is used.
	 * 
	 * @return a message with the way the command is used.
	 */
	public MessageEmbed getHelpMessage();
}

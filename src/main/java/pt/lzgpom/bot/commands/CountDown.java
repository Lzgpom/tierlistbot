package pt.lzgpom.bot.commands;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import pt.lzgpom.bot.lib.Config;
import pt.lzgpom.bot.model.Bot;

public class CountDown implements Command
{
	public CountDown()
	{
		
	}

	@Override
	public List<String> getCommands() 
	{
		List<String> commands = new ArrayList<>();
		commands.add("countdown");
		commands.add("cd");
		return null;
	}

	@Override
	public String getDescription() 
	{
		return "Makes a countdown.";
	}

	@Override
	public void run(String[] args, Bot bot, MessageChannel channel) 
	{
		
	}

	@Override
	public MessageEmbed getHelpMessage() 
	{
		EmbedBuilder eb = new EmbedBuilder();
		eb.setTitle(Config.PREFIX + getCommandName(), null);
		eb.setColor(Color.YELLOW);
		
		eb.addField("Description:", getDescription(), false);
		eb.addField("Usage:", getCommandName() + " <time>", false);
		eb.addField("Example: ", getCommandName() + " 4", false);
		
		return eb.build();
	}
}

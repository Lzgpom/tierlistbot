package pt.lzgpom.bot.commands;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import pt.lzgpom.bot.lib.Config;
import pt.lzgpom.bot.model.Bot;
import pt.lzgpom.bot.util.Utils;

public class Video implements Command
{
	public Video()
	{
		
	}
	
	@Override
	public List<String> getCommands()
	{
		List<String> commands = new ArrayList<>();
		commands.add("video");
		return commands;
	}

	@Override
	public String getDescription()
	{
		return "Shows a random video.";
	}

	@Override
	public void run(String[] args, Bot bot, MessageChannel channel)
	{
		try 
		{
			channel.sendMessage(Utils.getRandomVideo()).queue();
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}
	}

	@Override
	public MessageEmbed getHelpMessage() 
	{
		EmbedBuilder eb = new EmbedBuilder();
		eb.setTitle(Config.PREFIX + getCommandName(), null);
		eb.setColor(Color.YELLOW);
		
		eb.addField("Description:", getDescription(), false);
		eb.addField("Usage:", getCommandName(), false);
		eb.addField("Example: ", getCommandName(), false);
		
		return eb.build();
	}
	
}

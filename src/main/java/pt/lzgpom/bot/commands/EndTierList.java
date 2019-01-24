package pt.lzgpom.bot.commands;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import pt.lzgpom.bot.lib.Config;
import pt.lzgpom.bot.model.Bot;

public class EndTierList implements Command
{
	public EndTierList()
	{
		
	}
	
	@Override
	public List<String> getCommands() 
	{
		java.util.List<String> commands = new ArrayList<>();
		commands.add("end");
		return commands;
	}
	
	@Override
	public String getDescription()
	{
		return "Terminates the tierlist in progress.";
	}

	@Override
	public void run(String[] args, Bot bot, MessageChannel channel)
	{
		channel.sendMessage("Calculating scores...").queue();
		
		if(!bot.getTierListManager().hasTierListStarted())
		{
			channel.sendMessage("There is no tierlist in making.").queue();
			return;
		}
		
		bot.getTierListManager().end(channel);
		
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

package pt.lzgpom.bot.commands.tierlist;

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

public class CancelTierList implements Command
{
	public CancelTierList()
	{
		
	}
	
	@Override
	public List<String> getCommands() 
	{
		java.util.List<String> commands = new ArrayList<>();
		commands.add("cancel");
		return commands;
	}

	@Override
	public String getDescription() 
	{
		return "Cancels the on going tierlist.";
	}

	@Override
	public void run(String[] args, Bot bot, MessageChannel channel, User user)
	{
		if(bot.getTierListManager().hasTierListStarted())
		{
			bot.getTierListManager().clear();
			channel.sendMessage("The tierlist was canceled.").queue();
			return;
		}
		
		channel.sendMessage("There is not an on going tierlist.").queue();
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

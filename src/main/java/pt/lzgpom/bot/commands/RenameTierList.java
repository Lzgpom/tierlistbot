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
import pt.lzgpom.bot.model.TierList;
import pt.lzgpom.bot.util.SaveLoader;

public class RenameTierList implements Command
{
	public RenameTierList()
	{
		
	}
	
	@Override
	public List<String> getCommands() 
	{
		java.util.List<String> commands = new ArrayList<>();
		commands.add("rename");
		return commands;
	}

	@Override
	public String getDescription() 
	{
		return "Changes the id of the tierlist.";
	}

	@Override
	public void run(String[] args, Bot bot, MessageChannel channel, User user)
	{
		if(args.length >= 2)
		{
			TierList tierlist = bot.getTierListById(args[0]);
			
			if(tierlist == null)
			{
				channel.sendMessage("There is no tierlist with such id").queue();
				return;
			}
			
			if(bot.hasTierListWithId(args[1]))
			{
				channel.sendMessage("There is a tier already with that id.").queue();
				return;
			}
			
			tierlist.setId(args[1]);
			SaveLoader.saveCentre(bot);
			channel.sendMessage(String.format("Change tierlist id from %s to %s", args[0], args[1])).queue();
		}
		else
		{
			channel.sendMessage("Not enough arguments.").queue();
		}
	}

	@Override
	public MessageEmbed getHelpMessage()
	{
		EmbedBuilder eb = new EmbedBuilder();
		eb.setTitle(Config.PREFIX + getCommandName(), null);
		eb.setColor(Color.YELLOW);
			
		eb.addField("Description:", getDescription(), false);
		eb.addField("Usage:", getCommandName() + " <tierlist id> <new tierlist id>", false);
		eb.addField("Example: ", getCommandName() + " TWICE_MV TWICE_PHOTOS", false);
			
		return eb.build();
	}
}

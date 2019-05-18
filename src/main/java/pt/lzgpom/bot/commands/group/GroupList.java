package pt.lzgpom.bot.commands.group;

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
import pt.lzgpom.bot.model.Group;

public class GroupList implements Command
{
	private static final String INVALID_PAGE_NUMBER = "Invalid page number.";
	
	public GroupList()
	{
		
	}

	@Override
	public List<String> getCommands() 
	{
		java.util.List<String> commands = new ArrayList<>();
		commands.add("grouplist");
		commands.add("gl");
		return commands;
	}

	@Override
	public String getDescription() 
	{
		return "Lists all the groups in the system.";
	}

	@Override
	public void run(String[] args, Bot bot, MessageChannel channel, User user)
	{
		int totalPages = (int) Math.ceil((double)bot.getGroups().size() / Config.LISTS_PER_PAGE);
		int page = 1;
		
		if(args.length > 0)
		{
			try
			{
				page = Integer.parseInt(args[0]);
				
				if(page > totalPages || page < 1)
				{
					channel.sendMessage(INVALID_PAGE_NUMBER).queue();
					return;
				}
			}
			catch(NumberFormatException e)
			{
				channel.sendMessage(INVALID_PAGE_NUMBER).queue();
				return;
			}
		}
		
		EmbedBuilder eb = new EmbedBuilder();

		eb.setTitle("List of groups:", null);
		eb.setColor(Color.YELLOW);
		
		int min = Config.LISTS_PER_PAGE * (page - 1);
		int max = Config.LISTS_PER_PAGE * page > bot.getGroups().size() ? bot.getGroups().size() : Config.LISTS_PER_PAGE * page;
		
		for(int i = min; i < max; i++)
		{
			Group group = bot.getGroups().get(i);
			String id = String.format("%d - %s", i + 1, group.getName());
			String description = String.format("Number of People: %d.", group.getNumberOfPeople());
			eb.addField(id, description, false);
		}
		
		eb.setAuthor("TierListBot", null, Config.ICON);
		eb.setFooter(String.format("Page %d/%d", page, totalPages), null);
		
		channel.sendMessage(eb.build()).queue();
	}

	@Override
	public MessageEmbed getHelpMessage()
	{
		EmbedBuilder eb = new EmbedBuilder();
		eb.setTitle(Config.PREFIX + getCommandName(), null);
		eb.setColor(Color.YELLOW);
		
		eb.addField("Description:", getDescription() , false);
		eb.addField("Usage:", getCommandName() + "\n" + getCommandName() + " <page number>", false);
		eb.addField("Example: ", getCommandName() + " 2", false);
		
		return eb.build();
	}
}

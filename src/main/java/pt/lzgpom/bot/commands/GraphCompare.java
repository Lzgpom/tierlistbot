package pt.lzgpom.bot.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.googlecode.charts4j.AxisLabels;
import com.googlecode.charts4j.AxisLabelsFactory;
import com.googlecode.charts4j.AxisStyle;
import com.googlecode.charts4j.AxisTextAlignment;
import com.googlecode.charts4j.Color;
import com.googlecode.charts4j.DataUtil;
import com.googlecode.charts4j.Fills;
import com.googlecode.charts4j.GCharts;
import com.googlecode.charts4j.Line;
import com.googlecode.charts4j.LineChart;
import com.googlecode.charts4j.LineStyle;
import com.googlecode.charts4j.Plot;
import com.googlecode.charts4j.Plots;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.User;
import pt.lzgpom.bot.lib.Config;
import pt.lzgpom.bot.model.Bot;
import pt.lzgpom.bot.model.Group;
import pt.lzgpom.bot.model.Person;
import pt.lzgpom.bot.model.TierList;
import pt.lzgpom.bot.model.Voter;
import pt.lzgpom.bot.util.Utils;

public class GraphCompare implements Command
{
	public GraphCompare()
	{
		
	}
	
	@Override
	public List<String> getCommands() 
	{
		java.util.List<String> commands = new ArrayList<>();
		commands.add("compare");
		return commands;
	}

	@Override
	public String getDescription() 
	{
		return "Makes a graph that compares the voter's scores in a tierlist.";
	}

	@Override
	public void run(String[] args, Bot bot, MessageChannel channel, User user)
	{
		try
		{
			HashMap<String, String> colors = Utils.readColors();
			
			TierList tierlist = bot.getTierListById(args[0]);
			
			if(tierlist == null)
			{
				channel.sendMessage("The tierlist id is invalid.").queue();
				return;
			}
			
			List<String> votersName = new ArrayList<>();
			
			if(args.length == 1)
			{
				for(Voter voter : tierlist.getVoterList())
				{
					votersName.add(voter.getName());
				}
			}
			
			else
			{
				//Gets the voters inputed.
				for(int i = 1; i < args.length; i++)
				{
					if(tierlist.getVoterByName(args[1]) != null)
					{
						votersName.add(args[i]);
					}
					
					else
					{
						channel.sendMessage(String.format("%s didn't vote on this tierlist...", args[i])).queue();
					}
				}
			}
			
			if(votersName.size() <= 1)
			{
				channel.sendMessage("Not enough voters to create graph.").queue();
				return;
			}
						
			//The List for Lines.
			List<Plot> lines = new ArrayList<>();
			
			//Gets the group. Since the group is the same for all tier list, I get from the first one.
			Group group = tierlist.getGroup();
			
			//Creates the lines for the graph.
			for(Person person : group.getPeople())
			{
				List<Integer> scores = new ArrayList<>();
				
				for(String voterName : votersName)
				{
					Voter voter = tierlist.getVoterByName(voterName);
						
					if(voter == null) 
					{
						System.out.println(voterName);
						channel.sendMessage(String.format("The user %s didnt vote in %s.", voterName, tierlist.getId())).queue();
						return;
					}
						
					scores.add(voter.getVote(person).getScore());
				}
				
				lines.add(createLine(person, scores, group.getNumberOfPeople(), colors));
			}
				
			LineChart chart = createLineChart(lines, votersName, group.getNumberOfPeople(), tierlist);
			channel.sendMessage(chart.toURLString()).queue();		
		}
		catch(IndexOutOfBoundsException e)
		{
			channel.sendMessage("Invalid number of parameters!").queue();
		}
		
	}

	@Override
	public MessageEmbed getHelpMessage() 
	{
		EmbedBuilder eb = new EmbedBuilder();
		eb.setTitle(Config.PREFIX + getCommandName(), null);
		eb.setColor(java.awt.Color.YELLOW);
		
		eb.addField("Description:", getDescription() , false);
		eb.addField("Usage:", getCommandName() + " <tier list>" + "\n" + 
				getCommandName() + " <tier list> <voter name> <voter name> ...", false);
		eb.addField("Example: ", getCommandName() + " TWICE_MV\n"
				+ getCommandName() + " TWICE_MV solteiro256 Lzgpom", false);
		
		return eb.build();
	}
	
	/**
	 * Creates a line for the graph.
	 * @param legend The person of the line.
	 * @param scores The scores.
	 * @return The new Plot line created.
	 */
	private Line createLine(Person person, List<? extends Number> scores, int max, HashMap<String, String> colors)
	{
		Line line = Plots.newLine(DataUtil.scaleWithinRange(1, max, scores));
		
		String color = colors.get(person.getName());
		
		if(color != null)
		{
			line.setColor(Color.newColor(color));
		}
		
		else
		{
			line.setColor(Color.newColor(generateColor()));
		}
		
		line.setLegend(person.getName());
		line.setLineStyle(LineStyle.MEDIUM_LINE);
		return line;
	}
	
	private LineChart createLineChart(List<Plot> lines, List<String> votersName, int max, TierList tierlist)
	{
		//Creates a list of String for the x axis.
		List<String> axisLabels = new ArrayList<>();
		
		for(String voter : votersName)
		{
			axisLabels.add(voter);
		}
		
		int width = 300 + 100 * votersName.size();
				
		LineChart chart = GCharts.newLineChart(lines);
		chart.setTitle(tierlist.getId(), Color.WHITE, 14);
		chart.setMargins(30, 0, 20, 30);
		chart.setBackgroundFill(Fills.newSolidFill(Color.newColor("36393f")));
		chart.setSize(width > 1000 ? 1000 : width, 300);
		
		AxisLabels yLabel = AxisLabelsFactory.newNumericRangeAxisLabels(1, max);
		yLabel.setAxisStyle(AxisStyle.newAxisStyle(Color.WHITE, 12, AxisTextAlignment.CENTER));
		chart.addYAxisLabels(yLabel);
		
		AxisLabels xLabel = AxisLabelsFactory.newAxisLabels(axisLabels);
		xLabel.setAxisStyle(AxisStyle.newAxisStyle(Color.WHITE, 12, AxisTextAlignment.CENTER));
		chart.addXAxisLabels(xLabel);
		
		return chart;
	}
	
	private static String generateColor()
	{
		Random r = new Random();
		
	    final char [] hex = { '0', '1', '2', '3', '4', '5', '6', '7',
	                          '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
	    char [] s = new char[6];
	    int n = r.nextInt(0x1000000);

	    for (int i=0;i<6;i++) {
	        s[i] = hex[n & 0xf];
	        n >>= 4;
	    }
	    return new String(s);
	}
}

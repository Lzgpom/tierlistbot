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

import javafx.util.Pair;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import pt.lzgpom.bot.lib.Config;
import pt.lzgpom.bot.model.Bot;
import pt.lzgpom.bot.model.Group;
import pt.lzgpom.bot.model.Person;
import pt.lzgpom.bot.model.Score;
import pt.lzgpom.bot.model.TierList;
import pt.lzgpom.bot.model.Voter;
import pt.lzgpom.bot.util.Utils;


public class Graph implements Command
{
	private static final String OVERALL = "Overall";

	public Graph()
	{
		
	}
	
	@Override
	public List<String> getCommands() 
	{
		java.util.List<String> commands = new ArrayList<>();
		commands.add("graph");
		return commands;
	}

	@Override
	public String getDescription() 
	{
		return "Makes a graph with tierlists.";
	}

	@Override
	public void run(String[] args, Bot bot, MessageChannel channel) 
	{
		try
		{
			HashMap<String, String> colors = Utils.readColors();
			
			String voterName = args[0];
			List<TierList> lists = new ArrayList<>();
			
			//Gets the Tier lists inputed.
			for(int i = 1; i < args.length; i++)
			{
				TierList tmp = bot.getTierListById(args[i]);
				
				if(tmp != null)
				{
					lists.add(tmp);
				}
				
				else
				{
					channel.sendMessage(String.format("%s not found...", args[i])).queue();
				}
			}
			
			if(lists.size() <= 1)
			{
				channel.sendMessage("Not enough tiers lists to create graph.").queue();
				return;
			}
			
			if(validateTierLists(lists))
			{
				channel.sendMessage("Not all tier lists are from the same group.").queue();
				return;
			}
						
			//The List for Lines.
			List<Plot> lines = new ArrayList<>();
			
			//Gets the group. Since the group is the same for all tier list, I get from the first one.
			Group group = lists.get(0).getGroup();
			
			//For The overall placement in tier list.
			if(voterName.equalsIgnoreCase(OVERALL))
			{
				//The Pair has the number of voters as key and the scores as value.
				List<Pair<Integer,List<Score>>> tierListScores = new ArrayList<>();
				
				for(TierList list : lists)
				{
					tierListScores.add(new Pair<>(list.getVoterList().size(), list.getFinalScores()));
				}
				
				for(Person person : group.getPeople())
				{
					List<Double> scores = new ArrayList<>();
					
					for(Pair<Integer, List<Score>> pair : tierListScores)
					{
						scores.add((double) getScoreByPerson(pair.getValue(), person) / pair.getKey());
					}
					
					lines.add(createLine(person, scores, group.getNumberOfPeople(), colors));
				}
				
				LineChart chart = createLineChart(lines, lists, group.getNumberOfPeople(), OVERALL);
				channel.sendMessage(chart.toURLString()).queue();
			}
			
			else
			{
				//Creates the lines for the graph.
				for(Person person : group.getPeople())
				{
					List<Integer> scores = new ArrayList<>();
					
					for(TierList list : lists)
					{
						Voter voter = list.getVoterByName(voterName);
						
						if(voter == null) 
						{
							System.out.println(voterName);
							channel.sendMessage(String.format("The user %s didnt vote in %s.", voterName, list.getId())).queue();
							return;
						}
						
						scores.add(voter.getVote(person).getScore());
					}
					
					lines.add(createLine(person, scores, group.getNumberOfPeople(), colors));
				}
				
				LineChart chart = createLineChart(lines, lists, group.getNumberOfPeople(), voterName);
				channel.sendMessage(chart.toURLString()).queue();
			}			
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
		eb.setTitle(Config.PREFIX + "graph", null);
		eb.setColor(java.awt.Color.YELLOW);
		
		eb.addField("Description:", "Makes a graph with tierlists.\n"
				+ "Overall: builds a graphic using the tierlists' overall results.", false);
		eb.addField("Usage:", getCommandName() + " <person> <tierlist id> <tierlist id> ...", false);
		eb.addField("Example: ", getCommandName() + " Lzgpom TWICE_4.0 TWICE_5.0\n"
				+ getCommandName() + " overall TWICE_4.0 TWICE_5.0", false);
		
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
	
	private boolean validateTierLists(List<TierList> lists)
	{
		if(lists.size() < 2)
		{
			return false;
		}
		
		Group group = lists.get(0).getGroup();
		
		for(TierList list : lists)
		{
			if(!list.getGroup().equals(group))
			{
				return false;
			}
		}
		
		return true;
	}
	
	private LineChart createLineChart(List<Plot> lines, List<TierList> tierLists, int max, String user)
	{
		//Creates a list of String for the x axis.
		List<String> axisLabels = new ArrayList<>();
		
		for(TierList tierList : tierLists)
		{
			axisLabels.add(tierList.getId());
		}
		
		int width = 300 + 100 * tierLists.size();
				
		LineChart chart = GCharts.newLineChart(lines);
		chart.setTitle(user, Color.WHITE, 14);
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
	
	private int getScoreByPerson(List<Score> scores, Person person)
	{
		for(Score score : scores)
		{
			if(score.getPerson().equals(person))
			{
				return score.getScore();
			}
		}
		
		return -1;
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

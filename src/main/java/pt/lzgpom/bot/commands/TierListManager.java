package pt.lzgpom.bot.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Logger;

import javafx.concurrent.Task;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.entities.User;
import pt.lzgpom.bot.model.Bot;
import pt.lzgpom.bot.model.Group;
import pt.lzgpom.bot.model.Person;
import pt.lzgpom.bot.model.TierList;
import pt.lzgpom.bot.model.Voter;
import pt.lzgpom.bot.util.Converter;
import pt.lzgpom.bot.util.SaveLoader;
import pt.lzgpom.bot.util.Utils;

public class TierListManager
{
	private final static Logger LOGGER = Logger.getLogger(TierListManager.class.getSimpleName());
	
	private final Bot bot;
	
	private TierList tierlist = null;
	private Map<User, List<PersonMessage>> messages = new HashMap<>();
	
	public TierListManager(Bot bot)
	{
		this.bot = bot;
	}
	
	/**
	 * @return If a tierlist is going already.
	 */
	public boolean hasTierListStarted()
	{
		return tierlist != null;
	}
	
	public void start(List<User> users, String id, Group group, String url)
	{
		this.tierlist = new TierList(id, group, url);
		LOGGER.info("TierList created.");

		ExecutorService executor = Executors.newFixedThreadPool(3);

		for(User user : users)
		{
			executor.submit(() ->
			{
				messages.put(user, new ArrayList<>());

				tierlist.addVoter(new Voter(user.getName(), user.getIdLong()));
				LOGGER.info("Created user " + user.getName());

				MessageChannel userChannel = user.openPrivateChannel().complete();
				userChannel.sendMessage(url).queue();

				for (Person person : group.getPeople())
				{

					Message message = userChannel.sendMessage(person.getName()).complete();
					messages.get(user).add(new PersonMessage(person, message.getIdLong()));

					for (int i = 1; i <= group.getNumberOfPeople(); i++)
					{
						message.addReaction(Utils.getReactionInPos(i)).queue();
					}
				}

			});
		}

		executor.shutdown();
	}
	
	public void end(MessageChannel mainChannel)
	{
		LOGGER.info("Starting to read the scores.");

		List<Future<?>> tasks = new ArrayList<>();
		ExecutorService executor = Executors.newFixedThreadPool(3);

		for(User user : messages.keySet())
		{
			tasks.add(executor.submit(() ->
			{
				MessageChannel channel = user.openPrivateChannel().complete();

				for(PersonMessage message : messages.get(user))
				{
					for(MessageReaction reaction : channel.getMessageById(message.getMessageId()).complete().getReactions())
					{
						if(reaction.getUsers().complete().size() > 1)
						{
							int score = Utils.getReactionValue(reaction.getReactionEmote().getName());
							tierlist.getVoterById(user.getIdLong()).addVote(message.person, score);
							LOGGER.info(String.format("The user %s voted on %s with %d.", user.getName(), message.getPerson().getName(), score));
						}
					}
				}
			}));
		}

		for(Future<?> task : tasks)
		{
			while(true)
			{
				if (task.isDone()) break;
			}
		}

		executor.shutdown();

		try
		{
			tierlist.validateVotes();
			bot.addTierList(tierlist);
			SaveLoader.saveCentre(bot);
			mainChannel.sendMessage(Converter.votersToEmbededMessage(tierlist)).queue();
			mainChannel.sendMessage(Converter.createFinalScoresMessage(tierlist)).queue();
			LOGGER.info("The Final Scores: " + tierlist.getFinalScores().toString());
			clear();
		}
		catch(IllegalArgumentException e)
		{
			LOGGER.info(e.getMessage());
			mainChannel.sendMessage(e.getMessage()).queue();
			tierlist.clearVoters();
		}
	}
	
	public void clear()
	{
		this.tierlist = null;
	}
	
	private class PersonMessage
	{
		private final Person person;
		private final long messageId;
		
		PersonMessage(Person person, long messageId)
		{
			this.person = person;
			this.messageId = messageId;
		}
		
		long getMessageId()
		{
			return this.messageId;
		}
		
		public Person getPerson()
		{
			return this.person;
		}
	}
}

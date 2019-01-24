package pt.lzgpom.bot.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "voter")
@XmlAccessorType(XmlAccessType.FIELD)
public class Voter
{
	private String name;
	private long id;
	
	@XmlElementWrapper(name = "scores")
	@XmlElement(name = "score")
	private List<Score> votes;
	
	public Voter()
	{
		this.name = null;
		this.id = 0;
		this.votes = new ArrayList<>();
	}
	
	public Voter(String name, long id)
	{
		this.name = name;
		this.id = id;
		this.votes = new ArrayList<>();
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public long getId()
	{
		return this.id;
	}
	
	public List<Score> getVotes()
	{
		return this.votes;
	}
	
	public Score getVote(Person person)
	{
		for(Score i : votes)
		{
			if(person.equals(i.getPerson()))
			{
				return i;
			}
		}
		
		return null;
	}
	
	public List<Person> getPeopleVoted()
	{
		List<Person> people = new ArrayList<>();
		
		for(Score vote : votes)
		{
			people.add(vote.getPerson());
		}
		
		return people;
	}
	
	public boolean addVote(Person person, int score)
	{
		if(validatePerson(person))
		{
			return votes.add(new Score(person, score));
		}
		
		return false;
	}
	
	private boolean validatePerson(Person person)
	{
		for(Score vote : votes)
		{
			if(vote.getPerson().equals(person))
			{
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Checks for no repetion of the same value for the vote.
	 */
	public boolean areAllVotesDiferent()
	{
		List<Integer> values = new ArrayList<>();
		
		for(Score vote : votes)
		{
			if(values.contains(vote.getScore()))
			{
				throw new IllegalArgumentException(String.format("%s ranked %s with the same score as someone else.", name, vote.getPerson().getName()));
			}
			
			values.add(vote.getScore());
		}
		
		return true;
	}
	
	/**
	 * Checks if the number of votes is the same as the group size and if the
	 * votes have all members of the group.
	 */
	public boolean validateVotesGroup(Group group)
	{
		List<Person> people = getPeopleVoted();
		
		if(people.containsAll(group.getPeople()) && people.size() == group.getPeople().size())
		{
			return true;
		}
		
		throw new IllegalArgumentException(String.format("%s didn't rank on everyone.", name));
	}
	
	/**
	 * Validates the votes on all circuntances.
	 */
	public boolean validateVotes(Group group)
	{
		return areAllVotesDiferent() && validateVotesGroup(group);
	}
	
	public void clearVotes()
	{
		this.votes = new ArrayList<>();
	}
}

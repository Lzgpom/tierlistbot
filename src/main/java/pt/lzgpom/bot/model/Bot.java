package pt.lzgpom.bot.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import pt.lzgpom.bot.commands.CommandManager;
import pt.lzgpom.bot.commands.TierListManager;

@XmlRootElement(name = "centre")
public class Bot 
{
	@XmlElementWrapper(name = "groups")
	@XmlElement(name = "group")
	private final List<Group> groupList;
	
	@XmlElementWrapper(name = "tierlists")
	@XmlElement(name = "tierlist")
	private final List<TierList> tierlistList;
	
	private final CommandManager commandManager = new CommandManager(this);
	private final TierListManager tierListManager = new TierListManager(this);
	
	public Bot()
	{
		groupList = new ArrayList<>();
		tierlistList = new ArrayList<>();
	}
	
	public List<Group> getGroups()
	{
		return this.groupList;
	}
	
	public Group getGroupByName(String groupName)
	{
		for(Group i : groupList)
		{
			if(i.getName().equalsIgnoreCase(groupName))
			{
				return i;
			}
		}
		
		return null;
	}
	
	public boolean addGroup(Group group)
	{
		if(!groupList.contains(group))
		{
			return groupList.add(group);
		}
		
		return false;
	}
	
	public boolean validateGroupName(String name)
	{
		for(Group group : groupList)
		{
			if(name.equalsIgnoreCase(group.getName()))
			{
				return false;
			}
		}
		
		return true;
	}
	
	public boolean hasTierListWithId(String id)
	{
		for(TierList list : tierlistList)
		{
			if(list.getId().equalsIgnoreCase(id))
			{
				return true;
			}
		}
		
		return false;
	}
	
	public boolean addTierList(TierList list)
	{
		return tierlistList.add(list);
	}
	
	public List<TierList> getTierLists()
	{
		return this.tierlistList;
	}
	
	public TierList getTierListById(String id)
	{
		for(TierList i : tierlistList)
		{
			if(i.getId().equalsIgnoreCase(id))
			{
				return i;
			}
		}
		
		return null;
	}
	
	public CommandManager getCommandManager()
	{
		return commandManager;
	}
	
	public TierListManager getTierListManager()
	{
		return tierListManager;
	}
}

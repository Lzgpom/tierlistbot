package pt.lzgpom.bot.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "group")
@XmlAccessorType(XmlAccessType.FIELD)
public class Group
{
	@XmlElementWrapper(name = "people")
	@XmlElement(name = "person")
	private final List<Person> group;
	private final String name;
	
	public Group()
	{
		this.name = "";
		this.group = new ArrayList<>();
	}
	
	public Group(String name, List<Person> group)
	{
		this.name = name;
		this.group = new ArrayList<>(group);
		orderGroupByAge();
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public List<Person> getPeople()
	{
		return this.group;
	}
	
	public boolean addPerson(Person person)
	{
		return group.add(person);
	}
	
	public int getNumberOfPeople()
	{
		return this.group.size();
	}
	
	public void orderGroupByAge()
	{
		Collections.sort(group);
	}
	
	@Override
	public int hashCode()
	{
		int hash = name.hashCode();
		hash = hash * 31 + group.hashCode();
		return hash;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(obj == null)
		{
			return false;
		}
		
		if(!(obj instanceof Rating))
		{
			return false;
		}
		
		Group other = (Group) obj;
		
		return this.name.equals(other.name) && this.group.equals(other.group);
	}
}

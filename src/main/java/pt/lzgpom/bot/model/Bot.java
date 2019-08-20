package pt.lzgpom.bot.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import pt.lzgpom.bot.commands.CommandManager;
import pt.lzgpom.bot.commands.tierlist.normal.TierListManager;
import pt.lzgpom.bot.commands.tierlist.real.RealTierListManager;
import pt.lzgpom.bot.model.realtierlist.RealTierList;
import pt.lzgpom.bot.model.realtierlist.RealTierListGlobal;

@XmlRootElement(name = "centre")
public class Bot {

  @XmlElementWrapper(name = "groups")
  @XmlElement(name = "group")
  private final List<Group> groupList;

  @XmlElementWrapper(name = "tierlists")
  @XmlElement(name = "tierlist")
  private final List<TierList> tierlistList;

  @XmlElementWrapper(name = "realtierlists")
  @XmlElement(name = "realtierlist")
  private final List<RealTierListGlobal> realTierLists;

  private final CommandManager commandManager = new CommandManager(this);
  private final TierListManager tierListManager = new TierListManager(this);
  private final RealTierListManager realTierListManager = new RealTierListManager(this);

  public Bot() {
    groupList = new ArrayList<>();
    tierlistList = new ArrayList<>();
    realTierLists = new ArrayList<>();
  }

  //==================
  //=Group utilities.=
  //==================
  public List<Group> getGroups() {
    return this.groupList;
  }

  public Group getGroupByName(String groupName) {
    for (Group i : groupList) {
      if (i.getName().equalsIgnoreCase(groupName)) {
        return i;
      }
    }

    return null;
  }

  public boolean addGroup(Group group) {
    if (!groupList.contains(group)) {
      return groupList.add(group);
    }

    return false;
  }

  public boolean validateGroupName(String name) {
    for (Group group : groupList) {
      if (name.equalsIgnoreCase(group.getName())) {
        return false;
      }
    }

    return true;
  }

  //=============================
  //=Normal Tier List utilities.=
  //=============================
  public boolean hasTierListWithId(String id) {
    for (TierList list : tierlistList) {
      if (list.getId().equalsIgnoreCase(id)) {
        return true;
      }
    }

    return false;
  }

  public boolean addTierList(TierList list) {
    return tierlistList.add(list);
  }

  public List<TierList> getTierLists() {
    return this.tierlistList;
  }

  public TierList getTierListById(String id) {
    for (TierList i : tierlistList) {
      if (i.getId().equalsIgnoreCase(id)) {
        return i;
      }
    }

    return null;
  }

  public TierListManager getTierListManager() {
    return tierListManager;
  }

  //===========================
  //=Real Tier List utilities.=
  //===========================
  public boolean hasRealTierListWithId(String id) {
    for (RealTierListGlobal list : realTierLists) {
      if (list.id().equals(id)) {
        return true;
      }
    }

    return false;
  }

  public void addRealTierList(RealTierListGlobal list) {
    this.realTierLists.add(list);
  }

  public List<RealTierListGlobal> getRealTierLists() {
    return this.realTierLists;
  }

  public RealTierListGlobal getRealTierListById(String id) {
    for (RealTierListGlobal list : realTierLists) {
      if (list.id().equals(id)) {
        return list;
      }
    }

    return null;
  }

  public RealTierListManager getRealTierManager() {
    return this.realTierListManager;
  }

  public CommandManager getCommandManager() {
    return this.commandManager;
  }
}

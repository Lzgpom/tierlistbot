package pt.lzgpom.bot.model.realtierlist;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "realtierlistglobal")
@XmlAccessorType(XmlAccessType.FIELD)
public class RealTierListGlobal {

  private String id;
  private List<RealTierList> lists;

  protected RealTierListGlobal() {
    //Used for ORM
  }

  public RealTierListGlobal(String id, List<RealTierList> lists) {
    this.id = id;
    this.lists = new ArrayList<>(lists);
  }

  /**
   * Returns the combined {@link RealTierList} list.
   *
   * @return the combined {@link RealTierList} list.
   */
  public RealTierList getGlobalTierList() {
    return RealTierList.join(lists);
  }

  public List<RealTierList> getLists() {
    return lists;
  }

  /**
   * Returns the number of Voters.
   *
   * @return the number of Voters.
   */
  public int getNumberOfVoter() {
    return lists.size();
  }

  /**
   * Returns the id of the Global Tier List.
   *
   * @return the id of the Global Tier List.
   */
  public String id() {
    return id;
  }
}

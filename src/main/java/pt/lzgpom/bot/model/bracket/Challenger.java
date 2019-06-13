package pt.lzgpom.bot.model.bracket;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement(name = "challenger")
@XmlAccessorType(XmlAccessType.FIELD)
public class Challenger {

  //Google search url parts.
  private static final String GOOGLE_SEARCH_URL = "https://www.google.com/search";
  private static final String GOOGLE_SEARCH_QUERY = "?q=";
  private static final String GOOGLE_SEARCH_IMAGE_MODIFIER = "&tbm=isch";

  private static final String SPACE_QUERY = "%20";

  private String name;
  private String extraInfo;

  @XmlTransient
  private String url;

  protected Challenger() {
    //Used for xml file.
  }

  /**
   * Creates an instance of ChallengerImpl.
   *
   * @param name The name of the challenger.
   * @param extraInfo Extra information of the challenger to create url.
   */
  public Challenger(String name, String extraInfo) {
    this.name = name;
    this.extraInfo = extraInfo;
    setUrl(name, extraInfo);
  }

  /**
   * Creates an instance of ChallengerImpl
   *
   * @param name The name of the challenger
   * @param extraInfo Extra information of the challenger
   * @param url The url fo the challenger.
   */
  public Challenger(String name, String extraInfo, String url) {
    this.name = name;
    this.extraInfo = extraInfo;
    this.url = url;
  }

  /**
   * Given extra info about the challenger creates a google image url.
   *
   * @param name The name of the challenger.
   * @param extraInfo The extra information about the challenger.
   */
  private void setUrl(String name, String extraInfo) {
    String query = name + SPACE_QUERY + extraInfo;
    query = query.replaceAll(" ", SPACE_QUERY);

    this.url = GOOGLE_SEARCH_URL + GOOGLE_SEARCH_QUERY + query + GOOGLE_SEARCH_IMAGE_MODIFIER;
  }

  public String getName() {
    return name;
  }

  public String getExtraInfo() {
    return extraInfo;
  }

  public String getUrl() {
    return url;
  }

  @Override
  public String toString() {
    return name;
  }

  @Override
  public int hashCode() {
    int hash = name.hashCode();
    return hash * 31 + extraInfo.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }

    if (!(obj instanceof Challenger)) {
      return false;
    }

    Challenger other = ((Challenger) obj);

    return this.name.equals(other.name) && this.extraInfo.equals(other.extraInfo);
  }
}

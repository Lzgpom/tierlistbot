package pt.lzgpom.bot.model.bracket.impl;

import pt.lzgpom.bot.model.bracket.Challenger;

/**
 * A simple implementation of Challenger.
 * @see Challenger
 */
public class ChallengerImpl implements Challenger
{
    //Google search url parts.
    private static final String GOOGLE_SEARCH_URL = "https://www.google.com/search";
    private static final String GOOGLE_SEARCH_QUERY = "?q=";
    private static final String GOOGLE_SEARCH_IMAGE_MODIFIER = "&tbm=isch";

    private static final String SPACE_QUERY = "%20";

    private String name;
    private String extraInfo;
    private String url;

    /**
     * Creates an instance of ChallengerImpl.
     * @param name The name of the challenger.
     * @param extraInfo Extra information of the challenger to create url.
     */
    public ChallengerImpl(String name, String extraInfo)
    {
        this.name = name;
        this.extraInfo = extraInfo;
        setUrl(name, extraInfo);
    }

    /**
     * Given extra info about the challenger creates a google image url.
     * @param name The name of the challenger.
     * @param extraInfo The extra information about the challenger.
     */
    private void setUrl(String name, String extraInfo)
    {
        String query = name + SPACE_QUERY + extraInfo;
        query = query.replaceAll(" ", SPACE_QUERY);

        this.url = GOOGLE_SEARCH_URL + GOOGLE_SEARCH_QUERY + query + GOOGLE_SEARCH_IMAGE_MODIFIER;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public String getExtraInfo()
    {
        return extraInfo;
    }

    @Override
    public String getUrl()
    {
        return url;
    }

    @Override
    public String toString()
    {
        return name;
    }
}

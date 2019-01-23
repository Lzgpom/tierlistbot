package pt.lzgpom.bot.model.bracket.impl;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.customsearch.Customsearch;
import pt.lzgpom.bot.model.bracket.Challenger;

import java.io.IOException;
import java.security.GeneralSecurityException;

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
    private static final String GOOGLE_SEARCH_IMAGE_SQUARE_MODIFIER = "&tbs=iar:s";

    private static final String SPACE_QUERY = "%20";

    private String name;
    private String url;

    /**
     * Creates an instance of ChallengerImpl.
     * @param name The name of the challenger.
     * @param extraInfo Extra information of the challenger to create url.
     */
    public ChallengerImpl(String name, String extraInfo)
    {
        this.name = name;
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


        try
        {
            Customsearch customsearch = new Customsearch(GoogleNetHttpTransport.newTrustedTransport(), new JacksonFactory(), new HttpRequestInitializer()
            {
                @Override
                public void initialize(HttpRequest request) throws IOException
                {
                    try
                    {
                        // set connect and read timeouts
                        request.setConnectTimeout(HTTP_REQUEST_TIMEOUT);
                        request.setReadTimeout(HTTP_REQUEST_TIMEOUT);

                    }
                    catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        this.url = GOOGLE_SEARCH_URL + GOOGLE_SEARCH_QUERY + query + GOOGLE_SEARCH_IMAGE_MODIFIER;
    }

    @Override
    public String getName()
    {
        return name;
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

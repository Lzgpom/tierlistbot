package pt.lzgpom.bot.util.bracket;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.customsearch.Customsearch;
import com.google.api.services.customsearch.model.Result;
import com.google.api.services.customsearch.model.Search;

import java.util.List;

public class ImageSearch
{
    private static final int HTTP_REQUEST_TIMEOUT = 3 * 600000;
    private static final String GOOGLE_API_KEY = "AIzaSyC5hsodXd8hb8yGcWUFnuGZ2VvUu23EIXU";
    private static final String SEARCH_ENGINE_ID = "008441243620261045803:bppouk8bcki";

    public static List<Result> search(String keyword)
    {
        Customsearch customsearch= null;

        try
        {
            customsearch = new Customsearch(new NetHttpTransport(), new JacksonFactory(), httpRequest ->
            {
                try
                {
                    // set connect and read timeouts
                    httpRequest.setConnectTimeout(HTTP_REQUEST_TIMEOUT);
                    httpRequest.setReadTimeout(HTTP_REQUEST_TIMEOUT);

                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            });

        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        List<Result> resultList = null;

        try
        {
            Customsearch.Cse.List list = customsearch.cse().list(keyword);
            list.setNum((long) 3);
            list.setSearchType("image");
            list.setKey(GOOGLE_API_KEY);
            list.setCx(SEARCH_ENGINE_ID);
            Search results = list.execute();
            resultList = results.getItems();
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        return resultList;
    }
}

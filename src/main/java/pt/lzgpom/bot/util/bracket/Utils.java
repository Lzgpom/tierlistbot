package pt.lzgpom.bot.util.bracket;

import com.google.api.services.customsearch.model.Result;
import pt.lzgpom.bot.model.Group;
import pt.lzgpom.bot.model.Person;
import pt.lzgpom.bot.model.bracket.Challenger;
import pt.lzgpom.bot.model.bracket.impl.ChallengerImpl;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.List;

public class Utils
{
    private static final String DATABASE = "save/bracket/";
    private static final String IMAGES = "images/";
    private static final String REGEX_SPLITTER = ":";
    public static final Map<Integer, String> phases = new HashMap<Integer, String>()
    {{
        put(0, "Finals");
        put(-1, "Looser Finals");
        put(1, "Semi-Finals");
        put(2, "Quarter-Finals");
    }};

    /**
     * Converts a {@link BufferedImage} into a {@link InputStream}.
     * @param img The {@link BufferedImage} to convert.
     * @return The converted {@link InputStream}.
     */
    public static InputStream bufferedImageToInputStream(BufferedImage img)
    {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try
        {
            ImageIO.write(img, "jpg", os);
            return new ByteArrayInputStream(os.toByteArray());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Reads a file and creates a list of {@link Challenger}.
     * @param file The file to read from.
     * @return A list of {@link Challenger}.
     */
    public static List<Challenger> readChallengersFile(String file)
    {
        List<Challenger> out = new ArrayList<>();

        try
        {
            Scanner input = new Scanner(new File(DATABASE + file), "UTF-8");

            while(input.hasNext())
            {
                String[] tmp = input.nextLine().split(REGEX_SPLITTER);
                out.add(new ChallengerImpl(tmp[0], tmp[1]));
            }

            input.close();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

        return out;
    }

    /**
     * Converts a list of {@link Group} into a list of {@link Challenger}.
     * @param groups The list of {@link Group}.
     * @return The converted list of {@link Challenger}.
     */
    public static List<Challenger> convertGroupsIntoChallengers(List<Group> groups)
    {
        List<Challenger> challengers = new ArrayList<>();

        for(Group group : groups)
        {
            for(Person person : group.getPeople())
            {
                challengers.add(new ChallengerImpl(person.getName(), group.getName()));
            }
        }

        return challengers;
    }

    /**
     * Creates the teams given the challengers, team size and number of teams.
     * If there are not enough different challengers for all teams,
     * it duplicates some of them.
     * @param challengers The list of {@link Challenger challengers}.
     * @param sizeTeam The size of the team.
     * @param numTeam The amount of teams.
     * @return A list of teams of {@link Challenger}
     */
    public static List<List<Challenger>> createTeams(List<Challenger> challengers, int sizeTeam, int numTeam)
    {
        List<Challenger> totalChallengers = new ArrayList<>();
        int totalChallengerNeeded = sizeTeam * numTeam;

        if(challengers.size() <= totalChallengerNeeded)
        {
            totalChallengers.addAll(challengers);
        }

        List<Challenger> cpy = new ArrayList<>(challengers);

        while(totalChallengers.size() < totalChallengerNeeded)
        {
            if(cpy.isEmpty())
            {
                cpy.addAll(challengers);
            }

            totalChallengers.add(chooseRandomChallengerFromList(cpy));
        }

        List<List<Challenger>> teams = new ArrayList<>();

        for(int i = 0; i < numTeam; i++)
        {
            teams.add(new ArrayList<>());

            for(int j = 0; j < sizeTeam; j++)
            {
                teams.get(i).add(chooseRandomChallengerFromList(totalChallengers));
            }
        }

        return teams;
    }

    /**
     * Chooses randomly a {@link Challenger} from the list
     * and removes it from the list.
     * @param challengers The list of {@link Challenger}.
     * @return The {@link Challenger} picked.
     */
    private static Challenger chooseRandomChallengerFromList(List<Challenger> challengers)
    {
        Random random = new Random();
        return challengers.remove(random.nextInt(challengers.size()));
    }

    /**
     * Gets a image from a link
     * @param link The link of the image.
     * @return The {@link Image} read, if not {@code null}.
     */
    public static BufferedImage getImageFromUrl(String link)
    {
        try
        {
            final URL url = new URL(link);
            final HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setRequestProperty(
                    "User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36");

            return ImageIO.read(connection.getInputStream());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public static BufferedImage getImageSquared(BufferedImage image)
    {
        int height = image.getHeight(null);
        int width = image.getWidth(null);

        if(height > width)
        {
            return image.getSubimage(0, 0, width, width);
        }

        else if(width > height)
        {
            return image.getSubimage((width / 2) - (height / 2), 0, height, height);
        }

        return image;
    }

    public static BufferedImage getImageFromSearch(String query)
    {
        List<Result> results = ImageSearch.search(query);

        for(Result result : results)
        {
            BufferedImage image = getImageFromUrl(result.getLink());

            if(image != null)
            {
                return image;
            }
        }

        return null;
    }

    /**
     * From the image of the {@link Challenger} from saved files. If
     * not found google searches for the {@link Challenger}, saves it and
     * then returns it.
     * @param challenger The {@link Challenger} to get the image from.
     * @return The image of the {@link Challenger}.
     */
    public static BufferedImage getImage(Challenger challenger)
    {
        try
        {
            return getImageSquared(ImageIO.read(new File(DATABASE + IMAGES + challenger.getName() + ".jpg")));
        }

        catch (IOException e)
        {
            BufferedImage image = getImageFromSearch(challenger.getName()+ " " + challenger.getExtraInfo());

            try
            {
                if(image != null)
                {
                    ImageIO.write(image, "jpg", new File(DATABASE + IMAGES + challenger.getName() + ".jpg"));
                }
            }
            catch (IOException e1)
            {
                e1.printStackTrace();
            }

            if(image != null)
            {
                return getImageSquared(image);
            }
        }

        return null;
    }
}

package pt.lzgpom.bot.util.bracket.image;

import net.dv8tion.jda.core.entities.User;
import pt.lzgpom.bot.model.bracket.Challenger;
import pt.lzgpom.bot.model.bracket.Duel;
import pt.lzgpom.bot.util.bracket.Utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class AIImageDuel
{
    private static final String ASSET_VS_LOCATION = "target/classes/duel/vs.png";

    private static final int MARGIN_PADDING_HORIZONTAL = 20;
    private static final int MARGIN_PADDING_VERTICAL = 20;

    private static final int USER_AVATAR_SIZE = 50;
    private static final int USER_AVATAR_PADDING = 10;
    private static final int FONT_SIZE = 20;
    private static final int COUNTERS_SEPARATION_DIST = 100;

    private static final int CHALLENGER_IMAGE_SIZE = 400;
    private static final int CHALLENGER_PADDING_HORIZONTAL = 30;
    private static final int CHALLENGER_PADDING_VERTICAL = 10;

    private Duel duel;
    private Set<User> users;
    private Map<User, Integer> counters;
    private Map<User, Integer> minorDisagrees;

    /**
     * Creates a instance of AIImageDuel.
     * @param duel The duel to draw.
     * @param counters The counters that each user has.
     * @param minorDisagrees The minor disagrees that each user has.
     */
    public AIImageDuel(Duel duel, Map<User, Integer> counters, Map<User, Integer> minorDisagrees)
    {
        this.duel = duel;
        this.users = counters.keySet();
        this.counters = counters;
        this.minorDisagrees = minorDisagrees;
    }

    public BufferedImage createImage()
    {
        int height = (MARGIN_PADDING_VERTICAL * 4) + (FONT_SIZE * 2) + USER_AVATAR_SIZE + ((CHALLENGER_IMAGE_SIZE + FONT_SIZE) * duel.getFirstChallenger().size())
                + (CHALLENGER_PADDING_VERTICAL * (duel.getFirstChallenger().size() - 1));

        int userWidth = (USER_AVATAR_SIZE * users.size()) + ((USER_AVATAR_PADDING * (users.size() - 1)) * 2) + COUNTERS_SEPARATION_DIST;
        int challengerWidth = (CHALLENGER_IMAGE_SIZE * 3) + (CHALLENGER_PADDING_HORIZONTAL * 2);

        int width = (MARGIN_PADDING_HORIZONTAL * 2) + ((userWidth > challengerWidth) ? userWidth : challengerWidth);

        BufferedImage image;
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        //Sets the background color
        g.setColor(Color.decode("#36393f"));
        g.fillRect(0, 0, width, height);

        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, FONT_SIZE));

        int x = MARGIN_PADDING_HORIZONTAL;
        int y = MARGIN_PADDING_VERTICAL;

        g.drawString("Counters:", x, y+ ((FONT_SIZE / 3) * 2));
        x = drawCounters(g, counters, x, y + FONT_SIZE) + COUNTERS_SEPARATION_DIST;

        g.drawString("Minor disagrees:", x, y + ((FONT_SIZE / 3) * 2));
        drawCounters(g, minorDisagrees, x, y + FONT_SIZE);

        x = MARGIN_PADDING_HORIZONTAL;
        y += MARGIN_PADDING_VERTICAL + (FONT_SIZE * 2) + USER_AVATAR_SIZE;

        try
        {
            Image vs = ImageIO.read(new File(ASSET_VS_LOCATION));
            int midY = ((height - y - MARGIN_PADDING_VERTICAL) / 2) + y - (CHALLENGER_IMAGE_SIZE / 2);
            g.drawImage(vs, x + CHALLENGER_IMAGE_SIZE + CHALLENGER_PADDING_HORIZONTAL, midY, null);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        for(int i = 0; i < duel.getFirstChallenger().size(); i++)
        {
            Challenger c1 = duel.getFirstChallenger().get(i);
            Challenger c2 = duel.getSecondChallenger().get(i);

            BufferedImage i1 = Utils.getImage(c1);
            BufferedImage i2 = Utils.getImage(c2);

            if(i1 != null)
            {
                g.drawString(c1.getName(), x, y + ((FONT_SIZE / 3) * 2));
                g.drawImage(Utils.getImageSquared(i1).getScaledInstance(CHALLENGER_IMAGE_SIZE, CHALLENGER_IMAGE_SIZE, Image.SCALE_DEFAULT), x, y + FONT_SIZE, null);
            }

            if(i2 != null)
            {
                g.drawString(c2.getName(), x + (CHALLENGER_IMAGE_SIZE * 2) + (CHALLENGER_PADDING_HORIZONTAL * 2), y + ((FONT_SIZE / 3) * 2));
                g.drawImage(Utils.getImageSquared(i2).getScaledInstance(CHALLENGER_IMAGE_SIZE, CHALLENGER_IMAGE_SIZE, Image.SCALE_DEFAULT),
                        x + (CHALLENGER_IMAGE_SIZE * 2) + (CHALLENGER_PADDING_HORIZONTAL * 2), y + FONT_SIZE, null);
            }


            y += CHALLENGER_IMAGE_SIZE + MARGIN_PADDING_VERTICAL + FONT_SIZE;
        }

        return image;
    }

    private int drawCounters(Graphics2D g, Map<User, Integer> map, int x, int y)
    {
        for(User user : users)
        {
            Image avatar = Utils.getImageFromUrl(user.getAvatarUrl()).getScaledInstance(USER_AVATAR_SIZE, USER_AVATAR_SIZE, Image.SCALE_DEFAULT);
            g.drawImage(avatar, x, y, null);
            g.drawString("" + map.get(user), x + (USER_AVATAR_SIZE / 4) + (FONT_SIZE / 3), y + USER_AVATAR_SIZE + FONT_SIZE);

            x += USER_AVATAR_SIZE + USER_AVATAR_PADDING;
        }

        return x;
    }
}

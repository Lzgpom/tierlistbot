package pt.lzgpom.bot.util.bracket.image;

import net.dv8tion.jda.core.entities.User;
import pt.lzgpom.bot.model.bracket.Challenger;
import pt.lzgpom.bot.model.bracket.Duel;
import pt.lzgpom.bot.util.bracket.Utils;

import java.awt.*;
import java.util.List;
import java.util.Map;

class ImageDuel
{
    static final int DUEL_HEIGHT = 150;
    static final int DUEL_WIDTH = 600;
    private static final int FONT_SIZE = 29;
    private static final int PADDING = 20;

    private Duel duel;
    private int midX;
    private int midY;

    private ImageDuel next;

    /**
     * Creates an instance of ImageDuel.
     * @param duel The duel to draw.
     * @param midX The mid x coordinate.
     * @param midY The mid y coordinate.
     */
    ImageDuel(Duel duel, int midX, int midY)
    {
        this.duel = duel;
        this.midX = midX;
        this.midY = midY;
    }

    /**
     * Draws the {@link Duel} in the graphics at the certain coordinates.
     * @param g The graphics of the image to draw onto.
     * @param userColors The colors of each {@link User}
     */
    void draw(Graphics2D g, Map<User, Color> userColors)
    {
        g.setColor(Color.DARK_GRAY);
        g.drawRect(midX, midY - (DUEL_HEIGHT / 2), DUEL_WIDTH - (DUEL_HEIGHT / 2), DUEL_HEIGHT);
        g.fillRect(midX, midY - (DUEL_HEIGHT / 2), DUEL_WIDTH - (DUEL_HEIGHT / 2), DUEL_HEIGHT);
        g.setColor(Color.LIGHT_GRAY);
        g.drawRect(midX + DUEL_WIDTH - (DUEL_HEIGHT / 2), midY - (DUEL_HEIGHT / 2), (DUEL_HEIGHT / 2), DUEL_HEIGHT);
        g.fillRect(midX + DUEL_WIDTH - (DUEL_HEIGHT / 2), midY - (DUEL_HEIGHT / 2), (DUEL_HEIGHT / 2), DUEL_HEIGHT);
        g.setColor(Color.WHITE);

        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        if(duel.getFirstChallenger() != null)
        {
            String teamName = convertTeamToString(duel.getFirstChallenger());
            int fontSize = fontSizeGivenString(teamName);

            g.setFont(new Font("Arial", Font.PLAIN, fontSize));
            g.drawString(teamName, midX + PADDING, midY - (DUEL_HEIGHT / 4) + (fontSize / 3));
        }

        if(duel.getSecondChallenger() != null)
        {
            String teamName = convertTeamToString(duel.getSecondChallenger());
            int fontSize = fontSizeGivenString(teamName);

            g.setFont(new Font("Arial", Font.PLAIN, fontSize));
            g.drawString(teamName, midX + PADDING, midY + (DUEL_HEIGHT / 4) + (fontSize / 3));
        }

        paintWinner(g, userColors);

        g.setColor(Color.WHITE);
        g.drawLine(midX, midY, midX + DUEL_WIDTH, midY);

        drawCounter(g, duel.getCounterUser());
    }

    private void paintWinner(Graphics2D g, Map<User, Color> userColors)
    {
        if(duel.getWinner() != null)
        {
            int x = midX + DUEL_WIDTH - (DUEL_HEIGHT / 2);

            if(duel.getCounterUser() == null)
            {
                int y = midY;

                if(duel.getWinner() == duel.getFirstChallenger())
                {
                    y = midY - (DUEL_HEIGHT / 2);
                }

                g.setColor(userColors.get(duel.getUser()));
                g.fillRect(x, y, (DUEL_HEIGHT / 2), (DUEL_HEIGHT / 2));
                g.drawRect(x, y, (DUEL_HEIGHT / 2), (DUEL_HEIGHT / 2));
            }

            else
            {
                Color first = userColors.get(duel.getCounterUser());
                Color second = userColors.get(duel.getUser());

               if(duel.getWinner() == duel.getSecondChallenger())
               {
                   first = userColors.get(duel.getUser());
                   second = userColors.get(duel.getCounterUser());
               }

                g.setColor(first);
                g.fillRect(x, midY - (DUEL_HEIGHT / 2), (DUEL_HEIGHT / 2), (DUEL_HEIGHT / 2));
                g.drawRect(x, midY - (DUEL_HEIGHT / 2), (DUEL_HEIGHT / 2), (DUEL_HEIGHT / 2));

                g.setColor(second);
                g.fillRect(x, midY, (DUEL_HEIGHT / 2), (DUEL_HEIGHT / 2));
                g.drawRect(x, midY, (DUEL_HEIGHT / 2), (DUEL_HEIGHT / 2));
            }
        }
    }

    private String convertTeamToString(List<Challenger> team)
    {
        StringBuilder s = new StringBuilder();

        for(int i = 0; i < team.size(); i++)
        {
            s.append(team.get(i).getName());

            if(i != team.size() - 1)
            {
                s.append(", ");
            }
        }

        return s.toString();
    }

    private int fontSizeGivenString(String str)
    {
        if(str.length() <= 30)
        {
            return FONT_SIZE;
        }
        else
        {
            int size = FONT_SIZE - (int) Math.ceil((double) (str.length() - 30) / 1.3);

            if(size > FONT_SIZE / 2)
            {
                return size;
            }

            return FONT_SIZE / 2;
        }
    }

    private void drawCounter(Graphics2D g, User user)
    {
        if(user != null)
        {
            Image image = Utils.getImageFromUrl(user.getAvatarUrl());

            if(image != null)
            {
                image = image.getScaledInstance(DUEL_HEIGHT / 2, DUEL_HEIGHT / 2, Image.SCALE_DEFAULT);
                g.drawImage(image, midX + DUEL_WIDTH - (DUEL_HEIGHT / 2), midY - (DUEL_HEIGHT / 4), null);
            }
        }

    }

    /**
     * Returns the mid x coordinate.
     * @return the mid x coordinate.
     */
    int getMidX()
    {
        return midX;
    }

    /**
     * Returns the mid y coordinate.
     * @return the mid y coordinate.
     */
    int getMidY()
    {
        return midY;
    }

    /**
     * Returns true if this ImageDuel is dummy.
     * @return true if this ImageDuel is dummy.
     */
    boolean isDummy()
    {
        return duel == null;
    }

    /**
     * Returns the duel of this ImageDuel.
     * @return The duel of this ImageDuel.
     */
    public Duel getDuel()
    {
        return duel;
    }

    /**
     * Gets the next ImageDuel.
     * @return the next ImageDuel.
     */
    ImageDuel getNext()
    {
        return next;
    }

    /**
     * Sets the next ImageDuel
     * @param next The next ImageDuel.
     */
    void setNext(ImageDuel next)
    {
        this.next = next;
    }
}

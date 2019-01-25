package pt.lzgpom.bot.util.bracket.image;

import net.dv8tion.jda.core.entities.User;
import pt.lzgpom.bot.model.bracket.BracketSolo;
import pt.lzgpom.bot.model.bracket.impl.DuelSolo;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ImageBracket
{
    private static final int MARGIN_PADDING_VERTICAL = 30;
    private static final int MARGIN_PADDING_HORIZONTAL = 30;
    private static final int DUELS_PADDING_VERTICAL = 25;
    private static final int DUELS_PADDING_HORIZONTAL = 50;

    private static final int DUEL_LOOSER_FINAL_PADDING = 100;
    private static final int LINE_THICKNESS = 5;

    private List<Map<Integer, DuelSolo>> bracket;
    private Map<User, Color> userColors;

    public ImageBracket(BracketSolo bracket, Map<User, Color> userColors)
    {
        this.bracket = bracket.getDuels();
        this.userColors = userColors;
    }

    public BufferedImage createImage()
    {
        int lastPartSize = (int) Math.pow(2, bracket.size() - 1);
        int height = (MARGIN_PADDING_VERTICAL * 2) + (lastPartSize * ImageDuel.DUEL_HEIGHT) + ((lastPartSize - 1) * DUELS_PADDING_VERTICAL);
        int width = (MARGIN_PADDING_HORIZONTAL * 2) + (bracket.size() * ImageDuel.DUEL_WIDTH) + ((bracket.size() - 1) * DUELS_PADDING_HORIZONTAL);

        List<ImageDuel> imageDuels = new ArrayList<>();

        int midY = MARGIN_PADDING_VERTICAL + (ImageDuel.DUEL_HEIGHT / 2);
        int midX = MARGIN_PADDING_HORIZONTAL;

        for(int i = bracket.size() - 1; i >= 0; i--)
        {
            int n = (int) Math.pow(2, i);

            for(int j = 0; j < n; j++)
            {
                int index = imageDuels.size() - (n * 2) + j;

                if(i == bracket.size() - 1)
                {
                    if(j != 0)
                    {
                        midY += DUELS_PADDING_VERTICAL + ImageDuel.DUEL_HEIGHT;
                    }
                }
                else
                {
                    midY = (imageDuels.get(index).getMidY() + imageDuels.get(index + 1).getMidY()) / 2;
                }

                ImageDuel imageDuel = new ImageDuel(bracket.get(i).getOrDefault(j, null), midX, midY);
                imageDuels.add(imageDuel);

                if(i != bracket.size() - 1)
                {
                    imageDuels.get(index).setNext(imageDuel);
                    imageDuels.get(index + 1).setNext(imageDuel);
                }

            }

            midX += ImageDuel.DUEL_WIDTH + DUELS_PADDING_HORIZONTAL;
        }

        //Adds the third place
        if(bracket.size() > 1 && bracket.get(1).size() == 2)
        {
            imageDuels.add(new ImageDuel(bracket.get(0).get(1), width - MARGIN_PADDING_HORIZONTAL - ImageDuel.DUEL_WIDTH, height / 2 + DUEL_LOOSER_FINAL_PADDING + ImageDuel.DUEL_HEIGHT));

            int necessary = (height / 2) + DUEL_LOOSER_FINAL_PADDING + ImageDuel.DUEL_HEIGHT + (ImageDuel.DUEL_HEIGHT / 2) + MARGIN_PADDING_VERTICAL;

            if(height < necessary)
            {
                height = necessary;
            }
        }

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        //Sets the background color
        g.setColor(Color.decode("#36393f"));
        g.fillRect(0, 0, width, height);

        g.setStroke(new BasicStroke(LINE_THICKNESS));

        for(ImageDuel part : imageDuels)
        {
            if (!part.isDummy())
            {
                part.draw(g, userColors);

                //Connection front
                if (part.getMidX() + ImageDuel.DUEL_WIDTH < width - MARGIN_PADDING_HORIZONTAL)
                {
                    Color color = Color.WHITE;

                    if(part.getDuel().getWinner() != null)
                    {
                        color = (part.getDuel().getCounterUser() != null) ? userColors.get(part.getDuel().getCounterUser()) : userColors.get(part.getDuel().getUser());
                    }

                    g.setColor(color);

                    //Front part
                    g.drawLine(part.getMidX() + ImageDuel.DUEL_WIDTH + LINE_THICKNESS, part.getMidY(), part.getMidX() + ImageDuel.DUEL_WIDTH + (DUELS_PADDING_HORIZONTAL / 2), part.getMidY());

                    //Vertical line
                    int startX = part.getMidX() + (DUELS_PADDING_HORIZONTAL / 2) + ImageDuel.DUEL_WIDTH - (LINE_THICKNESS / 2);
                    int startY = part.getMidY();

                    GradientPaint gradient = new GradientPaint(startX, startY, color, startX, startY + Math.abs(part.getMidY() - part.getNext().getMidY()), Color.WHITE);

                    if(startY > part.getNext().getMidY())
                    {
                        startY = part.getNext().getMidY();
                        gradient = new GradientPaint(startX, startY + Math.abs(part.getMidY() - part.getNext().getMidY()), color, startX, startY, Color.WHITE);
                    }

                    g.setPaint(gradient);
                    g.fill(new Rectangle2D.Double(startX, startY, LINE_THICKNESS, Math.abs(part.getMidY() - part.getNext().getMidY())));

                    //Back part of the next.
                    g.setColor(Color.WHITE);
                    g.drawLine(part.getNext().getMidX() - (DUELS_PADDING_HORIZONTAL / 2), part.getNext().getMidY(), part.getNext().getMidX(), part.getNext().getMidY());
                }
            }
        }

        return image;
    }
}

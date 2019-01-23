package pt.lzgpom.bot.util.bracket.image;

import pt.lzgpom.bot.model.bracket.BracketSolo;
import pt.lzgpom.bot.model.bracket.impl.DuelSolo;

import java.awt.*;
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

    private List<Map<Integer, DuelSolo>> bracket;

    public ImageBracket(BracketSolo bracket)
    {
        this.bracket = bracket.getDuels();
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

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        //Sets the background color
        g.setColor(Color.decode("#36393f"));
        g.fillRect(0, 0, width, height);

        g.setStroke(new BasicStroke(5));

        for(int i = 0; i < imageDuels.size(); i++)
        {
            ImageDuel part = imageDuels.get(i);

            if(!part.isDummy())
            {
                part.draw(g);
                g.setColor(Color.WHITE);

                //Connection front
                if(part.getMidX() + ImageDuel.DUEL_WIDTH < width - MARGIN_PADDING_HORIZONTAL)
                {
                    //Front part
                    g.drawLine(part.getMidX() + ImageDuel.DUEL_WIDTH, part.getMidY(), part.getMidX() + ImageDuel.DUEL_WIDTH + (DUELS_PADDING_HORIZONTAL / 2), part.getMidY());

                    int startX = part.getMidX() + (DUELS_PADDING_HORIZONTAL / 2) + ImageDuel.DUEL_WIDTH;
                    int startY = part.getMidY();
                    int endX = part.getNext().getMidX() - (DUELS_PADDING_HORIZONTAL / 2);
                    int endY = part.getNext().getMidY();

                    //Vertical line.
                    g.drawLine(startX, startY, endX, endY);

                    //Back part of the next.
                    g.drawLine(part.getNext().getMidX() - (DUELS_PADDING_HORIZONTAL / 2), part.getNext().getMidY(), part.getNext().getMidX(), part.getNext().getMidY());
                }
            }
        }

        return image;
    }
}

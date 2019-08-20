package pt.lzgpom.bot.util.real;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import pt.lzgpom.bot.model.bracket.Challenger;
import pt.lzgpom.bot.model.realtierlist.RealTierList;
import pt.lzgpom.bot.model.realtierlist.RealTierListGlobal;
import pt.lzgpom.bot.util.Utils;

public class ImageRealTierList {

  private static final Color BACKGROUND_COLOR = Color.decode("#36393f");
  private static final Color TIER_BACKGROUND_COLOR = Color.decode("#3f4249");

  private static final int TIER_HEIGHT = 250;
  private static final int CHALLENGER_SCALE_DOWN = 7;
  private static final int CHALLENGER_IMAGE_SIZE = TIER_HEIGHT - (CHALLENGER_SCALE_DOWN * 2);
  private static final int MARGIN_PADDING = 25;
  private static final int TIER_PADDING = 18;

  private static final int GLOBAL_IMAGE_PADDING = MARGIN_PADDING * 2;

  private static final int FONT_SIZE = 50;

  public static BufferedImage createImage(RealTierList list) {
    int width =
        (MARGIN_PADDING * 2) + (TIER_HEIGHT * (list.getMaxTierSize() + 1)) + (TIER_PADDING * list
            .getMaxTierSize());
    int height = (MARGIN_PADDING * 2) + (TIER_HEIGHT * list.getNumberOfTiers()) + (TIER_PADDING * (
        list.getNumberOfTiers() - 1));

    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    Graphics2D g = image.createGraphics();
    g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

    //Sets the background color
    g.setColor(BACKGROUND_COLOR);
    g.fillRect(0, 0, width, height);

    List<String> tiers = new ArrayList<>(list.map().keySet());

    for (int i = 0; i < list.getNumberOfTiers(); i++) {
      //Background rectangles.
      g.setColor(TIER_BACKGROUND_COLOR);
      g.fillRect(MARGIN_PADDING + TIER_HEIGHT + TIER_PADDING,
          MARGIN_PADDING + (TIER_HEIGHT * i) + (TIER_PADDING * (i - 1 < 0 ? 0 : i)),
          width - (MARGIN_PADDING * 2) - TIER_PADDING - TIER_HEIGHT, TIER_HEIGHT);

      //Tier square
      int y = MARGIN_PADDING + (TIER_HEIGHT * i) + +(TIER_PADDING * (i - 1 < 0 ? 0 : i));
      g.setColor(Color.decode(Utils.getTiers().get(tiers.get(i)).getColor()));
      g.fillRect(MARGIN_PADDING,
          y, TIER_HEIGHT,
          TIER_HEIGHT);

      //Tier name
      g.setColor(Color.BLACK);
      g.setFont(new Font("Arial", Font.BOLD, FONT_SIZE));
      g.drawString(tiers.get(i), MARGIN_PADDING + (TIER_HEIGHT / 2) - (FONT_SIZE / 3),
          y + (TIER_HEIGHT / 2) + (FONT_SIZE / 2));


      //Drawing images of the challengers in the tier.
      List<Challenger> challengers = list.map().get(tiers.get(i)).iterator();

      for (int j = 0; j < challengers.size(); j++) {
        BufferedImage imageChallenger = pt.lzgpom.bot.util.bracket.Utils
            .getImage(challengers.get(j));

        int x =
            MARGIN_PADDING + TIER_HEIGHT + TIER_PADDING + CHALLENGER_SCALE_DOWN + (j * (TIER_HEIGHT
                + CHALLENGER_SCALE_DOWN));

        g.drawImage(pt.lzgpom.bot.util.bracket.Utils.getImageSquared(imageChallenger)
                .getScaledInstance(CHALLENGER_IMAGE_SIZE, CHALLENGER_IMAGE_SIZE, Image.SCALE_SMOOTH),
            x, y + CHALLENGER_SCALE_DOWN, null);
      }
    }

    return image;
  }

  public static BufferedImage createGlobalImage(RealTierListGlobal global) {
    List<BufferedImage> images = new ArrayList<>();
    int height = 0;
    int width = 0;

    for(RealTierList list : global.getLists()) {
      BufferedImage tmp = createImage(list);
      height = tmp.getHeight();
      width += tmp.getWidth();
      images.add(tmp);
    }

    width += (images.size() - 1) * GLOBAL_IMAGE_PADDING;

    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

    Graphics2D g = image.createGraphics();
    g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

    //Sets the background color
    g.setColor(BACKGROUND_COLOR);
    g.fillRect(0, 0, width, height);

    int x = 0;

    for(BufferedImage img : images) {
      g.drawImage(img, x, 0, null);
      x += img.getWidth() + GLOBAL_IMAGE_PADDING;
    }

    return image;
  }
}
package pt.lzgpom.bot.util;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import pt.lzgpom.bot.lib.Config;
import pt.lzgpom.bot.model.realtierlist.Tier;

public class Utils {

  private static final String COLORS_FILE = "./save/colors.txt";
  private static final String TIERS_FILE = "./save/tiers.txt";
  private static final String VIDEOS_FILE = "./save/video.txt";

  private static volatile Map<String, String> colors;
  private static volatile Map<String, Tier> tiers;

  public static String getReactionInPos(int pos) {
    return "" + Config.REACTIONS.charAt((2 * pos) - 2) + Config.REACTIONS.charAt((2 * pos) - 1);
  }

  public static int getReactionValue(String reaction) {
    return (Config.REACTIONS.indexOf(reaction) / 2) + 1;
  }

  public static boolean isNumeric(String str) {
    return str.matches("-?\\d+(\\.\\d+)?");
  }

  public static Map<String, String> readColors() {

    if (colors != null) {
      return colors;
    }

    colors = new HashMap<>();
    try {
      Scanner in = new Scanner(new File(COLORS_FILE));

      while (in.hasNext()) {
        String[] personColor = in.nextLine().split(":");
        colors.put(personColor[0], personColor[1]);
      }

      in.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }

    return colors;
  }

  /**
   * Reads a config file for the tiers.
   *
   * @return A map with the tiers and their information.
   */
  public static Map<String, Tier> getTiers() {

    if (tiers != null) {
      return tiers;
    }

    tiers = new LinkedHashMap<>();

    try (FileReader reader = new FileReader(TIERS_FILE);
        BufferedReader br = new BufferedReader(reader)) {

      String line;
      while ((line = br.readLine()) != null) {
        String[] parts = line.split(";");
        tiers.put(parts[0], new Tier(parts[1], parts[2]));
      }

    } catch (IOException e) {
      e.printStackTrace();
    }

    return tiers;
  }

  public static String getRandomVideo() throws FileNotFoundException {
    String result = null;
    Random rand = new Random();
    int n = 0;
    for (Scanner sc = new Scanner(new File(VIDEOS_FILE)); sc.hasNext(); ) {
      ++n;
      String line = sc.nextLine();
      if (rand.nextInt(n) == 0) {
        result = line;
      }
    }

    return result;
  }

  /**
   * Returns the position of the tier given its reaction.
   *
   * @param reaction The reaction to look for.
   * @return The position of the reaction. If not found returns -1.
   */
  public static int getTierPositionFromReaction(String reaction) {
    int i = 0;
    for (Tier tier : getTiers().values()) {
      if (tier.getReaction().equals(reaction)) {
        return i;
      }
      i++;
    }

    return -1;
  }

  public static List<Color> rainbowColor(int size) {
    List<Color> colors = new ArrayList<>();

    final int FADES = 6;

    int perFade = (int) Math.ceil((double) size / FADES);

    for (int r = 0; r < perFade; r++) {
      colors.add(new Color(r * 255 / perFade, 255, 0));
    }
    for (int g = perFade; g > 0; g--) {
      colors.add(new Color(255, g * 255 / perFade, 0));
    }
    for (int b = 0; b < perFade; b++) {
      colors.add(new Color(255, 0, b * 255 / perFade));
    }
    for (int r = perFade; r > 0; r--) {
      colors.add(new Color(r * 255 / perFade, 0, 255));
    }
    for (int g = 0; g < perFade; g++) {
      colors.add(new Color(0, g * 255 / perFade, 255));
    }
    for (int b = perFade; b > 0; b--) {
      colors.add(new Color(0, 255, b * 255 / perFade));
    }
    colors.add(new Color(0, 255, 0));

    return colors;
  }
}

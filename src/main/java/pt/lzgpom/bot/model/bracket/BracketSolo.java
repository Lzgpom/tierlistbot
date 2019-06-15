package pt.lzgpom.bot.model.bracket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import net.dv8tion.jda.core.entities.User;
import pt.lzgpom.bot.model.bracket.impl.DuelSolo;

public class BracketSolo {

  private List<Map<Integer, DuelSolo>> duels;
  private boolean isRandom;
  private int currentPart;

  public BracketSolo(List<List<Challenger>> teams, boolean isRandom) {
    setup(teams);
    this.isRandom = isRandom;
  }

  /**
   * Creates the bracket randomly.
   *
   * @param teams The teams of the bracket.
   */
  private void setup(List<List<Challenger>> teams) {
    createDuels(teams.size());
    fillDuels(new ArrayList<>(teams));
  }

  public List<Map<Integer, DuelSolo>> getDuels() {
    return duels;
  }

  /**
   * Creates an empty bracket for all teams.
   *
   * @param teams The number of teams.
   */
  private void createDuels(int teams) {
    //Finds the greatest potency of 2 that is smaller than the size of teams.
    int n = 1;

    while (teams > n) {
      n <<= 1;
    }

    n >>= 1;

    //The part of the bracket, being 0 the finals and so on...
    int part = 0;

    this.duels = new ArrayList<>();

    for (int i = 1; i <= n >> 1; i <<= 1) {
      duels.add(new HashMap<>());

      for (int i2 = 0; i2 < i; i2++) {
        duels.get(part).put(i2, new DuelSolo(part));
      }

      part++;
    }

    //Adds the third place.
    if (teams >= 4) {
      duels.get(0).put(1, new DuelSolo(-1));
    }

    //If the bracket size isn't a potency of 2 it adds the rest to the next part.
    if (teams - n > 0) {
      int index = 0;

      duels.add(new HashMap<>());

      for (int i = 0; i < teams - n; i++) {
        duels.get(part).put(index, new DuelSolo(part));

        index += 2;

        if (index >= n) {
          index = 1;
        }
      }
    }
  }

  /**
   * Fills the beginning of the bracket.
   *
   * @param teams The teams who are participating.
   */
  private void fillDuels(List<List<Challenger>> teams) {
    Random random = new Random();
    int lastPart = duels.size() - 1;

    //Sets the current part to the last one.
    this.currentPart = lastPart;

    for (DuelSolo duel : duels.get(lastPart).values()) {
      duel.addTeam(teams.remove(random.nextInt(teams.size())));
      duel.addTeam(teams.remove(random.nextInt(teams.size())));
    }

    while (!teams.isEmpty()) {
      for (int i = duels.get(lastPart - 1).size() - 1; i >= 0; i--) {
        if (teams.isEmpty()) {
          break;
        }

        duels.get(lastPart - 1).get(i).addTeam(teams.remove(random.nextInt(teams.size())));
      }
    }
  }

  /**
   * Returns true if there are still matches to do, otherwise false.
   *
   * @return true if there are still matches to do, otherwise false.
   */
  public boolean hasNextDuel() {
    return !tbdDuelsCurrentPart().isEmpty();
  }

  /**
   * Returns the next assets to be decided. If the isRandom flag is true it returns a random
   * assets.
   *
   * @return The next TBD Duel, or null if there are none left.
   */
  public DuelSolo getNextDuel() {
    List<DuelSolo> tbdDuels = tbdDuelsCurrentPart();

    if (!isRandom) {
      return tbdDuels.get(0);
    }

    Random random = new Random();
    return tbdDuels.get(random.nextInt(tbdDuels.size()));
  }

  /**
   * Returns all the tbd duels of the current Part.
   *
   * @return All the tbd duels of the current Part.
   */
  private List<DuelSolo> tbdDuelsCurrentPart() {
    if (currentPart == -1) {
      return new ArrayList<>();
    }

    List<DuelSolo> tbdDuels = new ArrayList<>();

    for (DuelSolo duel : duels.get(currentPart).values()) {
      if (duel.getWinner() == null && duel.isReadyToStart()) {
        tbdDuels.add(duel);
      }
    }

    return tbdDuels;
  }

  /**
   * Sets the winner of the dual and moves the winning team to the next part.
   *
   * @param duel The {@link DuelSolo assets}
   * @param user The {@link User} who choose the winner.
   * @param winner The position of the winner.
   */
  public void setDuelWinner(DuelSolo duel, User user, int winner) {
    duel.setWinner(winner, user);
    moveOnWinner(duel);
  }

  /**
   * Adds a counter to a assets.
   *
   * @param duel The assets to add the counter.
   * @param user The user who countered.
   */
  public void addDuelCounter(DuelSolo duel, User user) {
    duel.addCounter(user);
    moveOnWinner(duel);
  }

  /**
   * If there are no more tbd duels in this part it move the part no the next one.
   */
  public void moveToNextPart() {
    if (tbdDuelsCurrentPart().size() == 0) {
      this.currentPart--;
    }
  }

  /**
   * Moves the winner to the next part.
   *
   * @param duel The assets of which the winner moves on.
   */
  private void moveOnWinner(DuelSolo duel) {
    if (currentPart == 0) {
      return;
    }

    if (duel.getWinner() != null) {
      int index = getKeyByValue(duels.get(currentPart), duel);

      //Moves the looser to Losers Final
      if (currentPart == 1) {
        duels.get(0).get(1).addTeam(duel.getLooser(), index);
      }

      duels.get(currentPart - 1).get(index >> 1)
          .addTeam(duel.getWinner(), (index % 2 == 0) ? 0 : 1);
    }
  }

  private static <T, E> T getKeyByValue(Map<T, E> map, E value) {
    for (Map.Entry<T, E> entry : map.entrySet()) {
      if (Objects.equals(value, entry.getValue())) {
        return entry.getKey();
      }
    }
    return null;
  }


  @Override
  public String toString() {
    StringBuilder s = new StringBuilder();

    for (Map<Integer, DuelSolo> row : duels) {
      for (DuelSolo duel : row.values()) {
        s.append(duel).append("  ");
      }

      s.append("\n");
    }

    return s.toString();
  }
}

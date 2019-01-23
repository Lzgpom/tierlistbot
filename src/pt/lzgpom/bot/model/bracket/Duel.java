package pt.lzgpom.bot.model.bracket;

import net.dv8tion.jda.core.entities.User;

import java.util.List;

public interface Duel
{
    /**
     * Returns The first {@link Challenger team}.
     * @return The first {@link Challenger team}.
     */
    List<Challenger> getFirstChallenger();

    /**
     * Returns The second {@link Challenger team}.
     * @return The second {@link Challenger team}.
     */
    List<Challenger> getSecondChallenger();

    /**
     * Returns the round in which the duel occurs.
     * @return The round in which the duel occurs.
     */
    int getRound();

    /**
     * Returns the number of spots available for challengers.
     * @return the number of spots available for challengers.
     */
    int getFreeSpots();

    /**
     * Adds a team to the duel
     * @param team The {@link Challenger team} to add.
     */
    void addTeam(List<Challenger> team);

    /**
     * Adds a team to the dual.
     * @param team The {@link Challenger team} to add.
     * @param position The position at which the team will be in the dual.
     */
    void addTeam(List<Challenger> team, int position);

    /**
     * Checks if both teams are in the Dual.
     * @return True if it is ready to start, otherwise false.
     */
    boolean isReadyToStart();

    /**
     * Returns the winner team of the Dual.
     * @return the winner team of the Dual.
     */
    List<Challenger> getWinner();

    /**
     * Sets the winner of the dual
     * @param position The position of the winning team.
     */
    void setWinner(int position);

    /**
     * Returns the looser team of the Dual.
     * @return the looser team of the Dual.
     */
    List<Challenger> getLooser();

    /**
     * Changes the dual winner due to a counter.
     * @param user The user who used the counter.
     */
    void addCounter(User user);

    /**
     * Returns the user that used counter
     * @return The user that used counter if there was
     *         a counter, otherwise {@code null}.
     * @see User
     */
    User getCounterUser();
}

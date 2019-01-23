package pt.lzgpom.bot.model.bracket.impl;

import net.dv8tion.jda.core.entities.User;
import pt.lzgpom.bot.model.bracket.Challenger;
import pt.lzgpom.bot.model.bracket.Duel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A dual in which there is only a person to vote.
 */
public class DuelSolo implements Duel
{
    private Map<Integer, List<Challenger>> teams;
    private int winner;
    private int round;
    private User user;
    private User counterUser;

    /**
     * Creates a instance of DualSolo.
     */
    public DuelSolo(int round)
    {
        this.round = round;
        teams = new HashMap<>();
        winner = -1;
    }

    @Override
    public List<Challenger> getFirstChallenger()
    {
        return teams.get(0);
    }

    @Override
    public List<Challenger> getSecondChallenger()
    {
        return teams.get(1);
    }

    @Override
    public int getRound()
    {
        return round;
    }

    @Override
    public int getFreeSpots()
    {
        int spots = 0;

        if(teams.get(0) == null)
        {
            spots++;
        }

        if(teams.get(1) == null)
        {
            spots++;
        }

        return spots;
    }

    @Override
    public void addTeam(List<Challenger> team)
    {
        if(teams.get(1) != null)
        {
            teams.put(0, team);
        }
        else
        {
            teams.put(1, team);
        }
    }

    @Override
    public void addTeam(List<Challenger> team, int position)
    {
        if(position >= 0 && position <= 1)
        {
            teams.put(position, team);
        }
    }

    @Override
    public boolean isReadyToStart()
    {
        return teams.size() == 2;
    }

    @Override
    public List<Challenger> getWinner()
    {
        if(winner >= 0 && winner <= 1)
        {
            if(counterUser != null)
            {
                return teams.get(winner ^ 1);
            }

            return teams.get(winner);
        }

        return null;
    }

    @Override
    public void setWinner(int position, User user)
    {
        this.user = user;
        this.winner = position;
    }

    @Override
    public List<Challenger> getLooser()
    {
        if(winner >= 0 && winner <= 1)
        {
            if(counterUser == null)
            {
                return teams.get(winner ^ 1);
            }

            return teams.get(winner);
        }

        return null;
    }

    @Override
    public void addCounter(User user)
    {
        this.counterUser = user;
    }

    @Override
    public User getCounterUser()
    {
        return counterUser;
    }

    @Override
    public User getUser()
    {
        return user;
    }

    @Override
    public String toString()
    {
        if(teams.size() == 1)
        {
            if(teams.get(1) != null)
            {
                return String.format("TBD vs %s", teams.get(1).toString());
            }

            if(teams.get(0) != null)
            {
                return String.format("%s vs TBD", teams.get(0).toString());
            }
        }

        if(teams.size() == 2)
        {
            return String.format("%s vs %s", teams.get(0).toString(), teams.get(1).toString());
        }

        return "TBD vs TBD";
    }
}

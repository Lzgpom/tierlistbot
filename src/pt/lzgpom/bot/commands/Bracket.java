package pt.lzgpom.bot.commands;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;
import pt.lzgpom.bot.lib.Config;
import pt.lzgpom.bot.model.Bot;
import pt.lzgpom.bot.model.Group;
import pt.lzgpom.bot.model.bracket.BracketSolo;
import pt.lzgpom.bot.model.bracket.Challenger;
import pt.lzgpom.bot.model.bracket.Duel;
import pt.lzgpom.bot.model.bracket.impl.DuelSolo;
import pt.lzgpom.bot.util.bracket.Utils;
import pt.lzgpom.bot.util.bracket.image.ImageBracket;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Bracket implements Command
{
    private static final String REACTION_A = "🇦";
    private static final String REACTION_B = "🇧";
    private static final String REACTION_COUNTER = "😡";
    private static final String REACTION_SEE = "👀";
    private static final String REACTION_CANCEL = "❌";
    private static final String REACTION_CONTINUE = "✅";

    private static final String FILE_MODIFIER = "-f";
    private static final String GROUPS_MODIFIER = "-g";

    private BracketSolo bracket;
    private Map<User, Integer> counters;
    private Map<User, Integer> minorDisagrees;
    private List<User> participants;

    public Bracket()
    {
        this.bracket = null;
    }

    @Override
    public List<String> getCommands()
    {
        List<String> commands = new ArrayList<>();
        commands.add("bracket");
        commands.add("br");
        return commands;
    }

    @Override
    public String getDescription()
    {
        return "Creates a bracket in which people can participate.";
    }

    @Override
    public MessageEmbed getHelpMessage()
    {
        return null;
    }

    /**
     * Asks who wants to participate in the bracket.
     * @param channel The message channel where it was invoked.
     * @param counters The number of counters all users have.
     * @param minorDisagrees The number of minor disagrees all user have.
     */
    private void start(MessageChannel channel, int counters, int minorDisagrees)
    {
        Message message = channel.sendMessage("React to participate!").complete();
        long id = message.getIdLong();
        message.addReaction("🤚").queue();

        try
        {
            TimeUnit.SECONDS.sleep(Config.TIME_TO_REACT);
        }

        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        participants = new ArrayList<>(channel.getMessageById(id).complete().getReactions().get(0).getUsers().complete());
        participants.remove(participants.size() - 1);
        Collections.shuffle(participants);

        this.counters = new HashMap<>();
        this.minorDisagrees = new HashMap<>();

        for(User user : participants)
        {
            this.counters.put(user, counters);
            this.minorDisagrees.put(user, minorDisagrees);
        }
    }

    @Override
    public void run(String[] args, Bot bot, MessageChannel channel)
    {
        if(isBracketRunning())
        {
            channel.sendMessage("There is a bracket already going...").queue();
            return;
        }

        if(args.length < 6)
        {
            channel.sendMessage("There are not enough arguments").queue();
            return;
        }

        for(int i = 0; i < 4; i++)
        {
            if(!pt.lzgpom.bot.util.Utils.isNumeric(args[i]))
            {
                channel.sendMessage("Invalid first four params, they are not numbers...").queue();
                return;
            }
        }

        List<List<Challenger>> teams = startTeams(channel, args, bot);

        if(teams.size() <= 1)
        {
            channel.sendMessage("Not enough teams...").queue();
            return;
        }


        start(channel, Integer.parseInt(args[2]), Integer.parseInt(args[3]));

        if(participants.size() < 1)
        {
            channel.sendMessage("Not enough users...").queue();
            return;
        }

        bracket = new BracketSolo(teams, true);

        while(bracket.hasNextDuel())
        {
            DuelSolo duel = bracket.getNextDuel();
            User user = getNextUser();
            Message message = channel.sendMessage(makeDuelMessage(duel, user)).complete();
            long messageId = message.getIdLong();

            //Reactions for the user to interact with.
            message.addReaction(REACTION_A).queue();
            message.addReaction(REACTION_B).queue();
            message.addReaction(REACTION_SEE).queue();
            message.addReaction(REACTION_CANCEL).queue();

            int winner = -1;
            boolean hasShownBracket = false;

            while(winner == -1)
            {
                List<MessageReaction> reactions = channel.getMessageById(messageId).complete().getReactions();

                //Show bracket.
                if(!hasShownBracket && getPeopleReacted(reactions, REACTION_SEE).size() > 0)
                {
                    uploadBracket(channel);
                    hasShownBracket = true;
                }

                //Cancel Bracket
                if(getPeopleReacted(reactions, REACTION_CANCEL).size() == participants.size())
                {
                    bracket = null;
                    channel.sendMessage("Bracket canceled!").queue();
                    return;
                }

                //Set winner to 0.
                if(hasUserReacted(user, reactions, REACTION_A))
                {
                    winner = 0;
                }

                //Set winner to 1.
                else if(hasUserReacted(user, reactions, REACTION_B))
                {
                    winner = 1;
                }
            }

            bracket.setDuelWinner(duel, winner);

            doCounters(channel, user, duel);
        }

        end(channel);
    }

    /**
     * After the duel it awaits for someone to counter or for all
     * the participants to continue.
     * @param channel The message channel.
     * @param user The {@link User} who decided this round.
     * @param duel The {@link Duel} of this encounter.
     */
    private void doCounters(MessageChannel channel, User user, DuelSolo duel)
    {
        if(hasAnyoneCounter(user))
        {
            Message message = channel.sendMessage("Counter?").complete();
            long messageId = message.getIdLong();

            message.addReaction(REACTION_COUNTER).queue();
            message.addReaction(REACTION_SEE).queue();
            message.addReaction(REACTION_CONTINUE).queue();

            boolean hasShownBracket = false;
            while (true)
            {
                List<MessageReaction> reactions = channel.getMessageById(messageId).complete().getReactions();

                if (getPeopleReacted(reactions, REACTION_COUNTER).size() > 0)
                {
                    User counterUser = addCounter(user, reactions, duel);

                    if (counterUser != null)
                    {
                        channel.sendMessage(String.format("%s countered the duel!!!", counterUser.getName())).queue();
                        return;
                    }
                }

                //Show bracket.
                if(!hasShownBracket && getPeopleReacted(reactions, REACTION_SEE).size() > 0)
                {
                    uploadBracket(channel);
                    hasShownBracket = true;
                }

                List<User> users = getPeopleReacted(reactions, REACTION_CONTINUE);
                users.remove(user);

                if (users.size() == participants.size() - 1)
                {
                    return;
                }
            }
        }
    }

    /**
     * Checks if someone tried to add a counter. If someone did it adds it.
     * @param user The user who is voting.
     * @param reactions The reactions of the message.
     * @param duel The duel to add the counter.
     * @return If a counter was added, otherwise false.
     */
    private User addCounter(User user, List<MessageReaction> reactions, DuelSolo duel)
    {
        List<User> users = getPeopleReacted(reactions, REACTION_COUNTER);

        if(users.size() > 0)
        {
            for(User counterUser : users)
            {
                if(counterUser != user)
                {
                    int n = counters.get(counterUser);

                    if(n > 0)
                    {
                        bracket.addDuelCounter(duel, counterUser);
                        counters.replace(counterUser, n - 1);

                        return counterUser;
                    }
                }
            }
        }

        return null;
    }

    /**
     * Makes the teams according to the args of the command.
     * @param channel The message channel.
     * @param args The arguments of the command.
     * @param bot The bot.
     * @return The list of teams.
     */
    private List<List<Challenger>> startTeams(MessageChannel channel, String[] args, Bot bot)
    {
        List<List<Challenger>> teams = new ArrayList<>();

        int teamSize = Integer.parseInt(args[0]);
        int numberTeams = Integer.parseInt(args[1]);

        if(teamSize <= 0)
        {
            channel.sendMessage("Team size cannot be 0 or less.").queue();
            return teams;
        }

        if(numberTeams <= 1)
        {
            channel.sendMessage("The number of teams cannot be 1 or less.").queue();
            return teams;
        }

        List<Challenger> challengers;

        if(args[4].equalsIgnoreCase(FILE_MODIFIER))
        {
            challengers = Utils.readChallengersFile(args[5]);

            if(challengers.isEmpty())
            {
                channel.sendMessage("Invalid file or file is empty.").queue();
                return teams;
            }
        }

        else if(args[4].equalsIgnoreCase(GROUPS_MODIFIER))
        {
            List<Group> groups = new ArrayList<>();

            for(int i = 5; i < args.length; i++)
            {
                Group group = bot.getGroupByName(args[i]);

                if(group == null)
                {
                    channel.sendMessage("Group " + args[i] + " doesn't exist...").queue();
                }
                else
                {
                    groups.add(group);
                }
            }

            if(groups.isEmpty())
            {
                channel.sendMessage("There were imputed 0 valid groups...").queue();
                return teams;
            }

            challengers = Utils.convertGroupsIntoChallengers(groups);
        }
        else
        {
            return teams;
        }

        return Utils.createTeams(challengers, teamSize, numberTeams);
    }

    private void end(MessageChannel channel)
    {
        uploadBracket(channel);
        bracket = null;
        counters = null;
        minorDisagrees = null;
    }
    /**
     * Checks if there is a bracket running.
     * @return If there is a bracket running returns true, otherwise false.
     */
    private boolean isBracketRunning()
    {
        return bracket != null;
    }

    /**
     * Creates a {@link MessageEmbed} of a duel.
     * @param duel The {@link Duel} to convert to message.
     * @param user The {@link User} to answer this dual.
     * @return The {@link MessageEmbed} created.
     */
    private MessageEmbed makeDuelMessage(Duel duel, User user)
    {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.YELLOW);

        String fieldName;
        String phase = Utils.phases.get(duel.getRound());

        if(phase != null)
        {
            fieldName = phase;
        }
        else
        {
            fieldName = String.format("Round of %d", (int) Math.pow(2, duel.getRound()));
        }

        eb.addField(String.format("%s%nYour turn:", fieldName), user.getName(), true);

        eb.addField("A", teamToString(duel.getFirstChallenger()), true);
        eb.addField("B", teamToString(duel.getSecondChallenger()), true);

        return eb.build();
    }

    /**
     * Creates a String of the team with the challenger and
     * the link to a image search.
     * @param team The team to create the string from.
     * @return The string created.
     */
    private static String teamToString(List<Challenger> team)
    {
        StringBuilder s = new StringBuilder();

        for(int i = 0; i < team.size(); i++)
        {
            s.append(String.format("[%s](%s)", team.get(i).getName(), team.get(i).getUrl()));

            if(i != team.size() - 1)
            {
                s.append("\n");
            }
        }

        return s.toString();
    }

    /**
     * Returns the next {@link User}.
     * @return the next {@link User}.
     */
    private User getNextUser()
    {
        User user = participants.remove(0);
        participants.add(user);
        return user;
    }

    /**
     * Uploads the bracket in image form.
     * @param channel The message channel where it was invoked.
     */
    private void uploadBracket(MessageChannel channel)
    {
        ImageBracket img = new ImageBracket(bracket);
        channel.sendFile(Utils.bufferedImageToInputStream(img.createImage()), "bracket.jpg").queue();
    }

    private static boolean hasUserReacted(User user, List<MessageReaction> reactions, String unicode)
    {
        for(MessageReaction reaction : reactions)
        {
            if(reaction.getReactionEmote().getName().equals(unicode))
            {
                return reaction.getUsers().complete().contains(user);
            }
        }

        return false;
    }

    private static List<User> getPeopleReacted(List<MessageReaction> reactions, String unicode)
    {
        for(MessageReaction reaction : reactions)
        {
            if(reaction.getReactionEmote().getName().equals(unicode))
            {
                List<User> users =  new ArrayList<>(reaction.getUsers().complete());
                users.remove(users.size() - 1);
                return users;
            }
        }

        return new ArrayList<>();
    }

    /**
     * Checks if the other users have a counter left.
     * @param user The user who voted this round.
     * @return True if the other users have at least one counter.
     */
    private boolean hasAnyoneCounter(User user)
    {
        for(User other : counters.keySet())
        {
            if(!user.equals(other))
            {
                if(counters.get(other) > 0)
                {
                    return true;
                }
            }
        }

        return false;
    }
}

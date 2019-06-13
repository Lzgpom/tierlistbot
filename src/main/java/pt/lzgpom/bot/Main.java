package pt.lzgpom.bot;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import pt.lzgpom.bot.commands.*;
import pt.lzgpom.bot.model.Bot;
import pt.lzgpom.bot.util.ConfigLoader;
import pt.lzgpom.bot.util.SaveLoader;


public class Main {

  public static void main(String[] args) {
    //Loads the configurations.
    ConfigLoader.loadCongigurations();
    Bot bot = SaveLoader.readCentre();

    //Adds the commands
    bot.getCommandManager().addCommand(new Help());
    bot.getCommandManager().addCommand(new StartTierList());
    bot.getCommandManager().addCommand(new EndTierList());
    bot.getCommandManager().addCommand(new CancelTierList());
    bot.getCommandManager().addCommand(new List());
    bot.getCommandManager().addCommand(new Get());
    bot.getCommandManager().addCommand(new RenameTierList());
    bot.getCommandManager().addCommand(new StartRealTierList());
    bot.getCommandManager().addCommand(new RealList());
    bot.getCommandManager().addCommand(new RealGet());
    bot.getCommandManager().addCommand(new GroupCommand());
    bot.getCommandManager().addCommand(new GroupList());
    bot.getCommandManager().addCommand(new GroupGet());
    bot.getCommandManager().addCommand(new GroupAdd());
    bot.getCommandManager().addCommand(new Graph());
    bot.getCommandManager().addCommand(new GraphCompare());
    bot.getCommandManager().addCommand(new Video());
    bot.getCommandManager().addCommand(new Bracket());
    bot.getCommandManager().addCommand(new Sort());
    bot.getCommandManager().addCommand(new MiniGame());

    JDABuilder builder = new JDABuilder(AccountType.BOT);
    builder.setToken("");
    builder.setAutoReconnect(true);
    builder.setStatus(OnlineStatus.ONLINE);
    builder.setGame(new Game("Watching KPOP!") {
    });
    builder.addEventListener(bot.getCommandManager());

    try {
      builder.buildAsync();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}

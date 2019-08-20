package pt.lzgpom.bot.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import pt.lzgpom.bot.lib.Config;
import pt.lzgpom.bot.model.Bot;

public class CommandManager extends ListenerAdapter {

  private final List<Command> commands;
  private final Bot bot;

  public CommandManager(Bot bot) {
    commands = new ArrayList<>();
    this.bot = bot;
  }

  public boolean addCommand(Command command) {
    return this.commands.add(command);
  }

  public Command getCommandById(String id) {
    for (Command command : commands) {
      if (command.getCommandName().equalsIgnoreCase(id)) {
        return command;
      }
    }

    return null;
  }

  public List<Command> getCommandList() {
    return this.commands;
  }

  @Override
  public void onMessageReceived(MessageReceivedEvent event) {
    if (event.getMessage().getContentDisplay().startsWith(Config.PREFIX)) {
      String[] args = event.getMessage().getContentDisplay().replaceFirst(Config.PREFIX, "").trim()
          .split(" ");

      for (Command command : commands) {
        for (String commandName : command.getCommands()) {
          if (args[0].equalsIgnoreCase(commandName)) {
            command.run(Arrays.copyOfRange(args, 1, args.length), bot, event.getChannel(),
                event.getMember());
          }
        }
      }
    }
  }
}

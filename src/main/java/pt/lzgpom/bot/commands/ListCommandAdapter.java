package pt.lzgpom.bot.commands;

import java.awt.Color;
import java.util.List;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;
import net.dv8tion.jda.core.entities.User;
import pt.lzgpom.bot.commands.utils.General;
import pt.lzgpom.bot.lib.Config;
import pt.lzgpom.bot.model.Bot;

public abstract class ListCommandAdapter<T> implements Command {

  private static final String INVALID_PAGE_NUMBER = "Invalid page number.";

  @Override
  public void run(String[] args, Bot bot, MessageChannel channel, Member user) {
    List<T> elements = getElements(bot);
    int totalPages = (int) Math.ceil((double) elements.size() / Config.LISTS_PER_PAGE);
    int page = 1;

    if (args.length > 0) {
      try {
        page = Integer.parseInt(args[0]);

        if (page > totalPages || page < 1) {
          channel.sendMessage(INVALID_PAGE_NUMBER).queue();
          return;
        }
      } catch (NumberFormatException e) {
        channel.sendMessage(INVALID_PAGE_NUMBER).queue();
        return;
      }
    }

    EmbedBuilder eb = new EmbedBuilder();

    eb.setTitle(listTitle(), null);
    eb.setColor(Color.YELLOW);

    int min = Config.LISTS_PER_PAGE * (page - 1);
    int max = Config.LISTS_PER_PAGE * page > elements.size() ? elements.size()
        : Config.LISTS_PER_PAGE * page;

    for (int i = min; i < max; i++) {
      eb.addField(getConverter().elementToField(elements.get(i), i));
    }

    eb.setAuthor("TierListBot", null, Config.ICON);
    eb.setFooter(String.format("Page %d/%d", page, totalPages), null);

    channel.sendMessage(eb.build()).queue();
  }

  /**
   * Returns the elements to list.
   *
   * @return the elements to list.
   */
  public abstract List<T> getElements(Bot bot);

  /**
   * Returns the {@link Converter} to convert elements into {@link Field}.
   *
   * @return The {@link Converter}.
   */
  public abstract Converter<T> getConverter();

  /**
   * Returns the title of the list.
   *
   * @return The title of the list..
   */
  public abstract String listTitle();

  @Override
  public MessageEmbed getHelpMessage() {
    return General.buildListHelpMessage(this);
  }

  /**
   * A interface for how to convert a element to a {@link Field}.
   *
   * @param <T> The type of the element to convert.
   */
  @FunctionalInterface
  public interface Converter<T> {
    Field elementToField(T element, int pos);
  }
}

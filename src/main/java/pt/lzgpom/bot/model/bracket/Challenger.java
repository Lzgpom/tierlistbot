package pt.lzgpom.bot.model.bracket;

public interface Challenger
{
    /**
     * Returns the name of the challenger.
     * @return the name of the challenger
     */
    String getName();

    /**
     * Returns the extra information about the challenger.
     * @return The extra information about the challenger.
     */
    String getExtraInfo();

    /**
     * Returns a google images url of the challenger.
     * @return a google images url of the challenger.
     */
    String getUrl();
}

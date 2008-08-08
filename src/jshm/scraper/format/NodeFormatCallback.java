package jshm.scraper.format;

/**
 * This specifies an interface for what you would
 * like to do with the text retrieved from a node, such
 * as removing commas from what would otherwise be a
 * numeric string.
 * @author Tim
 *
 */
public interface NodeFormatCallback {
	/**
	 * Use to modify the input string as desired for
	 * the given situation.
	 * @param text The text to modify. Guaranteed not null.
	 * @return
	 */
	public String format(String text);
}

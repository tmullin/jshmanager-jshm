package jshm.sh.scraper;

import org.htmlparser.tags.ImageTag;
import org.htmlparser.tags.LinkTag;

import jshm.scraper.format.RegexFormatCallback;
import jshm.scraper.format.TableColumnFormat;
import jshm.scraper.format.TagFormat;

/**
 * This is a class to hold static formats specific to
 * ScoreHero pages. This class cannot be instantiated.
 * @author Tim Mullin
 *
 */
public class Formats {
	public static final TagFormat STAR_RATING_SRC = new TagFormat(
		"src",
		new RegexFormatCallback("^.*rating_(\\d)\\.gif$", 1)
	);
	public static final TagFormat SONG_ID_HREF = new TagFormat(
		"href",
		new RegexFormatCallback("^.*song=(\\d+).*$", 1)
	);
	
	static {
		TableColumnFormat.addFormat("link", LinkTag.class,
			TagFormat.HREF,
			"songid", SONG_ID_HREF);
			
		TableColumnFormat.addFormat("img", ImageTag.class,
			TagFormat.SRC,
			"rating", STAR_RATING_SRC);
	}
	
	/**
	 * This is a dummy methods to be called to ensure that this
	 * class gets loaded and the static block gets executed.
	 */
	public static void init() {}
	
	private Formats() {}
}

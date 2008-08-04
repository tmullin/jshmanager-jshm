package jshm.sh.scraper.format;

import org.htmlparser.*;
import org.htmlparser.nodes.*;

/**
 * This NodeFormat returns the text of a
 * specified attribute of a TagNode.
 * @author Tim
 *
 */
public class TagFormat extends NodeFormat {
	public static final TagFormat SRC =
		new TagFormat("src");
	public static final TagFormat HREF =
		new TagFormat("href");
	
	public static final TagFormat STAR_RATING_SRC = new TagFormat(
		"src",
		new RegexFormatCallback("^.*rating_(\\d)\\.gif$", 1)
	);
	public static final TagFormat SONG_ID_HREF = new TagFormat(
		"href",
		new RegexFormatCallback("^.*song=(\\d+).*$", 1)
	);
	
	protected String attribute;
	
	public TagFormat(final String attribute) {
		this(attribute, null);
	}
	
	public TagFormat(final String attribute, final NodeFormatCallback formatCb) {
		super(formatCb);
		this.attribute = attribute;
	}
	
	@Override protected String getTextInternal(final Node node) {
		if (!(node instanceof TagNode))
			throw new IllegalArgumentException("TagNode required");
		
		TagNode tagNode = (TagNode) node;
		return tagNode.getAttribute(attribute);
	}
}

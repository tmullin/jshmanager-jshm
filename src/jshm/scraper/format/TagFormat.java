package jshm.scraper.format;

import org.htmlparser.*;
import org.htmlparser.nodes.*;
import org.htmlparser.util.Translate;

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
	public static final TagFormat TITLE =
		new TagFormat("title");
	
	protected String attribute;
	
	public TagFormat(final String attribute) {
		this(attribute, null);
	}
	
	public TagFormat(final String attribute, final NodeFormatCallback formatCb) {
		super(formatCb);
		this.attribute = attribute;
	}
	
	@Override
	protected String getTextInternal(final Node node) {
		if (!(node instanceof TagNode))
			throw new IllegalArgumentException("TagNode required");
		
		TagNode tagNode = (TagNode) node;
		String ret = tagNode.getAttribute(attribute);
		return ret == null ? "" : Translate.decode(ret);
	}
}

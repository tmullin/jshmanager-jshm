package jshm.scraper.format;

import org.htmlparser.*;
import org.htmlparser.util.*;

/**
 * This class specifies what text to return from
 * a given node. By default node.getText() is returned.
 * @author Tim Mullin
 *
 */
public class NodeFormat {
	public static final NodeFormat DEFAULT = new NodeFormat();
	
	public static final NodeFormat KEEP_NUMBERS =new NodeFormat(
		new RegexFormatCallback("[^\\d]")
	);
	
	public static final NodeFormat PARSE_FLOAT = new NodeFormat(
		new RegexFormatCallback("^.*?(\\d+(?:\\.\\d+)?|\\.\\d+).*?$", 1)
	);
	
	protected NodeFormatCallback formatCb;
	
	public NodeFormat() {
		this(null);
	}
	
	public NodeFormat(final NodeFormatCallback formatCb) {
		this.formatCb = formatCb;
	}
	
	/**
	 * Returns the desired text from the node and executes
	 * formattingCallback.format() if provided.
	 * @param node
	 * @return
	 */
	public final String getText(final Node node) {
		String text = getTextInternal(node);
		
		if (null != formatCb && null != text) {
			text = formatCb.format(text);
		}
		
		return text;
	}
	
	/**
	 * This actually gets the raw text the user is
	 * interested in and should be overridden. getText()
	 * is final to ensure that the formattingCallback gets
	 * called once we have the text. 
	 * @param node
	 * @return
	 */
	protected String getTextInternal(final Node node) {
		return Translate.decode(node.getText());
	}
	
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof NodeFormat)) return false;
		
		return formatCb.equals(((NodeFormat) o).formatCb);
	}
}

/*
 * -----LICENSE START-----
 * JSHManager - A Java-based tool for managing one's ScoreHero account.
 * Copyright (C) 2008 Tim Mullin
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * -----LICENSE END-----
 */
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
	 * 
	 * <p>
	 * When overridden, one may still need to call
	 * {@link Translate#decode(String)} on whatever text they are
	 * returning.
	 * </p>
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

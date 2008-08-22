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
	public static final TagFormat VALUE =
		new TagFormat("value");
	
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

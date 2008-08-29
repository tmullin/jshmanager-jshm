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
package jshm.sh.scraper;

import org.htmlparser.Node;
import org.htmlparser.nodes.TextNode;
import org.htmlparser.tags.ImageTag;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;

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
	
	public static final TagFormat PIC_VID_LINK = new TagFormat("href") {
		/**
		 * This requires extra processing to see if the link's child
		 * is a text node or an image node for the pic or vid link
		 * respectively.
		 * {@inheritDoc}
		 */
		@Override
		protected String getTextInternal(final Node node) {
			String ret = super.getTextInternal(node);
			
			NodeList children = node.getChildren();
			
			if (children.size() != 1) return "";
			
			if (children.elementAt(0) instanceof TextNode) {
				// if the child is a text node, it is the
				// score's link, which links to the picture url
				ret = "pic:" + ret;
			} else if (children.elementAt(0) instanceof ImageTag) {
				// if the child is an image, it is the camera image
				// link, which links to the video url
				ret = "vid:" + ret;
			} else {
				ret = "";
			}
			
			return ret;
		}
	};
	
	static {
		TableColumnFormat.addFormat("link", LinkTag.class,
			TagFormat.HREF,
			"songid", SONG_ID_HREF,
			"picvid", PIC_VID_LINK);
			
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

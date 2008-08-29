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

import org.htmlparser.nodes.TextNode;
import org.htmlparser.tags.ImageTag;
import org.htmlparser.tags.TableTag;
import org.htmlparser.util.NodeList;
import org.junit.*;

import static org.junit.Assert.*;

public class FormatsTest extends jshm.scraper.format.TagFormatTest {	
	@Test public void starRatingTest1() {
		assertEquals("6", Formats.STAR_RATING_SRC.getText(node));
	}
	
	@Test public void starRatingTest2() {
		node.setAttribute("src", "some/value/that/doesnt/match");
		assertEquals("", Formats.STAR_RATING_SRC.getText(node));
	}
	
	@Test public void songIdTest1() {
		assertEquals("12345", Formats.SONG_ID_HREF.getText(node));
	}
	
	@Test public void songIdTest2() {
		node.setAttribute("href", "another/value/that/doesnt/match");
		assertEquals("", Formats.SONG_ID_HREF.getText(node));
	}
	
	@Test public void picVidTest1() {
		node.setAttribute("href", "http://www.google.com");
		node.setChildren(new NodeList(new TextNode("12,345")));
		assertEquals("pic:http://www.google.com", Formats.PIC_VID_LINK.getText(node));
	}
	
	@Test public void picVidTest2() {
		node.setAttribute("href", "http://www.youtube.com");
		node.setChildren(new NodeList(new ImageTag()));
		assertEquals("vid:http://www.youtube.com", Formats.PIC_VID_LINK.getText(node));
	}
	
	@Test public void picVidTest3() {
		node.setAttribute("href", "http://www.youtube.com");
		node.setChildren(new NodeList(new TableTag()));
		assertEquals("", Formats.PIC_VID_LINK.getText(node));
	}
}

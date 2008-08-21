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

import org.junit.*;
import static org.junit.Assert.*;

import org.htmlparser.nodes.*;

public class TagFormatTest {
	protected TagNode node;
	
	@Before public void setUp() {
		node = new TagNode();
		node.setAttribute("href", "http://doesntmatter/woo.php?foo=bar&song=12345&ladada");
		node.setAttribute("src", "images/somewhere/rating_6.gif");
	}
	
	@After public void tearDown() {
		node = null;
	}
	
	@Test public void hrefTest1() {
		assertEquals(
			"http://doesntmatter/woo.php?foo=bar&song=12345&ladada",
			TagFormat.HREF.getText(node));
	}
	
	@Test public void srcTest1() {
		assertEquals(
			"images/somewhere/rating_6.gif",
			TagFormat.SRC.getText(node));
	}
}

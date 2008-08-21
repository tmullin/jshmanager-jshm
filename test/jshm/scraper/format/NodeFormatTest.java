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

import jshm.scraper.format.NodeFormat;

import org.junit.*;
import static org.junit.Assert.*;

import org.htmlparser.nodes.*;

public class NodeFormatTest {
	@Test public void defaultTest() {
		TextNode node = new TextNode("Hello &amp; world");
		assertEquals("Hello & world",
			NodeFormat.DEFAULT.getText(node));
	}
	
	@Test public void defaultEmptyTest() {
		TextNode node = new TextNode("");
		assertEquals("",
			NodeFormat.DEFAULT.getText(node));
	}
	
	@Test public void keepNumbersTest1() {
		TextNode node = new TextNode("123,456");
		assertEquals("123456",
			NodeFormat.KEEP_NUMBERS.getText(node));
	}
	
	@Test public void keepNumbersTest2() {
		TextNode node = new TextNode("123ABC,456,DEF789");
		assertEquals("123456789",
			NodeFormat.KEEP_NUMBERS.getText(node));
	}
	
	@Test public void parseFloatTest1() {
		TextNode node = new TextNode("(7.3)");
		assertEquals("7.3",
			NodeFormat.PARSE_FLOAT.getText(node));
	}
	
	@Test public void parseFloatTest2() {
		TextNode node = new TextNode("#$%123ABC");
		assertEquals("123",
			NodeFormat.PARSE_FLOAT.getText(node));
	}
	
	@Test public void parseFloatTest3() {
		TextNode node = new TextNode("(.1234)ABC");
		assertEquals(".1234",
			NodeFormat.PARSE_FLOAT.getText(node));
	}
}

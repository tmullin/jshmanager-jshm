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

import jshm.scraper.format.InvalidFormatArgumentException;
import jshm.scraper.format.InvalidFormatException;
import jshm.scraper.format.NodeFormat;
import jshm.scraper.format.TableColumnFormat;
import jshm.scraper.format.TagFormat;
import jshm.scraper.format.UndefinedFormatTypeException;
import jshm.sh.scraper.Formats;

import org.junit.*;
import static org.junit.Assert.*;

import org.htmlparser.nodes.*;
import org.htmlparser.tags.*;

public class TableColumnFormatTest {
	@Test public void ignoreTest1() {
		TableColumnFormat fmt1 = new TableColumnFormat();
		TableColumnFormat fmt2 = TableColumnFormat.factory("-");
		assertEquals(fmt1, fmt2);
	}
	
	@Test public void textPlainTest() {
		TableColumnFormat fmt1 =
			new TableColumnFormat(TextNode.class, NodeFormat.DEFAULT);
		TableColumnFormat fmt2 = TableColumnFormat.factory("text");
		assertEquals(fmt1, fmt2);
	}
	
	@Test public void textIntTest() {
		TableColumnFormat fmt1 =
			new TableColumnFormat(TextNode.class, NodeFormat.KEEP_NUMBERS);
		TableColumnFormat fmt2 = TableColumnFormat.factory("text=int");
		assertEquals(fmt1, fmt2);
	}
	
	@Test public void textFloatTest() {
		TableColumnFormat fmt1 =
			new TableColumnFormat(TextNode.class, NodeFormat.PARSE_FLOAT);
		TableColumnFormat fmt2 = TableColumnFormat.factory("text=float");
		assertEquals(fmt1, fmt2);
	}
	
	@Test public void linkPlainTest() {
		TableColumnFormat fmt1 =
			new TableColumnFormat(LinkTag.class, TagFormat.HREF);
		TableColumnFormat fmt2 = TableColumnFormat.factory("link");
		assertEquals(fmt1, fmt2);
	}
	
	@Test public void linkSongIdTest() {
		TableColumnFormat fmt1 =
			new TableColumnFormat(LinkTag.class, Formats.SONG_ID_HREF);
		TableColumnFormat fmt2 = TableColumnFormat.factory("link=songid");
		assertEquals(fmt1, fmt2);
	}
	
	@Test public void imgPlainTest() {
		TableColumnFormat fmt1 =
			new TableColumnFormat(ImageTag.class, TagFormat.SRC);
		TableColumnFormat fmt2 = TableColumnFormat.factory("img");
		assertEquals(fmt1, fmt2);
	}
	
	@Test public void imgRatingTest() {
		TableColumnFormat fmt1 =
			new TableColumnFormat(ImageTag.class, Formats.STAR_RATING_SRC);
		TableColumnFormat fmt2 = TableColumnFormat.factory("img=rating");
		assertEquals(fmt1, fmt2);
	}
	
	@Test public void multiTest1() {
		// img=rating~text=float
		TableColumnFormat fmt1 = new TableColumnFormat();
		fmt1.addType(ImageTag.class, Formats.STAR_RATING_SRC);
		fmt1.addType(TextNode.class, NodeFormat.PARSE_FLOAT);
		TableColumnFormat fmt2 = TableColumnFormat.factory("img=rating~text=float");
		assertEquals(fmt1, fmt2);
	}
	
	@Test public void multiTest2() {
		// text=int,-,float
		TableColumnFormat fmt1 = new TableColumnFormat();
		fmt1.addType(TextNode.class,
			NodeFormat.KEEP_NUMBERS,
			null,
			NodeFormat.PARSE_FLOAT);
		TableColumnFormat fmt2 = TableColumnFormat.factory("text=int,-,float");
		assertEquals(fmt1, fmt2);
	}
	
	@Test(expected=UndefinedFormatTypeException.class)
	public void badFormatTest1() {
		TableColumnFormat.factory("undefinednodetype");
	}
			       
	@Test(expected=InvalidFormatArgumentException.class)
	public void badFormatTest2() {
		TableColumnFormat.factory("text=invalidarg");
	}
	
	@Test(expected=InvalidFormatException.class)
	public void badFormatTest3() {
		TableColumnFormat.factory("text=too=many=args");
	}
}

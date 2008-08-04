package jshm.sh.scraper.format;

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
			new TableColumnFormat(LinkTag.class, TagFormat.SONG_ID_HREF);
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
			new TableColumnFormat(ImageTag.class, TagFormat.STAR_RATING_SRC);
		TableColumnFormat fmt2 = TableColumnFormat.factory("img=rating");
		assertEquals(fmt1, fmt2);
	}
	
	@Test public void multiTest1() {
		// img=rating~text=float
		TableColumnFormat fmt1 = new TableColumnFormat();
		fmt1.addType(ImageTag.class, TagFormat.STAR_RATING_SRC);
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

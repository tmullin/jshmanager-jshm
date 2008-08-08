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

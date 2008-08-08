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

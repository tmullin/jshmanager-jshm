package jshm.sh.scraper.format;

import org.junit.*;
import static org.junit.Assert.*;

import org.htmlparser.nodes.*;

public class TagFormatTest {
	TagNode node;
	
	@Before public void setUp() {
		node = new TagNode();
		node.setAttribute("href", "http://doesntmatter/woo.php?foo=bar&song=12345&ladada");
		node.setAttribute("src", "images/somewhere/rating_6.gif");
	}
	
	@After public void tearDown() {
		node = null;
	}
	
	@Test public void starRatingTest1() {
		assertEquals("6", TagFormat.STAR_RATING_SRC.getText(node));
	}
	
	@Test public void starRatingTest2() {
		node.setAttribute("src", "some/value/that/doesnt/match");
		assertEquals("", TagFormat.STAR_RATING_SRC.getText(node));
	}
	
	@Test public void songIdTest1() {
		assertEquals("12345", TagFormat.SONG_ID_HREF.getText(node));
	}
	
	@Test public void songIdTest2() {
		node.setAttribute("href", "another/value/that/doesnt/match");
		assertEquals("", TagFormat.SONG_ID_HREF.getText(node));
	}
}

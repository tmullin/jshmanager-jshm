package jshm.sh.scraper;

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
}

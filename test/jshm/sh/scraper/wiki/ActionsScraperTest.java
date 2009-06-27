package jshm.sh.scraper.wiki;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import jshm.exceptions.ScraperException;

import org.junit.*;

import static org.junit.Assert.*;

@SuppressWarnings("unused")
public class ActionsScraperTest {
	private static Reader getReader(final String s) {
//		try {
			return
			new StringReader(s);
//			new InputStreamReader(
//				new ByteArrayInputStream(
//					s.getBytes("ISO-8859-1")), "ISO-8859-1");
//		} catch (UnsupportedEncodingException e) {
//			throw new RuntimeException(e);
//		}
	}
	
	@Test public void empty() throws IOException, ScraperException {
		Reader reader = getReader("");
		
		Map<String, List<Action>> ret = ActionsScraper.scrape(reader);
		
		assertNotNull(ret);
		assertEquals(0, ret.size());
	}
	
	@Test public void noActions() throws IOException, ScraperException {
		Reader reader = getReader("random test 1234 1234 abcd stuff...!!@#$");
		
		Map<String, List<Action>> ret = ActionsScraper.scrape(reader);
		
		assertNotNull(ret);
		assertEquals(0, ret.size());
	}
	
	@Test public void justName() throws IOException, ScraperException {
		Reader reader = getReader("{{test}}");
		
		Map<String, List<Action>> ret = ActionsScraper.scrape(reader);
		
		assertNotNull(ret);
		List<Action> list = ret.get("test");
		assertNotNull(list);
		assertEquals(1, list.size());
		Action action = list.get(0);
		assertNotNull(action);
		assertNotNull(action.args);
		assertEquals(0, action.args.size());
	}
	
	@Test public void justNameAndSpace() throws IOException, ScraperException {
		Reader reader = getReader("{{test  }}");
		
		Map<String, List<Action>> ret = ActionsScraper.scrape(reader);
		
		assertNotNull(ret);
		List<Action> list = ret.get("test");
		assertNotNull(list);
		assertEquals(1, list.size());
		Action action = list.get(0);
		assertNotNull(action);
		assertNotNull(action.args);
		assertEquals(0, action.args.size());
	}
	
//	@Test
//	public void nameNoCloseBrace() throws IOException, ScraperException {
//		ByteArrayInputStream istream = new ByteArrayInputStream(
//			"{{test  ".getBytes()
//		);
//		
//		Map<String, List<Action>> ret = ActionsScraper.scrape(istream);
//		
//		assertNotNull(ret);
//		assertEquals(0, ret.size());
//	}
	
	@Test(expected=ScraperException.class)
	public void noEquals() throws IOException, ScraperException {
		Reader reader = getReader("{{test arg\"value\"}}");
		
		ActionsScraper.scrape(reader);
	}
	
	@Test(expected=ScraperException.class)
	public void argNoValue() throws IOException, ScraperException {
		Reader reader = getReader("{{test  arg  }}");
		
		ActionsScraper.scrape(reader);
	}
	
//	@Test(expected=ScraperException.class)
//	public void argRogueEquals() throws IOException, ScraperException {
//		ByteArrayInputStream istream = new ByteArrayInputStream(
//			"{{test  = }}".getBytes()
//		);
//		
//		ActionsScraper.scrape(istream);
//	}
	
	@Test public void oneArg() throws IOException, ScraperException {
		Reader reader = getReader("{{test arg=\"value\"}}");
		
		Map<String, List<Action>> ret = ActionsScraper.scrape(reader);
		
		assertNotNull(ret);
		List<Action> list = ret.get("test");
		assertNotNull(list);
		assertEquals(1, list.size());
		Action action = list.get(0);
		assertNotNull(action);
		assertNotNull(action.args);
		assertEquals(1, action.args.size());
		String value = action.get("arg");
		assertNotNull(value);
		assertEquals("value", value);
	}
	
	@Test public void twoArg() throws IOException, ScraperException {
		Reader reader = getReader("{{test \n  argOne=\"value1\"  \n argTwo=\"value2\"}}");
		
		Map<String, List<Action>> ret = ActionsScraper.scrape(reader);
		
		assertNotNull(ret);
		List<Action> list = ret.get("test");
		assertNotNull(list);
		assertEquals(1, list.size());
		Action action = list.get(0);
		assertNotNull(action);
		assertNotNull(action.args);
		assertEquals(2, action.args.size());
		String value = action.get("argOne");
		assertNotNull(value);
		assertEquals("value1", value);
		value = action.get("argTwo");
		assertNotNull(value);
		assertEquals("value2", value);
	}
	
	@Test public void twoArgNoSpace() throws IOException, ScraperException {
		Reader reader = getReader("{{test argOne=\"value1\"argTwo=\"value2\"  }}");
		
		Map<String, List<Action>> ret = ActionsScraper.scrape(reader);
		
		assertNotNull(ret);
		List<Action> list = ret.get("test");
		assertNotNull(list);
		assertEquals(1, list.size());
		Action action = list.get(0);
		assertNotNull(action);
		assertNotNull(action.args);
		assertEquals(2, action.args.size());
		String value = action.get("argOne");
		assertNotNull(value);
		assertEquals("value1", value);
		value = action.get("argTwo");
		assertNotNull(value);
		assertEquals("value2", value);
	}
	
	@Test public void twoArgNoCloseQuote() throws IOException, ScraperException {
		Reader reader = getReader("{{test argOne=\"value1\" argTwo=\"value2}}");
		
		Map<String, List<Action>> ret = ActionsScraper.scrape(reader);
		
		assertNotNull(ret);
		List<Action> list = ret.get("test");
		assertNotNull(list);
		assertEquals(1, list.size());
		Action action = list.get(0);
		assertNotNull(action);
		assertNotNull(action.args);
		assertEquals(2, action.args.size());
		String value = action.get("argOne");
		assertNotNull(value);
		assertEquals("value1", value);
		value = action.get("argTwo");
		assertNotNull(value);
		assertEquals("value2", value);
	}
	
	@Test public void twoArgNoCloseQuoteWithSpace() throws IOException, ScraperException {
		Reader reader = getReader("{{test argOne=\"value1\" argTwo=\"value2   }}");
		
		Map<String, List<Action>> ret = ActionsScraper.scrape(reader);
		
		assertNotNull(ret);
		List<Action> list = ret.get("test");
		assertNotNull(list);
		assertEquals(1, list.size());
		Action action = list.get(0);
		assertNotNull(action);
		assertNotNull(action.args);
		assertEquals(2, action.args.size());
		String value = action.get("argOne");
		assertNotNull(value);
		assertEquals("value1", value);
		value = action.get("argTwo");
		assertNotNull(value);
		assertEquals("value2", value);
	}
	
	@Test public void htmlEntity() throws IOException, ScraperException {
		Reader reader = getReader("{{test arg=\"Pronounced 'L&#277;h-'Nérd 'Skin-'Nérd\"}}");
		
		Map<String, List<Action>> ret = ActionsScraper.scrape(reader);
		
		assertNotNull(ret);
		List<Action> list = ret.get("test");
		assertNotNull(list);
		assertEquals(1, list.size());
		Action action = list.get(0);
		assertNotNull(action);
		assertNotNull(action.args);
		assertEquals(1, action.args.size());
		String value = action.get("arg");
		assertNotNull(value);
		assertEquals("Pronounced 'L\u0115h-'Nérd 'Skin-'Nérd", value);
	}
	
	@Test public void ampersand() throws IOException, ScraperException {
		Reader reader = getReader("{{test arg=\"value & &amp; value\"}}");
		
		Map<String, List<Action>> ret = ActionsScraper.scrape(reader);
		
		assertNotNull(ret);
		List<Action> list = ret.get("test");
		assertNotNull(list);
		assertEquals(1, list.size());
		Action action = list.get(0);
		assertNotNull(action);
		assertNotNull(action.args);
		assertEquals(1, action.args.size());
		String value = action.get("arg");
		assertNotNull(value);
		assertEquals("value & & value", value);
	}
}

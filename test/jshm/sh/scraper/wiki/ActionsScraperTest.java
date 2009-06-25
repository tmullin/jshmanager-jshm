package jshm.sh.scraper.wiki;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import jshm.exceptions.ScraperException;

import org.junit.*;

import static org.junit.Assert.*;

public class ActionsScraperTest {	
	@Test public void empty() throws IOException, ScraperException {
		ByteArrayInputStream istream = new ByteArrayInputStream(
			"".getBytes()
		);
		
		Map<String, List<Action>> ret = ActionsScraper.scrape(istream);
		
		assertNotNull(ret);
		assertEquals(0, ret.size());
	}
	
	@Test public void noActions() throws IOException, ScraperException {
		ByteArrayInputStream istream = new ByteArrayInputStream(
			"random test 1234 1234 abcd stuff...!!@#$".getBytes()
		);
		
		Map<String, List<Action>> ret = ActionsScraper.scrape(istream);
		
		assertNotNull(ret);
		assertEquals(0, ret.size());
	}
	
	@Test public void justName() throws IOException, ScraperException {
		ByteArrayInputStream istream = new ByteArrayInputStream(
			"{{test}}".getBytes()
		);
		
		Map<String, List<Action>> ret = ActionsScraper.scrape(istream);
		
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
		ByteArrayInputStream istream = new ByteArrayInputStream(
			"{{test  }}".getBytes()
		);
		
		Map<String, List<Action>> ret = ActionsScraper.scrape(istream);
		
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
		ByteArrayInputStream istream = new ByteArrayInputStream(
			"{{test arg\"value\"}}".getBytes()
		);
		
		ActionsScraper.scrape(istream);
	}
	
	@Test(expected=ScraperException.class)
	public void argNoValue() throws IOException, ScraperException {
		ByteArrayInputStream istream = new ByteArrayInputStream(
			"{{test  arg  }}".getBytes()
		);
		
		ActionsScraper.scrape(istream);
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
		ByteArrayInputStream istream = new ByteArrayInputStream(
			"{{test arg=\"value\"}}".getBytes()
		);
		
		Map<String, List<Action>> ret = ActionsScraper.scrape(istream);
		
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
		ByteArrayInputStream istream = new ByteArrayInputStream(
			"{{test \n  argOne=\"value1\"  \n argTwo=\"value2\"}}".getBytes()
		);
		
		Map<String, List<Action>> ret = ActionsScraper.scrape(istream);
		
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
		ByteArrayInputStream istream = new ByteArrayInputStream(
			"{{test argOne=\"value1\"argTwo=\"value2\"  }}".getBytes()
		);
		
		Map<String, List<Action>> ret = ActionsScraper.scrape(istream);
		
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
		ByteArrayInputStream istream = new ByteArrayInputStream(
			"{{test argOne=\"value1\" argTwo=\"value2}}".getBytes()
		);
		
		Map<String, List<Action>> ret = ActionsScraper.scrape(istream);
		
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
		ByteArrayInputStream istream = new ByteArrayInputStream(
			"{{test argOne=\"value1\" argTwo=\"value2   }}".getBytes()
		);
		
		Map<String, List<Action>> ret = ActionsScraper.scrape(istream);
		
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
}

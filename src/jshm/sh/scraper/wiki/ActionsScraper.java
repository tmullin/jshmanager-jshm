package jshm.sh.scraper.wiki;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import jshm.exceptions.ScraperException;
import jshm.sh.Client;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;

/**
 * This class serves to scrape actions from the wiki into a useful form.
 * @author Tim Mullin
 *
 */
public class ActionsScraper {
	static final Logger LOG = Logger.getLogger(ActionsScraper.class.getName());
	
	static {
		LOG.setLevel(Level.INFO);
	}
	
	public static Map<String, List<Action>> scrape(final String wikiUrl) throws IOException, ScraperException {
		return scrape(wikiUrl, new HashMap<String, List<Action>>());
	}
	
	public static Map<String, List<Action>> scrape(String wikiUrl, final Map<String, List<Action>> ret) throws IOException, ScraperException {
		if (!wikiUrl.endsWith("/raw"))
			wikiUrl = wikiUrl + "/raw";
		
		HttpClient client = Client.getHttpClient();
		GetMethod method = new GetMethod(wikiUrl);
		client.executeMethod(method);
		
		return scrape(method.getResponseBodyAsStream(), ret);
	}
	
	public static Map<String, List<Action>> scrape(final InputStream istream) throws IOException, ScraperException {
		return scrape(istream, new HashMap<String, List<Action>>());
	}
	
	public static Map<String, List<Action>> scrape(final InputStream istream, final Map<String, List<Action>> ret) throws IOException, ScraperException {
		if (null == ret)
			throw new NullPointerException("ret");
		
		BufferedReader in = new BufferedReader(
			new InputStreamReader(istream));
		StringBuilder sb = null;
		String lastKey = null;
		Action action = null;
		
		int c = -1,
		leftBraceCount = 0,
		rightBraceCount = 0;
		boolean isQuotedString = false;
		Expect expect = Expect.LEFT_BRACE;
		
		while (-1 != (c = in.read())) {
			switch (expect) {
				case LEFT_BRACE:
					if ('{' == c) {
						leftBraceCount++;
						
						if (2 == leftBraceCount) {
							LOG.finer("got 2 left braces, expecting name");
							expect = Expect.NAME;
							action = new Action();
							sb = new StringBuilder();
							isQuotedString = false;
						}
					}
					
					// skipping non action block
					continue;
					
				case NAME:
					if (Character.isLetter(c)) {
						sb.append((char) c);
						continue;
					} else if ('}' == c) {
						// no key/value pairs
						rightBraceCount++;
						action.name = sb.toString();
						if (null == ret.get(action.name))
							ret.put(action.name, new ArrayList<Action>());
						ret.get(action.name).add(action);
						expect = Expect.RIGHT_BRACE;

						LOG.finer("got right brace after name (" + action.name + "), action has no args");
						continue;
					} else if (Character.isWhitespace(c)) {
						// we've read some characters for the name
						if (0 != sb.length()) {
							action.name = sb.toString();
							if (null == ret.get(action.name))
								ret.put(action.name, new ArrayList<Action>());
							ret.get(action.name).add(action);
							expect = Expect.KEY;
							sb = new StringBuilder();
							isQuotedString = false;							

							LOG.finer("got whitespace after name (" + action.name + ")");
						}
						
						// else there's whitespace before the name
						
						continue;
					}
					
					throw new ScraperException("expecting next letter in name or whitespace after, got: " + (char) c);
					
				case KEY:
					if (Character.isLetter(c)) {
						sb.append((char) c);
						continue;
					} else if (Character.isWhitespace(c) && 0 == sb.length()) {
						// extra whitespace between last thing and this key 
						continue;
					} else if ('}' == c) {
						if (0 != sb.length())
							throw new ScraperException("expecting next letter in key but got right brace");
						
						LOG.finer("got right brace after last value");
						
						rightBraceCount++;
						expect = Expect.RIGHT_BRACE;
						continue;
					} else if ('=' == c) {
						lastKey = sb.toString();
						
						LOG.finer("got equals sign after key (" + lastKey + ")");
						
						expect = Expect.VALUE;
						sb = new StringBuilder();
						isQuotedString = false;
						continue;
					}
					
					throw new ScraperException("expecting next letter in key or equals sign, got: " + (char) c);
					
				case VALUE:
					if (isQuotedString && '"' != c && '}' != c) {
						sb.append((char) c);
						continue;
					} else if ('"' == c || (isQuotedString && '}' == c)) {
						if ('}' == c) {
							// malformed, no end quote
							isQuotedString = false;
						} else {
							isQuotedString = !isQuotedString;
						}
						
						if (isQuotedString) {
							LOG.finest("got opening quote for value of key (" + lastKey + ")");
						} else {
							String value =
								org.htmlparser.util.Translate.decode(
									sb.toString().trim());
							
							LOG.finer("got closing quote for value (" + value + ")");
							
							action.args.put(lastKey, value);
							
							if ('}' == c) {
								expect = Expect.RIGHT_BRACE;
								rightBraceCount++;
							} else {
								expect = Expect.KEY;
							}
							
							lastKey = null;
							sb = new StringBuilder();
							isQuotedString = false;
						}
						
						continue;
					} else if (Character.isWhitespace(c)) {
						LOG.finer("got whitespace before value for key (" + lastKey + ")");
						expect = Expect.KEY;
						lastKey = null;
						sb = new StringBuilder();
						isQuotedString = false;
						
						continue;
					}
					
					throw new ScraperException("expecting quote or next letter in value, got: " + (char) c);
					
				case RIGHT_BRACE:
					if ('}' == c) {
						rightBraceCount++;
						
						if (2 == rightBraceCount) {
							LOG.finer("got 2nd right brace");
							leftBraceCount = rightBraceCount = 0;
							expect = Expect.LEFT_BRACE;
							continue;
						}
					}
					
					throw new ScraperException("expecting 2nd right brace, got " + (char) c);
			}
		}
		
		
		return ret;
	}
	
	private static enum Expect {
		LEFT_BRACE,
		NAME,
		KEY,
		VALUE,
		RIGHT_BRACE
	}
}

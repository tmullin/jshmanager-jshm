/*
 * -----LICENSE START-----
 * JSHManager - A Java-based tool for managing one's ScoreHero account.
 * Copyright (C) 2008, 2009 Tim Mullin
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
package jshm.sh.scraper.wiki;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
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
		LOG.setLevel(Level.FINE);
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
		
		if (method.getStatusCode() != 200) {
			LOG.warning("Non-200 response for " + wikiUrl + " - " + method.getStatusLine());
			return ret;
		}
		
		String charset = method.getResponseCharSet();
		LOG.fine("Charset for HTTP response is: " + charset);
		if (null == charset || charset.isEmpty())
			charset = "ISO-8859-1";
		
		return scrape(
			new InputStreamReader(method.getResponseBodyAsStream(), charset),
			ret);
	}
	
	public static Map<String, List<Action>> scrape(final Reader reader) throws IOException, ScraperException {
		return scrape(reader, new HashMap<String, List<Action>>());
	}
	
	public static Map<String, List<Action>> scrape(final Reader reader, final Map<String, List<Action>> ret) throws IOException, ScraperException {
		if (null == ret)
			throw new NullPointerException("ret");
		
		BufferedReader in = reader instanceof BufferedReader
		? (BufferedReader) reader
		: new BufferedReader(reader);
		
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

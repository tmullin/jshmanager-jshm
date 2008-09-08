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
package jshm.scraper;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.htmlparser.*;
import org.htmlparser.http.*;
import org.htmlparser.beans.FilterBean;
import org.htmlparser.filters.*;
import org.htmlparser.nodes.*;
import org.htmlparser.util.*;

/**
 * This class serves to scrape data from a URL.
 * @author Tim Mullin
 *
 */
public class Scraper {
	static final Logger LOG = Logger.getLogger(Scraper.class.getName());
	
	public static NodeList scrape(final String url, final jshm.scraper.DataTable dataTable) throws ParserException {
		return scrape(url, dataTable.getFilters(), true);
	}
	
	public static NodeList scrape(final String url, final jshm.scraper.DataTable dataTable, final boolean removeWhitespace) throws ParserException {
		return scrape(url, dataTable.getFilters(), removeWhitespace);
	}
	
	public static NodeList scrape(
			final String url,
			final jshm.scraper.DataTable dataTable,
			final org.apache.commons.httpclient.Cookie[] cookies) throws ParserException {
			
			return scrape(url, dataTable.getFilters(), true, cookies);
		}
	
	public static NodeList scrape(
		final String url,
		final jshm.scraper.DataTable dataTable,
		final boolean removeWhitespace,
		final org.apache.commons.httpclient.Cookie[] cookies) throws ParserException {
		
		return scrape(url, dataTable.getFilters(), removeWhitespace, cookies);
	}
	
	public static NodeList scrape(final String url, final NodeFilter[] filters) throws ParserException {
		return scrape(url, filters, true);
	}
	
	public static NodeList scrape(final String url, final NodeFilter[] filters, final boolean removeWhitespace) throws ParserException {
		return scrape(url, filters, removeWhitespace, null);
	}
	
	/**
	 * Retrieves a NodeList of all elements of interest
	 * (and children) from the supplied URL.
	 * @param url
	 * @param filters The set of NodeFilters to use to find the 
	 * elements of interest. These could be different depending on the page
	 * format between Guitar Hero and Rockband scores, for example.
	 * @param removeWhitespace Whether to remove TextNodes that
	 * contain only whitespace
	 * @param cookies Any cookies that should be passed along with
	 * the request
	 * @return
	 * @throws ParserException 
	 */
	public static NodeList scrape(
		final String url,
		final NodeFilter[] filters,
		final boolean removeWhitespace,
		final org.apache.commons.httpclient.Cookie[] cookies) throws ParserException {
		
		LOG.finest("entered Scraper.scrape()");
		
        FilterBean bean = new FilterBean();
        bean.getParser().setFeedback(PARSER_FEEDBACK);
        
        bean.setFilters(filters);
        
        // set cookies if necessary
        if (null != cookies) {
        	LOG.fine("Setting cookies");
        	ConnectionManager cm = Parser.getConnectionManager();
        	Cookie cookie = null;
        	
        	// have to convert from commons cookie to htmlparser cookie
        	for (org.apache.commons.httpclient.Cookie c : cookies) {
        		LOG.finer("  " + c);
        		cookie = new Cookie(c.getName(), c.getValue());
        		cm.setCookie(cookie, c.getDomain());
        	}
        }
        
        LOG.finest("calling bean.setURL()");
        bean.setURL(url);
        LOG.finest("calling bean.getNodes()");
        NodeList nodes = bean.getNodes();
        
        if (removeWhitespace) {
        	LOG.finer("Removing whitespace from retrieved nodes");
        	
	        // now filter out text nodes that only have whitespace {{{
	        RegexFilter filter8 = new RegexFilter ();
	        filter8.setStrategy (RegexFilter.MATCH);
	        filter8.setPattern ("^\\s*$");
	        NodeClassFilter filter9 = new NodeClassFilter(TextNode.class);
	        AndFilter filter10 = new AndFilter(new NodeFilter[]{
	        	filter9, filter8
	        });
	        NotFilter filter11 = new NotFilter(filter10);

	        LOG.finest("calling nodes.keepAllNodesThatMatch()");
	        nodes.keepAllNodesThatMatch(filter11, true);
        }
        
        LOG.finest("returning from Scraper.scrape()");
        return nodes;
	}
	
	public static final MyParserFeedback PARSER_FEEDBACK = new MyParserFeedback();
	
	public static class MyParserFeedback implements ParserFeedback {
		final Logger LOG = Logger.getLogger("org.htmlparser");
		
		@Override
		public void error(String message, ParserException e) {
			LOG.log(Level.SEVERE, message, e);
		}

		@Override
		public void info(String message) {
			LOG.info(message);
		}

		@Override
		public void warning(String message) {
			LOG.warning(message);
		}
	}
}

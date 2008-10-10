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
package jshm.sh.scraper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.htmlparser.NodeFilter;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.HasChildFilter;
import org.htmlparser.filters.HasParentFilter;
import org.htmlparser.filters.HasSiblingFilter;
import org.htmlparser.filters.StringFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.htmlparser.util.SimpleNodeIterator;

import jshm.*;
import jshm.Instrument.Group;
import jshm.exceptions.ScraperException;
import jshm.scraper.Scraper;
import jshm.sh.URLs;
import jshm.sh.links.Link;

public class WikiSpScraper {
	static final Logger LOG = Logger.getLogger(WikiSpScraper.class.getName());
	
	static final NodeFilter[] FILTERS;
	
	static {
		FILTERS = new NodeFilter[] {
			new AndFilter(new NodeFilter[] {
				new TagNameFilter("DIV"),
				new HasAttributeFilter("class", "page")
			})
		};
	}
	
	static final NodeFilter BLANK_CHART_FILTER;
	
	static {
		BLANK_CHART_FILTER =
		new AndFilter(new NodeFilter[] {
			new TagNameFilter("A"),
			new HasParentFilter(
				new AndFilter(new NodeFilter[] {
					new TagNameFilter("TD"),
					new HasSiblingFilter(
						new AndFilter(new NodeFilter[] {
							new TagNameFilter("TH"),
							new HasChildFilter(
								new StringFilter("Chart")
							)
						})
					)
				})
			)
		});
	}
	
	static final NodeFilter EXT_LINK_FILTER;
	
	static {
		EXT_LINK_FILTER = new AndFilter(new NodeFilter[] {
			new HasAttributeFilter("class", "ext"),
			new TagNameFilter("A")
		});
	}

	static Map<String, List<Link>> cache = new HashMap<String, List<Link>>();
	
	public static List<Link> scrape(Song song, Group group, Difficulty diff) throws ScraperException, ParserException {
		return scrape(true, song, group, diff);
	}
	
	public static List<Link> scrape(boolean useCache, Song song, Group group, Difficulty diff) throws ScraperException, ParserException {
		final String cacheKey = String.format("%s_%s_%s",
			song.getScoreHeroId(), group, diff); 
		
		LOG.finer("scraping sp images for " + cacheKey);
		
		if (useCache && null != cache.get(cacheKey)) {
			LOG.finest("returning cached data");
			return cache.get(cacheKey);
		}
		
		List<Link> ret = new ArrayList<Link>();
		
		NodeList nodes = Scraper.scrape(
			URLs.wiki.getSongSpUrl(song, group, diff),
			FILTERS);
		
		if (nodes.size() != 1)
			throw new ScraperException("expected 1 node, got " + nodes.size());
				
		// first look for the blank chart
		NodeList chartNodes = 
			nodes.extractAllNodesThatMatch(BLANK_CHART_FILTER, true);
		
//		System.out.println(chartNodes.toHtml());
		
		if (chartNodes.size() == 1) {
			String href = ((LinkTag) chartNodes.elementAt(0)).getAttribute("href");
			
//			System.out.println(href);
			LOG.finest("found blank chart: " + href);
			
			if (null != href)
				ret.add(new Link("Blank Chart", href));
		}/* else {
			// no blank chart is available
		}*/
		
		
		// now for any other image links
		NodeList linkNodes =
			nodes.extractAllNodesThatMatch(EXT_LINK_FILTER, true);
		
		SimpleNodeIterator it = linkNodes.elements();
		
		while (it.hasMoreNodes()) {
			LinkTag cur = (LinkTag) it.nextNode();
			
			// trim non-alphanumeric chars
			String title = cur.getChildrenHTML()
				.replaceFirst("^[^\\p{L}\\p{N}]+", "")
				.replaceFirst("[^\\p{L}\\p{N}]+$", "");;

			ret.add(new Link(
				title,
				cur.getAttribute("href")
			));
		}
		
//		System.out.println(linkNodes.toHtml());
		
		LOG.finest("found " + ret.size() + " images in total");
		cache.put(cacheKey, ret);
		
		return ret;
	}
}
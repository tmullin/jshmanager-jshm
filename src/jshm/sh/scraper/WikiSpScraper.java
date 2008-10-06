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
		
		if (useCache && null != cache.get(cacheKey))
			return cache.get(cacheKey);
		
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
			ret.add(new Link(
				cur.getChildrenHTML(),
				cur.getAttribute("href")
			));
		}
		
//		System.out.println(linkNodes.toHtml());
		
		cache.put(cacheKey, ret);
		
		return ret;
	}
}

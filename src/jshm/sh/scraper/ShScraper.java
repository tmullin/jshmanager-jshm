package jshm.sh.scraper;

import org.htmlparser.NodeFilter;
import org.htmlparser.util.NodeList;

/**
 * This class is a helper for scraping stuff from ScoreHero
 * specifically. It takes care of getting auth cookies.
 * @author Tim Mullin
 *
 */
public class ShScraper {
	public static NodeList scrape(final String url, final NodeFilter[] filters) {
		return scrape(url, filters, true);
	}
	
	public static NodeList scrape(final String url, final NodeFilter[] filters, final boolean removeWhitespace) {
		return jshm.scraper.Scraper.scrape(url, filters, removeWhitespace, jshm.sh.Client.getAuthCookies());
	}
}

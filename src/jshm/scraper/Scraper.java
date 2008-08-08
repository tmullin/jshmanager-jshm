package jshm.scraper;

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
	public static NodeList scrape(final String url, final NodeFilter[] filters) {
		return scrape(url, filters, true);
	}
	
	public static NodeList scrape(final String url, final NodeFilter[] filters, final boolean removeWhitespace) {
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
	 */
	public static NodeList scrape(
		final String url,
		final NodeFilter[] filters,
		final boolean removeWhitespace,
		final org.apache.commons.httpclient.Cookie[] cookies) {
		
        FilterBean bean = new FilterBean ();
        bean.setFilters (filters);
        
        // set cookies if necessary
        if (null != cookies) {
        	ConnectionManager cm = Parser.getConnectionManager();
        	Cookie cookie = null;
        	
        	// have to convert from commons cookie to htmlparser cookie
        	for (org.apache.commons.httpclient.Cookie c : cookies) {
        		cookie = new Cookie(c.getName(), c.getValue());
        		cm.setCookie(cookie, c.getDomain());
        	}
        }
        
        bean.setURL(url);
        NodeList nodes = bean.getNodes();
        
        if (removeWhitespace) {
	        // now filter out text nodes that only have whitespace {{{
	        RegexFilter filter8 = new RegexFilter ();
	        filter8.setStrategy (RegexFilter.MATCH);
	        filter8.setPattern ("^\\s*$");
	        NodeClassFilter filter9 = new NodeClassFilter(TextNode.class);
	        AndFilter filter10 = new AndFilter(new NodeFilter[]{
	        	filter9, filter8
	        });
	        NotFilter filter11 = new NotFilter(filter10);

	        nodes.keepAllNodesThatMatch(filter11, true);
        }
        
        return nodes;
	}
}

package jshm.sh.gh.scraper;

import org.htmlparser.*;
import org.htmlparser.http.*;
import org.htmlparser.beans.FilterBean;
import org.htmlparser.filters.*;
import org.htmlparser.nodes.*;
import org.htmlparser.util.*;

/**
 * This class serves to scrape song data from ScoreHero.
 * @author Tim
 *
 */
public class Scraper {
	/**
	 * Retrieves a NodeList of all TR elements of interest 
	 * (and children) from the supplied URL. TextNodes
	 * that contain only whitespace are removed.
	 * @param url
	 * @return
	 */
	public static NodeList scrape(final String url) {
		return scrape(url, true, jshm.sh.Client.getAuthCookies());
	}
	
	public static NodeList scrape(
		final String url,
		final org.apache.commons.httpclient.Cookie[] cookies) {
		return scrape(url, true, cookies);
	}
	
	/**
	 * Retrieves a NodeList of all TR elements of interest
	 * (and children) from the supplied URL.
	 * @param url
	 * @param removeWhitespace Whether to remove TextNodes that
	 * contain only whitespace
	 * @param cookies Any cookies that should be passed along with
	 * the request
	 * @return
	 */
	public static NodeList scrape(
		final String url,
		final boolean removeWhitespace,
		final org.apache.commons.httpclient.Cookie[] cookies) {
		// HtmlParser semi auto-generated filter stuff
		
		NodeFilter[] array2 = null;
		
		{
	        TagNameFilter filter0 = new TagNameFilter ();
	        filter0.setName ("TABLE");
	        HasAttributeFilter filter1 = new HasAttributeFilter ();
	        filter1.setAttributeName ("border");
	        filter1.setAttributeValue ("1");
	        HasAttributeFilter filter2 = new HasAttributeFilter ();
	        filter2.setAttributeName ("cellspacing");
	        filter2.setAttributeValue ("0");
	        NodeFilter[] array0 = new NodeFilter[3];
	        array0[0] = filter0;
	        array0[1] = filter1;
	        array0[2] = filter2;
	        AndFilter filter3 = new AndFilter ();
	        filter3.setPredicates (array0);
	        HasParentFilter filter4 = new HasParentFilter ();
	        filter4.setRecursive (false);
	        filter4.setParentFilter (filter3);
	        TagNameFilter filter5 = new TagNameFilter ();
	        filter5.setName ("TR");
	        HasAttributeFilter filter6 = new HasAttributeFilter ();
	        filter6.setAttributeName ("height");
	        filter6.setAttributeValue ("30");
	        NodeFilter[] array1 = new NodeFilter[3];
	        array1[0] = filter4;
	        array1[1] = filter5;
	        array1[2] = filter6;
	        AndFilter filter7 = new AndFilter ();
	        filter7.setPredicates (array1);
	        
	        array2 = new NodeFilter[1];
	        array2[0] = filter7;
		}
		
        FilterBean bean = new FilterBean ();
        bean.setFilters (array2);
        
        // }}}
        
        // set cookies if necessary
        if (null != cookies) {
        	ConnectionManager cm = Parser.getConnectionManager();
        	Cookie cookie = null;
        	
        	// have to convert from commons cookie to htmlparser cookie
        	for (org.apache.commons.httpclient.Cookie c : cookies) {
        		cookie = new Cookie(c.getName(), c.getValue());
        		cookie.setDomain(c.getDomain());
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

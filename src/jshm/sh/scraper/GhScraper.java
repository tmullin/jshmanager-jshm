package jshm.sh.scraper;

import jshm.scraper.Scraper;

import org.htmlparser.*;
import org.htmlparser.filters.*;
import org.htmlparser.util.*;

public class GhScraper {
	public static final NodeFilter[] filters;
	
	/**
	 * This sets up the filters for the Guitar Hero page
	 * formatting.
	 */
	static {
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
        
        filters = new NodeFilter[1];
        filters[0] = filter7;
	}
	
	public static NodeList scrape(String url) {
		return scrape(url, true);
	}
	
	public static NodeList scrape(String url, boolean removeWhitespace) {
        return Scraper.scrape(url, filters, removeWhitespace);
	}
}

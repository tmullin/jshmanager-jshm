package jshm.sh;

import org.htmlparser.NodeFilter;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.HasParentFilter;
import org.htmlparser.filters.TagNameFilter;

import jshm.scraper.format.*;

/**
 * This stores some information about the table layouts
 * of the different data tables.
 */
public class DataTable {
	private static final NodeFilter[] DEFAULT_GH_FILTERS;
	
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
        
        DEFAULT_GH_FILTERS = new NodeFilter[1];
        DEFAULT_GH_FILTERS[0] = filter7;
	}
	
	public static final DataTable
	GH_TOP_SCORES = new DataTable(3, 2, 8,
		"-|text~link=songid|text=int|img=rating~text=float|text=int|text=int|text|span"
	),
	GH_MANAGE_SCORES = new DataTable(3, 4, 11,
		"-|-|link=songid~text|-|-|text=int|img=rating~text=float|text=int|text=int|text|span"
	),
	GH_STAR_CUTOFFS = new DataTable(3, 2, 7,
		"link=songid|-|text|text=int,-,int|text=int|text=int|text"
	),
	GH_TOTAL_NOTES = new DataTable(3, 2, 6,
		"link=songid|-|text|text=int|-|-"
	),
	GH_ALL_CUTOFFS = new DataTable(3, 2, 8,
		"text|text=int|text=int|text=int|text=int|text=int|text=int|text=int"
	)
	;
	
	public static final String DEFAULT_HEADER_CSS_CLASS = "headrow";
	public static final TableRowFormat TIER_ROW_FORMAT =
		TableRowFormat.factory("text|-");
	
	public final String headerCssClass;
	public final int tierColspan;
	public final int tierChildNodeCount;
	public final int rowChildNodeCount;
	public final TableRowFormat rowFormat;

	public DataTable(
		final int tierColspan,
		final int tierChildNodeCount,
		final int rowChildNodeCount,
		final String rowFormat) {
		
		this(null, tierColspan, tierChildNodeCount, rowChildNodeCount, rowFormat);
	}
	
	public DataTable(
		final String headerCssClass,
		final int tierColspan,
		final int tierChildNodeCount,
		final int rowChildNodeCount,
		final String rowFormat) {
		
		this.headerCssClass = null != headerCssClass ? headerCssClass : DEFAULT_HEADER_CSS_CLASS;
		this.tierColspan = tierColspan;
		this.tierChildNodeCount = tierChildNodeCount;
		this.rowChildNodeCount = rowChildNodeCount;
		this.rowFormat = TableRowFormat.factory(rowFormat);
	}
	
	/**
	 * 
	 * @return An array of NodeFilters that determines how to find
	 * the relevant table.
	 */
	public NodeFilter[] getFilters() {
		return DEFAULT_GH_FILTERS;
	}
}

package jshm.sh.scraper;

import jshm.scraper.DataTable;

import org.htmlparser.NodeFilter;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.HasParentFilter;
import org.htmlparser.filters.TagNameFilter;

public class GhDataTable extends DataTable {
	public static final GhDataTable
	TOP_SCORES = new GhDataTable(3, 2, 8,
		"-|text~link=songid|text=int|img=rating~text=float|text=int|text=int|text|span"
	),
	MANAGE_SCORES = new GhDataTable(3, 4, 11,
		"-|text=int|link=songid~text|-|-|text=int~link=picvid,picvid|img=rating~text=float|text=int|text=int|text|span"
	),
	MANAGE_SCORES_SINGLE_PLAT = new GhDataTable(3, 3, 10,
		"-|text=int|link=songid~text|-|text=int~link=picvid,picvid|img=rating~text=float|text=int|text=int|text|span"
	),
	STAR_CUTOFFS = new GhDataTable(3, 2, 7,
		"link=songid|-|text|text=int,-,int|text=int|text=int|text"
	),
	TOTAL_NOTES = new GhDataTable(3, 2, 6,
		"link=songid|-|text|text=int|-|-"
	),
	ALL_CUTOFFS = new GhDataTable(3, 2, 8,
		"text|text=int|text=int|text=int|text=int|text=int|text=int|text=int"
	),
	
	DELETE_SCORES = new GhDataTable(-1, -1, 6,
		"input|text=int|img=rating~text=float|text=int|text=int|text"
	) {
		private transient NodeFilter[] filters = null;
		
		@Override
		public NodeFilter[] getFilters() {
			if (null == filters) {
				TagNameFilter filter0 = new TagNameFilter("TABLE");
				HasAttributeFilter filter1 = new HasAttributeFilter("border", "0");
				HasAttributeFilter filter2 = new HasAttributeFilter("cellpadding", "5");
				NodeFilter[] array0 = new NodeFilter[] {
					filter0, filter1, filter2
				};
	
				AndFilter filter3 = new AndFilter();
				filter3.setPredicates(array0);
	
				HasParentFilter filter4 = new HasParentFilter();
				filter4.setRecursive(false);
				filter4.setParentFilter(filter3);
	
				filters = new NodeFilter[] { filter4 };
			}
			
			return filters;
		}
	} //,
	
//	GH_FORUM_INDEX = new DataTable(2, 2, 5,
//		"-|text=,~link=forumid,-,-,subforums|-|-|-")
	;
	
	private static final NodeFilter[] DEFAULT_FILTERS;
	
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
        
        DEFAULT_FILTERS = new NodeFilter[1];
        DEFAULT_FILTERS[0] = filter7;
	}
	
	public GhDataTable(
		final int tierColspan,
		final int tierChildNodeCount,
		final int rowChildNodeCount,
		final String rowFormat) {
		
		super(tierColspan, tierChildNodeCount, rowChildNodeCount, rowFormat);
	}
	
	public NodeFilter[] getFilters() {
		return DEFAULT_FILTERS;
	}
}

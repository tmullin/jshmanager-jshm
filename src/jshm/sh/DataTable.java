package jshm.sh;

import jshm.scraper.format.*;

/**
 * This stores some information about the table layouts
 * of the different data tables.
 */
public class DataTable {
	public static final DataTable
	GH_TOP_SCORES = new DataTable(3, 2, 8,
		"-|text~link=songid|text=int|img=rating~text=float|text=int|text=int|text|text"
	),
	GH_MANAGE_SCORES = new DataTable(3, 4, 11,
		"-|-|text|-|-|text=int|img=rating~text=float|text=int|text=int|text|text"
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
}

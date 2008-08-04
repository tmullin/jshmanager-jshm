package jshm.sh.gh;

import jshm.sh.scraper.format.*;

/**
 * This stores some information about the table layouts
 * of the different data tables.
 */
public enum DataTable {
	MANAGE_SCORES(3, 4, 11,
		"-|-|text|-|-|text=int|img=rating~text=float|text=int|text=int|text|text"
	),
	STAR_CUTOFFS(3, 2, 7,
		"link=songid|-|text|text=int,-,int|text=int|text=int|text"
	),
	TOTAL_NOTES(3, 2, 6,
		"link=songid|-|text|text=int|-|-"
	),
	ALL_CUTOFFS(3, 2, 8,
		"text|text=int|text=int|text=int|text=int|text=int|text=int|text=int"
	)
	;
	
	public static final String HEADER_ROW_CSS_CLASS = "headrow";
	public static final TableRowFormat TIER_ROW_FORMAT =
		TableRowFormat.factory("text|-");
	
	public final int tierColspan;
	public final int tierChildNodeCount;
	public final int rowChildNodeCount;
	public final TableRowFormat rowFormat;
	
	private DataTable(
		final int tierColspan,
		final int tierChildNodeCount,
		final int rowChildNodeCount,
		final String rowFormat) {
		this.tierColspan = tierColspan;
		this.tierChildNodeCount = tierChildNodeCount;
		this.rowChildNodeCount = rowChildNodeCount;
		this.rowFormat = TableRowFormat.factory(rowFormat);
	}
}

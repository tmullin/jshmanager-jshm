package jshm.sh.gh;

/**
 * This stores some information about the table layouts
 * of the different data tables.
 */
public enum DataTable {
	MANAGE_SCORES(3, 4, 11,
		"-|-|text|-|-|text=int|img=src~text=float|text=int|text=int|text|text"
	),
	STAR_CUTOFFS(3, 2, 7,
		"link|-|text|text=int,-,int|text=int|text=int|text"
	),
	TOTAL_NOTES(3, 2, 6,
		"link|-|text|text=int|-|-"
	),
	ALL_CUTOFFS(3, 2, 8,
		"text|text=int|text=int|text=int|text=int|text=int|text=int|text=int"
	)
	;
	
	public final int tierColspan;
	public final int tierChildNodeCount;
	public final int rowChildNodeCount;
	
	private DataTable(
		final int tierColspan,
		final int tierChildNodeCount,
		final int rowChildNodeCount,
		final String tableRowFormat) {
		this.tierColspan = tierColspan;
		this.tierChildNodeCount = tierChildNodeCount;
		this.rowChildNodeCount = rowChildNodeCount;
	}
}

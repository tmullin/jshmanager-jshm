package jshm.scraper;

import jshm.exceptions.ScraperException;
import jshm.sh.DataTable;

import org.htmlparser.tags.TableRow;

public abstract class TieredTabularDataAdapter implements TieredTabularDataHandler {
	protected volatile boolean ignoreNewData = false;
	protected boolean ignoreInvalidRowChildCount = true;
	
	public abstract DataTable getDataTable();
	
	public boolean ignoreNewData() {
		return ignoreNewData;
	}
	
	public boolean ignoreInvalidRowChildCount() {
		return ignoreInvalidRowChildCount;
	}
	
	public void handleDataRow(String[][] data) throws ScraperException {
	}

	public void handleHeaderRow(TableRow tr) throws ScraperException {
	}

	public void handleTierRow(String tierName) throws ScraperException {
	}
}

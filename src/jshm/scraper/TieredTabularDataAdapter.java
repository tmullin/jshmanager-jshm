package jshm.scraper;

import jshm.exceptions.ScraperException;

import org.htmlparser.tags.TableRow;

public abstract class TieredTabularDataAdapter implements TieredTabularDataHandler {
	protected volatile boolean ignoreNewData = false;
	
	public boolean ignoreNewData() {
		return ignoreNewData;
	}
	
	public void handleDataRow(String[][] data) throws ScraperException {
	}

	public void handleHeaderRow(TableRow tr) throws ScraperException {
	}

	public void handleTierRow(String tierName) throws ScraperException {
	}
}

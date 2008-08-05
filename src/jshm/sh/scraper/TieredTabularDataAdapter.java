package jshm.sh.scraper;

import jshm.exceptions.ScraperException;

import org.htmlparser.tags.TableRow;

public abstract class TieredTabularDataAdapter implements TieredTabularDataHandler {
	public void handleDataRow(String[][] data) throws ScraperException {
	}

	public void handleHeaderRow(TableRow tr) throws ScraperException {
	}

	public void handleTierRow(String tierName) throws ScraperException {
	}
}

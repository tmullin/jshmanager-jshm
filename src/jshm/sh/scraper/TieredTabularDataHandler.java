package jshm.sh.scraper;

import org.htmlparser.tags.*;

import jshm.exceptions.ScraperException;

public interface TieredTabularDataHandler {
	public jshm.sh.DataTable getDataTable();
	public void handleHeaderRow(TableRow tr) throws ScraperException;
	public void handleTierRow(String tierName) throws ScraperException;
	public void handleDataRow(String[][] data) throws ScraperException;
}

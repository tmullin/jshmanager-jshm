/*
 * -----LICENSE START-----
 * JSHManager - A Java-based tool for managing one's ScoreHero account.
 * Copyright (C) 2008 Tim Mullin
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * -----LICENSE END-----
 */
package jshm.scraper;

import java.util.logging.Logger;

import org.htmlparser.Node;
import org.htmlparser.util.*;
import org.htmlparser.tags.*;

import jshm.exceptions.ScraperException;

public class TieredTabularDataExtractor {
	static final Logger LOG = Logger.getLogger(TieredTabularDataAdapter.class.getName());
	
	/**
	 * Indicates what to do when a data row's child count
	 * doesn't match what the DataTable specifies.
	 * <ul>
	 *   <li>IGNORE - the row is ignored
	 *   <li>EXCEPTION - an exception is thrown
	 *   <li>HANDLE - the row is handled anyway, it is up to the handler to ensure the row is valid
	 * </li>
	 * @author Tim Mullin
	 *
	 */
	// yeah TieredTabularDataExtractor.InvalidChildCountStrategy is ridiculous...
	public static enum InvalidChildCountStrategy {
		IGNORE, EXCEPTION, HANDLE
	}
	
	public static void extract(
		final NodeList nodes,
		final TieredTabularDataHandler handler)
	throws ScraperException {
		
		final DataTable dataTable = handler.getDataTable();
        SimpleNodeIterator it = nodes.elements();
        final String tierColspanStr = String.valueOf(dataTable.tierColspan);
        
        LOG.finer("nodes has " + nodes.size() + " elements");
        
        while (it.hasMoreNodes()) {
        	if (handler.ignoreNewData()) {
        		LOG.finer("handler.ignoreNewData() was true, breaking out");
        		break;
        	}
        	
        	Node node = it.nextNode();
        	
        	if (!(node instanceof TableRow)) {
        		throw new ScraperException("Expecting nodes to contain TableRows, got a " + node.getClass().getName());
        	}
  	
        	TableRow tr = (TableRow) node;
        	
        	if (0 == tr.getChildCount()) {
        		LOG.finest("skipping row with no children");
        		continue;
        	}
        	
        	String cssClass = tr.getAttribute("class");
        	
        	if (dataTable.headerCssClass.equals(cssClass) ||
        		(tr.getChildCount() > 0 && tr.getChild(0) instanceof TableHeader)) {
        		LOG.finest("handling header row");
        		handler.handleHeaderRow(tr);
        		continue;
        	}
        	
        	TableColumn firstTd = (TableColumn) tr.getChild(0);
        	cssClass = firstTd.getAttribute("class");
        	
        	if (dataTable.tierCssClass.equals(cssClass) ||
        		(dataTable.tierChildNodeCount == tr.getChildCount() &&
        		 // check the colspan to try to make sure
        		 tierColspanStr.equals(
        			((TableColumn) tr.getChild(0)).getAttribute("colspan")))
        		) {
        		
        		// should be the tier row
        		String[][] tierData = DataTable.TIER_ROW_FORMAT.getData(tr);
        		String tierName =
        			null != tierData[0] && !tierData[0][0].isEmpty()
        			? tierData[0][0]
        			: "<BLANK>";
        		LOG.finest("handling tier row: " + tierName);
        		handler.handleTierRow(tierName);
        		continue;
        	}
        	
//        	LOG.finest("handler.getInvalidChildCountStrategy() == " + handler.getInvalidChildCountStrategy());
        	
        	if (dataTable.rowChildNodeCount != tr.getChildCount()) {
        		switch (handler.getInvalidChildCountStrategy()) {
        			case IGNORE:
        				LOG.finest("skipping invalid row");
        				LOG.finest("row: " + tr.toHtml());
        				continue;
        			case EXCEPTION:
        				ScraperException t = new ScraperException("invalid data row child node count, expecting " + dataTable.rowChildNodeCount + ", got " + tr.getChildCount());
        				LOG.finest("row: " + tr.toHtml());
        				LOG.throwing("TieredTabularDataExtractor", "extract", t);
        				throw t;
        			default:
        				LOG.finest("going to handle invalid row");
        				LOG.finest("row: " + tr.toHtml());
        		}
        	}
        	
        	LOG.finest("handling data row");
    		handler.handleDataRow(dataTable.rowFormat.getData(tr));
        }
	}
}

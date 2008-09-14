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

import org.htmlparser.Node;
import org.htmlparser.util.*;
import org.htmlparser.tags.*;

import jshm.exceptions.ScraperException;

public class TieredTabularDataExtractor {
	public static void extract(
		final NodeList nodes,
		final TieredTabularDataHandler handler)
	throws ScraperException {
		
		final DataTable dataTable = handler.getDataTable();
        SimpleNodeIterator it = nodes.elements();
        final String tierColspanStr = String.valueOf(dataTable.tierColspan);
        
        while (it.hasMoreNodes()) {
        	if (handler.ignoreNewData()) break;
        	
        	Node node = it.nextNode();
        	
        	if (!(node instanceof TableRow)) {
        		throw new ScraperException("Expecting nodes to contain TableRows, got a " + node.getClass().getName());
        	}
  	
        	TableRow tr = (TableRow) node;
        	String cssClass = tr.getAttribute("class");
        	
        	if (dataTable.headerCssClass.equals(cssClass) ||
        		(tr.getChildCount() > 0 && tr.getChild(0) instanceof TableHeader)) {
        		handler.handleHeaderRow(tr);
        		continue;
        	}
        	
        	if (dataTable.tierChildNodeCount == tr.getChildCount() &&
        		// check the colspan to try to make sure
        		tierColspanStr.equals(
        			((TableColumn) tr.getChild(0)).getAttribute("colspan"))
        		) {        		
        		
        		// should be the tier row
        		String[][] tierData = DataTable.TIER_ROW_FORMAT.getData(tr);
        		handler.handleTierRow(tierData[0][0]);
        		continue;
        	}
        	
        	if (dataTable.rowChildNodeCount != tr.getChildCount())
        		if (handler.ignoreInvalidRowChildCount())
        			continue;
        		else
        			throw new ScraperException("invalid data row child node count, expecting " + dataTable.rowChildNodeCount + ", got " + tr.getChildCount());
        	
    		handler.handleDataRow(dataTable.rowFormat.getData(tr));
        }
	}
}

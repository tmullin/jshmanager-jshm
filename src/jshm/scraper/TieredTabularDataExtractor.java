package jshm.scraper;

import org.htmlparser.Node;
import org.htmlparser.util.*;
import org.htmlparser.tags.TableRow;

import jshm.exceptions.ScraperException;
import jshm.sh.DataTable;

public class TieredTabularDataExtractor {
	public static void extract(
		final NodeList nodes,
		final TieredTabularDataHandler handler)
	throws ScraperException {
		
		final DataTable dataTable = handler.getDataTable();
        SimpleNodeIterator it = nodes.elements();
        
        while (it.hasMoreNodes()) {
        	if (handler.ignoreNewData()) break;
        	
        	Node node = it.nextNode();
        	
        	if (!(node instanceof TableRow)) {
        		throw new ScraperException("Expecting nodes to contain TableRows, got a " + node.getClass().getName());
        	}
  	
        	TableRow tr = (TableRow) node;
        	String cssClass = tr.getAttribute("class");
        	
        	if (dataTable.headerCssClass.equals(cssClass)) {
        		handler.handleHeaderRow(tr);
        		continue;
        	}
        	
        	if (dataTable.tierChildNodeCount == tr.getChildCount()) {
        		// should be the tier row
        		String[][] tierData = DataTable.TIER_ROW_FORMAT.getData(tr);
        		handler.handleTierRow(tierData[0][0]);
        		continue;
        	}
        	
        	if (dataTable.rowChildNodeCount != tr.getChildCount())
        		throw new ScraperException("Invalid data row child node count, expecting " + dataTable.rowChildNodeCount + ", got " + tr.getChildCount());
        	
    		handler.handleDataRow(dataTable.rowFormat.getData(tr));
        }
	}
}

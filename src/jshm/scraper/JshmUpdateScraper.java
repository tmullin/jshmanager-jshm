package jshm.scraper;

import java.util.ArrayList;
import java.util.List;

import jshm.exceptions.ScraperException;

import org.htmlparser.NodeFilter;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.HasParentFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.util.NodeList;

/**
 * This will get the list of current downloads from the Google Code
 * download page. 
 * @author Tim Mullin
 *
 */
public class JshmUpdateScraper {
	public static final String DOWNLOADS_URL =
		"http://code.google.com/p/jshmanager/downloads/list";
	public static final String FILE_DOWNLOAD_URL_PREFIX = 
		"http://jshmanager.googlecode.com/files";
	
	static final NodeFilter[] DOWNLOADS_TABLE_FILTERS;
	
	static {
		// <table cellspacing=0 cellpadding=2 border=0 class="results" id=resultstable width=100%>
		// <tbody>
		
        AndFilter tableParentPred = new AndFilter (new NodeFilter[] {
        	new TagNameFilter("TABLE"),
        	new HasAttributeFilter("id", "resultstable")//,
//        	new HasAttributeFilter("border", "0"),
//        	new HasAttributeFilter("cellspacing", "0"),
//        	new HasAttributeFilter("cellpadding", "2"),
//        	new HasAttributeFilter("class", "results"),
        });
		
//		AndFilter tbodyParentPred = new AndFilter(new NodeFilter[] {
//			new HasParentFilter(tableParentPred, false),
//			new TagNameFilter("TBODY")
//		});

        AndFilter mainFilter = new AndFilter (new NodeFilter[] {
        	new HasParentFilter(tableParentPred, true),
//    		new HasParentFilter(tbodyParentPred, false),
    		new TagNameFilter("TR")
        });
        
        DOWNLOADS_TABLE_FILTERS = new NodeFilter[] {
        	mainFilter
        };
	}
	
	static final DataTable DOWNLOADS_TABLE = new DataTable(-1, -1, 7,
		"-|link|-|-|-|-|-") {
		
		public NodeFilter[] getFilters() {
			return DOWNLOADS_TABLE_FILTERS;
		}
	};
	
	
	public static List<String> scrape() throws Exception {
		List<String> ret = new ArrayList<String>();
		
		DownloadsHandler h = new DownloadsHandler(ret);
		NodeList nodes = Scraper.scrape(DOWNLOADS_URL, h);
		TieredTabularDataExtractor.extract(nodes, h);
		
		return ret;
	}
	
	static class DownloadsHandler extends TieredTabularDataAdapter {
		List<String> ret;
		
		public DownloadsHandler(List<String> ret) {
			this.ret = ret;
		}
		
		public DataTable getDataTable() {
			return DOWNLOADS_TABLE;
		}
		
		public void handleDataRow(String[][] data) throws ScraperException {
			int i = data[1][0].lastIndexOf('/');
			
			// -4 because the file must end in a.ext
			if (i == FILE_DOWNLOAD_URL_PREFIX.length() &&
				i < data[1][0].length() - 5)
				ret.add(data[1][0].substring(i + 1));
		}
	}
}

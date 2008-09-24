package jshm.concepts;

import java.util.List;

import jshm.logging.Log;
import jshm.scraper.JshmUpdateScraper;

public class JshmUpdateScraperTest {
	public static void main(String[] args) throws Exception {
		Log.configTestLogging();
		List<String> ret = JshmUpdateScraper.scrape();
		
		for (String s : ret) System.out.println(s);
	}
}

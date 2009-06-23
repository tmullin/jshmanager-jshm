package jshm.concepts;

import java.util.Map;

import jshm.logging.Log;
import jshm.sh.scraper.wiki.Action;
import jshm.sh.scraper.wiki.ActionsScraper;

public class WikiActionsScraperTest {
	static final String url =
		"http://wiki.scorehero.com/Song_GH3_3sAnd7s/raw"
//		"http://wiki.scorehero.com/Song_GHWT_LivinOnAPrayer/raw"
//		"http://wiki.scorehero.com/User_DarylZero/raw"
//		"http://wiki.scorehero.com/Song_GH2_BeastAndTheHarlot/raw"
//		"http://wiki.scorehero.com/Song_GH3_CherubRock/raw" // no space after end quote
//		"http://wiki.scorehero.com/Song_GH2_LaidToRest/raw" // no end quote
	;
	
	public static void main(String[] args) throws Exception {
		Log.configTestLogging();
		
		Map<String, Action> actions = ActionsScraper.scrape(url);
		
		for (String key : actions.keySet()) {
			System.out.println(actions.get(key));
		}
	}
}

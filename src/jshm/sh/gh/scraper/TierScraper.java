package jshm.sh.gh.scraper;

import java.util.*;

import org.htmlparser.util.NodeList;

import jshm.Difficulty;
import jshm.exceptions.*;
import jshm.sh.*;
import jshm.sh.scraper.*;
import jshm.sh.gh.*;

public class TierScraper {
	public static List<String> scrape(GhGame game)
	throws ScraperException {
		final Difficulty difficulty = Difficulty.EXPERT;
		
        List<String> tiers = new ArrayList<String>();
        
		NodeList nodes = Scraper.scrape(
			URLs.gh.getSongStatsUrl(
				SongStat.TOTAL_NOTES,
				game,
				difficulty));
        
		TieredTabularDataExtractor.extract(
			nodes,
			new TierHandler(tiers)
		);
        
        return tiers;
	}
	
	private static class TierHandler extends TieredTabularDataAdapter {
		final List<String> tiers;
		
		public TierHandler(final List<String> tiers) {
			this.tiers = tiers;
		}
		
		public DataTable getDataTable() {
			return DataTable.GH_TOTAL_NOTES;
		}
		
		public void handleTierRow(String tierName) {
			tiers.add(tierName);
		}
	}
}

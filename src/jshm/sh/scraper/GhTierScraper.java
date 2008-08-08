package jshm.sh.scraper;

import java.util.*;

import org.htmlparser.util.NodeList;

import jshm.Difficulty;
import jshm.exceptions.*;
import jshm.gh.GhGame;
import jshm.scraper.TieredTabularDataAdapter;
import jshm.scraper.TieredTabularDataExtractor;
import jshm.sh.*;

public class GhTierScraper {
	public static List<String> scrape(GhGame game)
	throws ScraperException {
		final Difficulty difficulty = Difficulty.EXPERT;
		
        List<String> tiers = new ArrayList<String>();
        
		NodeList nodes = GhScraper.scrape(
			URLs.gh.getSongStatsUrl(
				GhSongStat.TOTAL_NOTES,
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

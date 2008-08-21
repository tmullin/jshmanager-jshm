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
			URLs.gh.getTopScoresUrl(
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
			return DataTable.GH_TOP_SCORES;
		}
		
		public void handleTierRow(String tierName) {
			tiers.add(tierName);
		}
	}
}

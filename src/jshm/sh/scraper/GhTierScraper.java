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
import org.htmlparser.util.ParserException;

import jshm.Difficulty;
import jshm.Instrument.Group;
import jshm.exceptions.*;
import jshm.gh.GhGame;
import jshm.scraper.DataTable;
import jshm.scraper.TieredTabularDataAdapter;
import jshm.scraper.TieredTabularDataExtractor;
import jshm.sh.*;
import jshm.wt.WtGame;

public class GhTierScraper {
	static final Difficulty difficulty = Difficulty.EXPERT;
	
	public static List<String> scrape(GhGame game)
	throws ScraperException, ParserException {
		return scrape(
			URLs.gh.getTopScoresUrl(game, difficulty),
			GhDataTable.TOP_SCORES);
	}
	
	public static List<String> scrape(WtGame game, Group group)
	throws ScraperException, ParserException {
		return scrape(
			URLs.wt.getTopScoresUrl(game, group, difficulty),
			WtDataTable.TOP_SCORES);
	}
	
	private static List<String> scrape(String url, DataTable dataTable) throws ParserException, ScraperException {		
        List<String> tiers = new ArrayList<String>();
        
		NodeList nodes = GhScraper.scrape(url);
//        System.out.println(url);
//        System.out.println(nodes);
		TieredTabularDataExtractor.extract(
			nodes,
			new TierHandler(tiers, dataTable)
		);
        
        return tiers;
	}
	
	private static class TierHandler extends TieredTabularDataAdapter {
		final List<String> tiers;
		final DataTable dataTable;
		
		public TierHandler(final List<String> tiers, DataTable dataTable) {
			this.tiers = tiers;
			this.dataTable = dataTable;
//			this.invalidChildCountStrategy = TieredTabularDataExtractor.InvalidChildCountStrategy.HANDLE;
		}
		
		public DataTable getDataTable() {
			return dataTable;
		}
		
		public void handleTierRow(String tierName) {
			tiers.add(tierName);
		}
	}
}

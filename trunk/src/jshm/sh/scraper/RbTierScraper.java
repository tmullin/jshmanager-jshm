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

import jshm.*;
import jshm.exceptions.*;
import jshm.rb.RbGame;
import jshm.scraper.*;
import jshm.sh.*;

public class RbTierScraper {
	public static List<String> scrape(RbGame game, Instrument.Group group)
	throws ScraperException, ParserException {
		final Difficulty difficulty = Difficulty.EXPERT;
		
        List<String> tiers = new ArrayList<String>();
        TierHandler handler = new TierHandler(tiers);
        String url = URLs.rb.getTopScoresUrl(game, group, difficulty);
//        System.out.println("url = " + url);
		NodeList nodes = Scraper.scrape(url, handler.getDataTable());
//        System.out.println("nodecount = " + nodes.size());
		TieredTabularDataExtractor.extract(
			nodes, handler
		);
        
        return tiers;
	}
	
	private static class TierHandler extends TieredTabularDataAdapter {
		final List<String> tiers;
		
		public TierHandler(final List<String> tiers) {
			this.tiers = tiers;
		}
		
		public DataTable getDataTable() {
			return RbDataTable.TOP_SCORES;
		}
		
		public void handleTierRow(String tierName) {
			tiers.add(tierName);
		}
	} 
}

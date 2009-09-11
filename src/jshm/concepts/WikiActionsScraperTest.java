/*
 * -----LICENSE START-----
 * JSHManager - A Java-based tool for managing one's ScoreHero account.
 * Copyright (C) 2008, 2009 Tim Mullin
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
package jshm.concepts;

import java.util.List;
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
		
		Map<String, List<Action>> actions = ActionsScraper.scrape(url);
		
		for (String key : actions.keySet()) {
			System.out.println(actions.get(key).get(0));
		}
	}
}

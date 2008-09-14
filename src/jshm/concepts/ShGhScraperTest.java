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
package jshm.concepts;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import jshm.*;
import jshm.gh.*;
import jshm.gui.LoginDialog;
import jshm.rb.*;
import jshm.sh.scraper.*;

@SuppressWarnings("unused")
public class ShGhScraperTest {
//	static final GhGame game 			= GhGame.GH3_XBOX360;
	static final RbGame game 			= RbGame.RB1_XBOX360;
	static final Difficulty difficulty 	= Difficulty.EXPERT;
	
	public static void main(String[] args) throws Exception {
		jshm.logging.Log.configTestLogging();
		jshm.util.TestTimer.start(true);
		
		doSongs();
//		doScores();
		
		jshm.util.TestTimer.stop(true);
	}
	
//	static void doScores() throws Exception {
//		LoginDialog.showDialog();
//		
//		List<GhScore> scores = 
//			GhScoreScraper.scrapeAll(
//				game, difficulty);
//
//		for (GhScore s : scores)
//			System.out.println(s);
//	}
	
	static void doSongs() throws Exception {
//		List<RbSong> songs = 
//			RbSongScraper.scrape(game);
//	
//		for (Song s : songs) {
//			System.out.println(s);
//		}
		
		List<SongOrder> orders = 
			RbSongScraper.scrapeOrders(game);
		
		for (SongOrder o : orders) {
			if (o.getTier() == 10) continue;
			System.out.println(o);
		}
	}
}

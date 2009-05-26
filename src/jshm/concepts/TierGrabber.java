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
import jshm.rb.RbGame;
import jshm.rb.RbGameTitle;
import jshm.sh.scraper.*;

/**
 * This is an internal program to provide an easily
 * copy and paste-able list for the GameTitle enum.
 * @author Tim Mullin
 *
 */
public class TierGrabber {
	public static void main(String[] args) throws Exception {
		Logger.getLogger("").setLevel(Level.ALL);
		
		List<Game> games = Game.getByTitle(RbGameTitle.RB2); // .getBySeries(GameSeries.ROCKBAND);
		
		jshm.util.TestTimer.start();
		
		for (Game game : games) {
			List<String> tiers = RbTierScraper.scrape((RbGame) game, Instrument.Group.GUITAR);
			
			System.out.print(game + " = \"");
			for (int i = 0; i < tiers.size(); i++) {
				if (i > 0) System.out.print('|');
				System.out.print(tiers.get(i));
			}
			System.out.println("\",");
		}
		
		jshm.util.TestTimer.stop();
	}
}

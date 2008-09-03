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
package jshm.sh.links;

import java.util.List;

import jshm.Difficulty;
import jshm.Game;
import jshm.GameSeries;
import jshm.GameTitle;
import jshm.gh.GhGameTitle;
import jshm.sh.URLs;

class GhGamesTemplate extends Link {	
	GhGamesTemplate(final String name, final String urlFmt) {
		super(name);
		
		for (GameTitle t : GameTitle.getTitlesBySeries(GameSeries.GUITAR_HERO)) {
			GhGameTitle tt = (GhGameTitle) t;
			
			Link ttlLink = new Link(t.title);
			ttlLink.icon = t.getIcon();
			
			List<Game> games = Game.getByTitle(t);
			
			for (Game g : games) {
				Link gameLink = null;
				
				if (games.size() > 1) {
					gameLink = new Link(g.platform.toString());
					gameLink.icon = g.platform.getIcon();
				} else {
					gameLink = ttlLink;
				}
				
				for (Difficulty d : Difficulty.values()) {
					Link diffLink = new Link(
						d.toString(),
						String.format(URLs.BASE + "/" + urlFmt + "game=%s&diff=%s", g.scoreHeroId, d.scoreHeroId),
						d.getIcon());
					gameLink.add(diffLink);
				}
				
				if (gameLink != ttlLink) {
					ttlLink.add(gameLink);
				}
			}
			
			// create "All" item
			if (games.size() > 1) {
				Link allLink = new Link("All");
				
				for (Difficulty d : Difficulty.values()) {
					Link diffLink = new Link(
						d.toString(),
						String.format(URLs.BASE + "/" + urlFmt + "group=%s&game=0&diff=%s", tt.scoreHeroGroupId, d.scoreHeroId),
						d.getIcon());
					allLink.add(diffLink);
				}
				
				ttlLink.add(allLink);
			}
			
			add(ttlLink);
		}
	}
}

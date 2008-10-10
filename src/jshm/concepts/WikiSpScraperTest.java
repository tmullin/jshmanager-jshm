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

import java.util.List;

import jshm.*;
import jshm.Instrument.Group;
import jshm.gh.GhSong;
import jshm.sh.links.Link;
import jshm.sh.scraper.WikiSpScraper;

public class WikiSpScraperTest {
	public static void main(String[] args) throws Exception {
		Song song = GhSong.getByScoreHeroId(96); // Ace Of Spades
		List<Link> links = WikiSpScraper.scrape(song, Group.GUITAR, Difficulty.EXPERT);
		
		for (Link l : links)
			System.out.print(l);
	}
}

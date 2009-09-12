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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jshm.Difficulty;
import jshm.Instrument;
import jshm.SongOrder;
import jshm.exceptions.ScraperException;
import jshm.rb.RbGame;
import jshm.rb.RbSong;
import jshm.scraper.DataTable;
import jshm.scraper.Scraper;
import jshm.scraper.TieredTabularDataAdapter;
import jshm.scraper.TieredTabularDataExtractor;
import jshm.scraper.TieredTabularDataExtractor.InvalidChildCountStrategy;
import jshm.sh.URLs;

import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.netbeans.spi.wizard.ResultProgressHandle;

public class RbSongScraper {
	static {
		Formats.init();
	}
	
	public static List<String> lastScrapedTiers = null;
	
	public static List<RbSong> scrape(
		final RbGame game) 
	throws ScraperException, ParserException {
		
		List<RbSong> songs = new ArrayList<RbSong>();
		lastScrapedTiers = new ArrayList<String>();
		TieredTabularDataAdapter handler = new SongHandler(game, songs, lastScrapedTiers);
		
		NodeList nodes = Scraper.scrape(
			URLs.rb.getTopScoresUrl(
				game,
				Instrument.Group.GUITAR,
				Difficulty.EXPERT),
			handler);
		
		TieredTabularDataExtractor.extract(nodes, handler);
		
		return songs;
	}

	public static List<SongOrder> scrapeOrders(final RbGame game) 
	throws ScraperException, ParserException {
		return scrapeOrders(null, game, null);
	}
	
	public static List<SongOrder> scrapeOrders(
		ResultProgressHandle progress, final RbGame game)
	throws ParserException, ScraperException {
		return scrapeOrders(progress, game, null);
	}
	
	/**
	 * Scrape the song orders without needing to use the database by
	 * providing a list of the songs.
	 * @param game
	 * @param songMap
	 * @return
	 * @throws ParserException
	 * @throws ScraperException
	 */
	public static List<SongOrder> scrapeOrders(
		final RbGame game, final Map<Integer, RbSong> songMap)
	throws ParserException, ScraperException {
		return scrapeOrders(null, game, songMap);
	}
	
	public static List<SongOrder> scrapeOrders(
		final ResultProgressHandle progress, final RbGame game, final Map<Integer, RbSong> songMap) 
	throws ScraperException, ParserException {
		
		List<SongOrder> orders = new ArrayList<SongOrder>();
		
		List<Instrument.Group> groups = Instrument.Group.getBySize(1);
		groups.add(Instrument.Group.GUITAR_BASS);
		
		for (Instrument.Group g : groups) {
			if (null != progress)
				progress.setBusy("Downloading list for " + game + " " + g);
			
//			List<SongOrder> curOrders = new ArrayList<SongOrder>();
			TieredTabularDataAdapter handler =
				new SongOrderHandler(game, g, orders, songMap);
		
			NodeList nodes = Scraper.scrape(
				URLs.rb.getTopScoresUrl(
					game,
					g,
					Difficulty.EXPERT),
				handler);
		
			TieredTabularDataExtractor.extract(nodes, handler);

//			orders.addAll(curOrders);
		}
		
		return orders;
	}
	
	private static class SongHandler extends TieredTabularDataAdapter {
		final RbGame game;
		final List<RbSong> songs;
		final List<String> tiers;
		
		public SongHandler(final RbGame game, final List<RbSong> songs, final List<String> tiers) {
			this.invalidChildCountStrategy = InvalidChildCountStrategy.HANDLE;
			this.game = game;
			this.songs = songs;
			this.tiers = tiers;
		}
		
		@Override
		public DataTable getDataTable() {
			return RbDataTable.TOP_SCORES;
		}
		
		@Override
		public void handleTierRow(String tierName) throws ScraperException {
			if (null != tiers)
				tiers.add(tierName);
			//System.out.println("new tier: " + tierName);
		}
		
		@Override
		public void handleDataRow(String[][] data) throws ScraperException {
			RbSong curSong = new RbSong();
			curSong.setGameTitle(game.title);
			curSong.addPlatform(game.platform);
			
			try {
    			curSong.setScoreHeroId(Integer.parseInt(data[1][1]));
    		} catch (NumberFormatException e) {
    			throw new ScraperException("Error parsing song id", e);
    		}
    		
    		curSong.setTitle(data[1][0]);
    		
    		songs.add(curSong);
		}
	}
	
	private static class SongOrderHandler extends TieredTabularDataAdapter {
		final RbGame game;
		final Instrument.Group group;
		final List<SongOrder> orders;
		final Map<Integer, RbSong> songMap;
		int curTierLevel = 0, curOrder = 0;
		
		public SongOrderHandler(RbGame game, Instrument.Group group, List<SongOrder> orders, Map<Integer, RbSong> songMap) {
			this.invalidChildCountStrategy = InvalidChildCountStrategy.HANDLE;
			this.game = game;
			this.group = group;
			this.orders = orders;
			this.songMap = songMap;
		}

		@Override
		public DataTable getDataTable() {
			return RbDataTable.TOP_SCORES;
		}
		
		@Override
		public void handleTierRow(String tierName) throws ScraperException {
			// can't ignore since we update tiers dynamically now
//			if (curTierLevel >= game.getTierCount()) {
//				ignoreNewData = true;
//				return;
//			}
			
			curTierLevel++;
			curOrder = 0;
			
//			System.out.println("new tier " + tierName + " = " + curTierLevel);
		}
		
		@Override
		public void handleDataRow(String[][] data) throws ScraperException {
			SongOrder order = new SongOrder();
			order.setGroup(group);
			order.setPlatform(game.platform);
			order.setOrder(curOrder);
			order.setTier(curTierLevel);
			
			try {
				// for testing
//				RbSong s = new RbSong();
//				s.setTitle(data[1][0]);
//				s.setScoreHeroId(Integer.parseInt(data[1][1]));
//				s.addPlatform(game.platform);
//				order.setSong(s);
				RbSong s = songMap != null
					? songMap.get(Integer.parseInt(data[1][1]))
					: RbSong.getByScoreHeroId(Integer.parseInt(data[1][1]));
				if (null == s)
					throw new ScraperException("Song not found with scoreHeroId=" + data[1][1]);
    			order.setSong(s);
    		} catch (NumberFormatException e) {
    			throw new ScraperException("Error parsing song id", e);
    		}
			
    		orders.add(order);
			curOrder++;
			
//			System.out.println(orders.size() + ": " + order);
		}
	}
}

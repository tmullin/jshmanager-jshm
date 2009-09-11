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
package jshm.sh.scraper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.netbeans.spi.wizard.ResultProgressHandle;

import jshm.Difficulty;
import jshm.Instrument;
import jshm.SongOrder;
import jshm.Instrument.Group;
import jshm.exceptions.ScraperException;
import jshm.scraper.DataTable;
import jshm.scraper.Scraper;
import jshm.scraper.TieredTabularDataAdapter;
import jshm.scraper.TieredTabularDataExtractor;
import jshm.sh.URLs;
import jshm.wt.*;

public class WtSongScraper {
	static {
		Formats.init();
	}
	
	public static List<WtSong> scrape(final WtGame game)
	throws ScraperException, ParserException {
		return scrape(game, Instrument.Group.GUITAR, Difficulty.EXPERT);
	}
	
	public static List<WtSong> scrape(final WtGame game, Instrument.Group group, Difficulty diff)
	throws ScraperException, ParserException {
		
		List<WtSong> songs = new ArrayList<WtSong>();
		SongHandler handler = new SongHandler(game, songs);
		NodeList nodes = Scraper.scrape(
			URLs.wt.getTopScoresUrl(
				game, group, diff),
			handler);
		
		TieredTabularDataExtractor.extract(nodes, handler);
		
		return songs;
	}
	
	public static List<SongOrder> scrapeOrders(final WtGame game) 
	throws ScraperException, ParserException {
		return scrapeOrders(null, game, null);
	}
	
	public static List<SongOrder> scrapeOrders(
		ResultProgressHandle progress, final WtGame game)
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
		final WtGame game, final Map<Integer, WtSong> songMap)
	throws ParserException, ScraperException {
		return scrapeOrders(null, game, songMap);
	}
	
	public static List<SongOrder> scrapeOrders(
		final ResultProgressHandle progress, final WtGame game, final Map<Integer, WtSong> songMap)
	throws ScraperException, ParserException {
		
		List<SongOrder> orders = new ArrayList<SongOrder>();

		// WT drum order is same as RB so we're not going to get them both
		List<Group> groups = Instrument.Group.getBySize(1, false);
		groups.add(Group.GUITAR_BASS);
		
		for (Group g : groups) {
			if (null != progress)
				progress.setBusy("Downloading list for " + game + " " + g);
			
			TieredTabularDataAdapter handler =
				new SongOrderHandler(game, g, orders, songMap);
		
			NodeList nodes = Scraper.scrape(
				URLs.wt.getTopScoresUrl(
					game,
					g,
					Difficulty.EXPERT),
				handler);
		
			TieredTabularDataExtractor.extract(nodes, handler);
		}
		
		return orders;
	}
	
	private static class SongHandler extends TieredTabularDataAdapter {
		final WtGame game;
		final List<WtSong> songs;
		int curTierLevel = 0;
		
		public SongHandler(WtGame game, List<WtSong> songs) {
			this.game = game;
			this.songs = songs;
			this.invalidChildCountStrategy = TieredTabularDataExtractor.InvalidChildCountStrategy.HANDLE;
		}
		
		@Override
		public DataTable getDataTable() {
			return WtDataTable.TOP_SCORES;
		}
		
		@Override
		public void handleTierRow(String tierName) throws ScraperException {
			// this will prevent retrieval of the ghsh demo tier
			// on the ghwt page, for example
			if (curTierLevel >= game.getTierCount()) {
				ignoreNewData = true;
				return;
			}
			
			curTierLevel++;
		}
		
		@Override
		public void handleDataRow(String[][] data) throws ScraperException {
			WtSong curSong = new WtSong();
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
		final WtGame game;
		final Group group;
		final List<SongOrder> orders;
		final Map<Integer, WtSong> songMap;
		int curTierLevel = 0, curOrder = 0;
		
		public SongOrderHandler(WtGame game, Group group, List<SongOrder> orders, Map<Integer, WtSong> songMap) {
			this.game = game;
			this.group = group;
			this.orders = orders;
			this.songMap = songMap;
			this.invalidChildCountStrategy = TieredTabularDataExtractor.InvalidChildCountStrategy.HANDLE;
		}

		@Override
		public DataTable getDataTable() {
			return WtDataTable.TOP_SCORES;
		}
		
		@Override
		public void handleTierRow(String tierName) throws ScraperException {
			if (curTierLevel >= game.getTierCount()) {
				ignoreNewData = true;
				return;
			}
			
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
				WtSong s = songMap != null
					? songMap.get(Integer.parseInt(data[1][1]))
					: WtSong.getByScoreHeroId((WtGameTitle) game.title, Integer.parseInt(data[1][1]));
				
				if (null == s)
					throw new ScraperException("Song not found with scoreHeroId=" + data[1][1]);
				order.setSong(s);
			} catch (NumberFormatException e) {
				throw new ScraperException("Error parsing song id", e);
			}
			
//			System.out.println("adding order: " + order);
			orders.add(order);
			curOrder++;
		}
	}
}

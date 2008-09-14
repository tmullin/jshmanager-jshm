package jshm.sh.scraper;

import java.util.ArrayList;
import java.util.List;

import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import jshm.Difficulty;
import jshm.Instrument;
import jshm.SongOrder;
import jshm.exceptions.ScraperException;
import jshm.rb.*;
import jshm.scraper.DataTable;
import jshm.scraper.Scraper;
import jshm.scraper.TieredTabularDataAdapter;
import jshm.scraper.TieredTabularDataExtractor;
import jshm.sh.URLs;

public class RbSongScraper {
	static {
		Formats.init();
	}
	
	public static List<RbSong> scrape(
		final RbGame game) 
	throws ScraperException, ParserException {
		
		List<RbSong> songs = new ArrayList<RbSong>();
		TieredTabularDataAdapter handler = new SongHandler(game, songs);
		
		NodeList nodes = Scraper.scrape(
			URLs.rb.getTopScoresUrl(
				game.platform,
				Instrument.Group.GUITAR,
				Difficulty.EXPERT),
			handler);
		
		TieredTabularDataExtractor.extract(nodes, handler);
		
		return songs;
	}
	
	public static List<SongOrder> scrapeOrders(
			final RbGame game) 
		throws ScraperException, ParserException {
		
		List<SongOrder> orders = new ArrayList<SongOrder>();
		
		List<Instrument.Group> groups = Instrument.Group.getBySize(1);
		groups.add(Instrument.Group.GUITAR_BASS);
		
		for (Instrument.Group g : groups) {
//			List<SongOrder> curOrders = new ArrayList<SongOrder>();
			TieredTabularDataAdapter handler = new SongOrderHandler(game, g, orders);
		
			NodeList nodes = Scraper.scrape(
				URLs.rb.getTopScoresUrl(
					game.platform,
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
		
		public SongHandler(final RbGame game, final List<RbSong> songs) {
			this.game = game;
			this.songs = songs;
		}
		
		@Override
		public DataTable getDataTable() {
			return RbDataTable.TOP_SCORES;
		}
		
		public void handleDataRow(String[][] data) throws ScraperException {
			RbSong curSong = new RbSong();
			curSong.getGames().add(game);
			
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
		int curTierLevel = 0, curOrder = 0;
		
		public SongOrderHandler(RbGame game, Instrument.Group group, List<SongOrder> orders) {
			this.game = game;
			this.group = group;
			this.orders = orders;
		}

		@Override
		public DataTable getDataTable() {
			return RbDataTable.TOP_SCORES;
		}
		
		@Override
		public void handleTierRow(String tierName) throws ScraperException {
			if (curTierLevel >= game.getTierCount()) {
				ignoreNewData = true;
				return;
			}
			
			curTierLevel++;
			curOrder = 0;
		}
		
		@Override
		public void handleDataRow(String[][] data) throws ScraperException {
			SongOrder order = new SongOrder();
			order.setGroup(group);
			order.setOrder(curOrder);
			order.setTier(curTierLevel);
			
			try {
				// for testing
				RbSong s = new RbSong();
				s.setTitle(data[1][0]);
				s.setScoreHeroId(Integer.parseInt(data[1][1]));
				s.getGames().add(game);
				order.setSong(s);
//    			order.setSong(
//    				RbSong.getByScoreHeroId(Integer.parseInt(data[1][1])));
    		} catch (NumberFormatException e) {
    			throw new ScraperException("Error parsing song id", e);
    		}
			
    		orders.add(order);
			curOrder++;
		}
	}
}

package jshm.sh.scraper;

import java.util.ArrayList;
import java.util.List;

import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.netbeans.spi.wizard.ResultProgressHandle;

import jshm.Difficulty;
import jshm.Instrument;
import jshm.SongOrder;
import jshm.exceptions.ScraperException;
import jshm.rb.*;
import jshm.scraper.DataTable;
import jshm.scraper.Scraper;
import jshm.scraper.TieredTabularDataAdapter;
import jshm.scraper.TieredTabularDataExtractor;
import jshm.scraper.TieredTabularDataExtractor.InvalidChildCountStrategy;
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
				game,
				Instrument.Group.GUITAR,
				Difficulty.EXPERT),
			handler);
		
		TieredTabularDataExtractor.extract(nodes, handler);
		
		return songs;
	}

	public static List<SongOrder> scrapeOrders(final RbGame game) 
		throws ScraperException, ParserException {
		return scrapeOrders(null, game);
	}
	
	public static List<SongOrder> scrapeOrders(
			ResultProgressHandle progress, final RbGame game) 
		throws ScraperException, ParserException {
		
		List<SongOrder> orders = new ArrayList<SongOrder>();
		
		List<Instrument.Group> groups = Instrument.Group.getBySize(1);
		groups.add(Instrument.Group.GUITAR_BASS);
		
		for (Instrument.Group g : groups) {
			if (null != progress)
				progress.setBusy("Downloading list for " + game + " " + g);
			
//			List<SongOrder> curOrders = new ArrayList<SongOrder>();
			TieredTabularDataAdapter handler = new SongOrderHandler(game, g, orders);
		
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
		
		public SongHandler(final RbGame game, final List<RbSong> songs) {
			this.invalidChildCountStrategy = InvalidChildCountStrategy.HANDLE;
			this.game = game;
			this.songs = songs;
		}
		
		@Override
		public DataTable getDataTable() {
			return RbDataTable.TOP_SCORES;
		}
		
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
		int curTierLevel = 0, curOrder = 0;
		
		public SongOrderHandler(RbGame game, Instrument.Group group, List<SongOrder> orders) {
			this.invalidChildCountStrategy = InvalidChildCountStrategy.HANDLE;
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
				RbSong s = RbSong.getByScoreHeroId(Integer.parseInt(data[1][1]));
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

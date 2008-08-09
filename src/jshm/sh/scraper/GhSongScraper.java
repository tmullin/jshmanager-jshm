package jshm.sh.scraper;

import java.util.*;

import org.htmlparser.util.*;

import jshm.Difficulty;
import jshm.exceptions.*;
import jshm.gh.GhGame;
import jshm.gh.GhGameTitle;
import jshm.gh.GhSong;
import jshm.scraper.TieredTabularDataAdapter;
import jshm.scraper.TieredTabularDataExtractor;
import jshm.scraper.TieredTabularDataHandler;
import jshm.sh.*;

/**
 * This class serves to scrape all necessary info from
 * ScoreHero to build complete Song objects.
 * @author Tim Mullin
 *
 */
public class GhSongScraper {
	static {
		Formats.init();
	}
	
	public static List<GhSong> scrape(
		final GhGame game, final Difficulty difficulty)
	throws ScraperException {
		
		List<GhSong> songs = new ArrayList<GhSong>();
		
		NodeList nodes = GhScraper.scrape(
			URLs.gh.getSongStatsUrl(
				GhSongStat.TOTAL_NOTES,
				game,
				difficulty));
		
		TieredTabularDataHandler handler =
			new IdAndNoteHandler(game, difficulty, songs);
		
		TieredTabularDataExtractor.extract(nodes, handler);
		
		nodes = GhScraper.scrape(
			URLs.gh.getSongStatsUrl(
				GhSongStat.ALL_CUTOFFS,
				game,
				difficulty));
		
		handler = new CutoffHandler(songs);
		TieredTabularDataExtractor.extract(nodes, handler);
		
		return songs;
	}
	
	private static class IdAndNoteHandler extends TieredTabularDataAdapter {
		final GhGame game;
		final GhGameTitle title;
		final Difficulty difficulty;
		final List<GhSong> songs;
		
		String curTierName = "UNKNOWN";
		int curTierLevel = 0;
		
		public IdAndNoteHandler(
			final GhGame game,
			final Difficulty difficulty,
			final List<GhSong> songs) {
			this.game = game;
			this.title = (GhGameTitle) game.title;
			this.difficulty = difficulty;
			this.songs = songs;
		}
		
		public DataTable getDataTable() {
			return DataTable.GH_TOTAL_NOTES;
		}
		
		public void handleTierRow(String tierName) throws ScraperException {
			// this will prevent retrieval of the gh3 demo tier
			// on the gh2 page, for example
			if (curTierLevel >= title.getTierCount()) {
				ignoreNewData = true;
				return;
			}
			
			curTierName = tierName;
			curTierLevel++;
		}
		
		public void handleDataRow(String[][] data) throws ScraperException {			
        	GhSong curSong = new GhSong();
        	curSong.setGame(game);
        	curSong.setDifficulty(difficulty);
        	curSong.setTierLevel(curTierLevel);
        	
    		try {
    			curSong.setScoreHeroId(Integer.parseInt(data[0][0]));
    		} catch (NumberFormatException e) {}
    		
    		curSong.setTitle(data[1][0]);
    		
    		try {
    			curSong.setNoteCount(Integer.parseInt(data[2][0]));
    		} catch (NumberFormatException e) {}
    		
        	songs.add(curSong);
		}
	}
	
	private static class CutoffHandler extends TieredTabularDataAdapter {
		final List<GhSong> songs;
		int totalSongs = 0;
		
		public CutoffHandler(
			final List<GhSong> songs) {
			this.songs = songs;
		}
		
		public DataTable getDataTable() {
			return DataTable.GH_ALL_CUTOFFS;
		}
		
		public void handleDataRow(String[][] data) throws ScraperException {
        	GhSong curSong = new GhSong();
        	
    		for (int i = 0; i < data.length; i++) {
    			String s = data[i][0];
    			int k = 0;
    			
    			try {
    				k = Integer.parseInt(s);
    			} catch (NumberFormatException e) {}
    			
    			switch (i) {
    				case 0: curSong.setTitle(s); break;
    				case 1: curSong.setBaseScore(k); break;
    				case 2: curSong.setFourStarCutoff(k); break;
    				case 3: curSong.setFiveStarCutoff(k); break;
    				case 4: curSong.setSixStarCutoff(k); break;
    				case 5: curSong.setSevenStarCutoff(k); break;
    				case 6: curSong.setEightStarCutoff(k); break;
    				case 7: curSong.setNineStarCutoff(k); break;
    			}
    		}
        	
        	try {
        		songs.get(totalSongs++).setScoreAndCutoffs(curSong);
        	} catch (IndexOutOfBoundsException e) {
        		ignoreNewData = true;
//        		throw new ScraperException("Song list is out of sync", e);
        	}
		}
	}
}

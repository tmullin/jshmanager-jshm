package jshm.sh.gh.scraper;

import java.util.*;

import org.htmlparser.util.*;

import jshm.exceptions.*;
import jshm.sh.*;
import jshm.sh.scraper.*;
import jshm.sh.gh.*;
import jshm.sh.gh.scraper.Scraper;

/**
 * This class serves to scrape all necessary info from
 * ScoreHero to build complete Song objects.
 * @author Tim Mullin
 *
 */
public class SongScraper {
	public static List<Song> scrape(
		final Game game, final Difficulty difficulty)
	throws ScraperException {
		
		List<Song> songs = new ArrayList<Song>();
		
		NodeList nodes = Scraper.scrape(
			URLs.gh.getSongStatsUrl(
				SongStat.TOTAL_NOTES,
				game,
				difficulty));
		
		TieredTabularDataHandler handler =
			new IdAndNoteHandler(game, difficulty, songs);
		
		TieredTabularDataExtractor.extract(nodes, handler);
		
		nodes = Scraper.scrape(
			URLs.gh.getSongStatsUrl(
				SongStat.ALL_CUTOFFS,
				game,
				difficulty));
		
		handler = new CutoffHandler(songs);
		TieredTabularDataExtractor.extract(nodes, handler);
		
		return songs;
	}
	
	private static class IdAndNoteHandler extends TieredTabularDataAdapter {
		final Game game;
		final Difficulty difficulty;
		final List<Song> songs;
		
		String curTierName = "UNKNOWN";
		int curTierLevel = 0;
		
		public IdAndNoteHandler(
			final Game game,
			final Difficulty difficulty,
			final List<Song> songs) {
			this.game = game;
			this.difficulty = difficulty;
			this.songs = songs;
		}
		
		public DataTable getDataTable() {
			return DataTable.GH_TOTAL_NOTES;
		}
		
		public void handleTierRow(String tierName) throws ScraperException {
			curTierName = tierName;
			curTierLevel++;
		}
		
		public void handleDataRow(String[][] data) throws ScraperException {
        	Song curSong = new Song();
        	curSong.game = game;
        	curSong.difficulty = difficulty;
        	curSong.tierName = curTierName;
        	curSong.tierLevel = curTierLevel;
        	
    		try {
    			curSong.id = Integer.parseInt(data[0][0]);
    		} catch (NumberFormatException e) {}
    		
    		curSong.title = data[1][0];
    		
    		try {
    			curSong.noteCount = Integer.parseInt(data[2][0]);
    		} catch (NumberFormatException e) {}
    		
        	songs.add(curSong);
        	curSong.order = songs.size();
		}
	}
	
	private static class CutoffHandler extends TieredTabularDataAdapter {
		final List<Song> songs;
		int totalSongs = 0;
		
		public CutoffHandler(
			final List<Song> songs) {
			this.songs = songs;
		}
		
		public DataTable getDataTable() {
			return DataTable.GH_ALL_CUTOFFS;
		}
		
		public void handleDataRow(String[][] data) throws ScraperException {
        	Song curSong = new Song();
        	
    		for (int i = 0; i < data.length; i++) {
    			String s = data[i][0];
    			int k = 0;
    			
    			try {
    				k = Integer.parseInt(s);
    			} catch (NumberFormatException e) {}
    			
    			switch (i) {
    				case 0: curSong.title = s; break;
    				case 1: curSong.baseScore = k; break;
    				case 2: curSong.fourStarCutoff = k; break;
    				case 3: curSong.fiveStarCutoff = k; break;
    				case 4: curSong.sixStarCutoff = k; break;
    				case 5: curSong.sevenStarCutoff = k; break;
    				case 6: curSong.eightStarCutoff = k; break;
    				case 7: curSong.nineStarCutoff = k; break;
    			}
    		}
        	
        	try {
        		songs.get(totalSongs++).setScoreAndCutoffs(curSong);
        	} catch (IndexOutOfBoundsException e) {
        		throw new ScraperException("Song list is out of sync", e);
        	}
		}
	}
}

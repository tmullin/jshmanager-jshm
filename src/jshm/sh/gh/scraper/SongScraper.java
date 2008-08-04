package jshm.sh.gh.scraper;

import java.util.*;

import org.htmlparser.*;
import org.htmlparser.tags.*;

import org.htmlparser.util.*;

import jshm.exceptions.*;
import jshm.sh.*;
import jshm.sh.gh.*;
import jshm.sh.gh.scraper.Scraper;

/**
 * This class serves to scrape all necessary info from
 * ScoreHero to build complete GhSong objects.
 * @author Tim Mullin
 *
 */
public class SongScraper {
	public static List<Song> scrape(
		final Game game, final Difficulty difficulty)
	throws ScraperException {
		List<Song> songs = new ArrayList<Song>();
		getIdsAndNotes(songs, game, difficulty);
		getCutoffs(songs, game, difficulty);
		return songs;
	}
	
	private static void getIdsAndNotes(
		final List<Song> songs, final Game game, final Difficulty difficulty)
	throws ScraperException {
		NodeList nodes = Scraper.scrape(
			URLs.gh.getSongStatsUrl(
				SongStat.TOTAL_NOTES,
				game,
				difficulty));
		
		final DataTable dataTable = DataTable.TOTAL_NOTES;
        SimpleNodeIterator it = nodes.elements();
        
        // have to track the tier while we traverse the rows
        String curTierName = "";
    	int curTierLevel = 0;
    	int totalSongs = 0;

        while (it.hasMoreNodes()) {
        	Node node = it.nextNode();
        	
        	if (!(node instanceof TableRow)) {
        		throw new ScraperException("Expecting nodes to contain TableRows, got a " + node.getClass().getName());
        	}
  	
        	TableRow tr = (TableRow) node;
        	String cssClass = tr.getAttribute("class");
        	
        	if (DataTable.HEADER_ROW_CSS_CLASS.equals(cssClass)) {
        		// skip header row
        		continue;
        	}
        	
        	Song curSong = new Song();
        	curSong.game = game;
        	curSong.difficulty = difficulty;
        	curSong.tierName = curTierName;
        	curSong.tierLevel = curTierLevel;
        	
        	if (dataTable.tierChildNodeCount == tr.getChildCount()) {
        		// should be the tier row
        		String[][] tierData = DataTable.TIER_ROW_FORMAT.getData(tr);
        		
        		curTierName = tierData[0][0];
        		curTierLevel++;
        		
        		continue;
        	}
        	
        	if (dataTable.rowChildNodeCount != tr.getChildCount())
        		throw new ScraperException("Invalid data row child node count, expecting " + dataTable.rowChildNodeCount + ", got " + tr.getChildCount());
        	
    		String[][] songData = dataTable.rowFormat.getData(tr);
    		// jshm.util.Print.print(songData);
    		
    		try {
    			curSong.id = Integer.parseInt(songData[0][0]);
    		} catch (NumberFormatException e) {}
    		
    		curSong.title = songData[1][0];
    		
    		try {
    			curSong.noteCount = Integer.parseInt(songData[2][0]);
    		} catch (NumberFormatException e) {}
    		
        	curSong.order = ++totalSongs;
        	
        	songs.add(curSong);
        }
	}
	
	private static void getCutoffs(
		final List<Song> songs, final Game game, final Difficulty difficulty)
	throws ScraperException {
		NodeList nodes = Scraper.scrape(
			URLs.gh.getSongStatsUrl(
				SongStat.ALL_CUTOFFS,
				game,
				difficulty));
		
		final DataTable dataTable = DataTable.ALL_CUTOFFS;
        SimpleNodeIterator it = nodes.elements();
        
    	int totalSongs = 0;
        
        while (it.hasMoreNodes()) {
        	Node node = it.nextNode();
        	
        	if (!(node instanceof TableRow)) {
        		throw new ScraperException("Expecting nodes to contain TableRows, got a " + node.getClass().getName());
        	}
        	
        	TableRow tr = (TableRow) node;
        	String cssClass = tr.getAttribute("class");
        	
        	if (null != cssClass && DataTable.HEADER_ROW_CSS_CLASS.equals(cssClass)) {
        		// skip header row
        		continue;
        	}
        	
        	Song curSong = new Song();
        	curSong.game = game;
        	curSong.difficulty = difficulty;
        	
        	if (dataTable.tierChildNodeCount == tr.getChildCount()) {
        		// should be the tier row
        		continue;
        	}
        	
        	if (dataTable.rowChildNodeCount != tr.getChildCount())
        		throw new ScraperException("Invalid data row child node count, expecting " + dataTable.rowChildNodeCount + ", got " + tr.getChildCount());
        	
    		String[][] songData = dataTable.rowFormat.getData(tr);
        	
//    		jshm.util.Print.print(songData);
//    		System.exit(0);
    		
    		for (int i = 0; i < songData.length; i++) {
    			String s = songData[i][0];
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
        		songs.get(totalSongs).setScoreAndCutoffs(curSong);
        	} catch (IndexOutOfBoundsException e) {
        		throw new ScraperException("Song list is out of sync", e);
        	}
        	
        	++totalSongs;
        }
	}
}

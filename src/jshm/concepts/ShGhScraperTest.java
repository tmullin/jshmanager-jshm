package jshm.concepts;

import java.util.*;

import jshm.Difficulty;
import jshm.*;
import jshm.gh.*;
import jshm.sh.scraper.*;

public class ShGhScraperTest {
	static final GhGame game = GhGame.GH3_XBOX360;
	static final Difficulty difficulty = Difficulty.EXPERT;
	
	public static void main(String[] args) throws Exception {
		jshm.util.TestTimer.start(true);
		
//		doSongs();
		doScores();
		
		jshm.util.TestTimer.stop(true);
	}
	
	static void doScores() throws Exception {
		jshm.sh.Client.getAuthCookies("someuser", "somepass");
		
		List<GhScore> scores = 
			GhScoreScraper.scrapeLatest(
				game, difficulty);
		
		for (Score s : scores) {
			System.out.println(s);
		}
	}
	
	static void doSongs() throws Exception {
		List<GhSong> songs = 
			GhSongScraper.scrape(
				game, difficulty);
	
		for (Song s : songs) {
			System.out.println(s);
		}
	}
}

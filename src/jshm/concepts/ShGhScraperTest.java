package jshm.concepts;

import java.util.*;

import jshm.Difficulty;
import jshm.Song;
import jshm.gh.GhGame;
import jshm.gh.GhSong;
import jshm.sh.scraper.GhSongScraper;

public class ShGhScraperTest {
	public static void main(String[] args) throws Exception {
		jshm.util.TestTimer.start(true);
		
		List<GhSong> songs = 
			GhSongScraper.scrape(
				GhGame.GH2_XBOX360, Difficulty.EXPERT);
		
		for (Song s : songs) {
			System.out.println(s);
		}
		
		jshm.util.TestTimer.stop(true);
	}
}

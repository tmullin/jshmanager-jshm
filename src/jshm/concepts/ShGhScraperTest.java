package jshm.concepts;

import java.util.*;

import jshm.sh.Difficulty;
import jshm.sh.gh.*;
import jshm.sh.gh.scraper.*;

public class ShGhScraperTest {
	public static void main(String[] args) throws Exception {
		jshm.util.TestTimer.start(true);
		
		List<GhSong> songs = 
			SongScraper.scrape(
				GhGame.GH2_XBOX360, Difficulty.EXPERT);
		
		for (GhSong s : songs) {
			System.out.println(s);
		}
		
		jshm.util.TestTimer.stop(true);
	}
}

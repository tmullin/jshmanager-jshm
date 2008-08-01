package jshm.concepts;

import java.util.*;

import jshm.sh.Difficulty;
import jshm.sh.gh.*;
import jshm.sh.gh.scraper.*;

public class ShGhScraperTest {
	public static void main(String[] args) throws Exception {
		List<Song> songs = 
			SongScraper.scrape(
				Game.GH2_XBOX360, Difficulty.EXPERT);
		
		for (Song s : songs) {
			System.out.println(s);
		}
	}
}

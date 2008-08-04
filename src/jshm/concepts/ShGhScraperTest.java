package jshm.concepts;

import java.util.*;

import jshm.sh.Difficulty;
import jshm.sh.gh.*;
import jshm.sh.gh.scraper.*;

public class ShGhScraperTest {
	public static void main(String[] args) throws Exception {
		final long start = System.currentTimeMillis();
		
		List<Song> songs = 
			SongScraper.scrape(
				Game.GH3_XBOX360, Difficulty.EXPERT);
		
		for (Song s : songs) {
			System.out.println(s);
		}
		
		final long diff = System.currentTimeMillis() - start;
		
		System.out.println("\nTime: " + diff / 1000.0);
		System.out.println("Mem: " +
			((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (double) (1 << 20)));
	}
}

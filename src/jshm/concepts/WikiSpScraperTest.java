package jshm.concepts;

import java.util.List;

import jshm.*;
import jshm.Instrument.Group;
import jshm.gh.GhSong;
import jshm.sh.links.Link;
import jshm.sh.scraper.WikiSpScraper;

public class WikiSpScraperTest {
	public static void main(String[] args) throws Exception {
		Song song = GhSong.getByScoreHeroId(96); // Ace Of Spades
		List<Link> links = WikiSpScraper.scrape(song, Group.GUITAR, Difficulty.EXPERT);
		
		for (Link l : links)
			System.out.print(l);
	}
}

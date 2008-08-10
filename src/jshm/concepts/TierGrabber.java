package jshm.concepts;

import java.util.*;

//import jshm.gh.*;
import jshm.Game;
import jshm.GameSeries;
import jshm.gh.GhGame;
import jshm.sh.scraper.GhTierScraper;

/**
 * This is an internal program to provide an easily
 * copy and paste-able list for the GameTitle enum.
 * @author Tim Mullin
 *
 */
public class TierGrabber {
	public static void main(String[] args) throws Exception {
		List<Game> games = Game.getBySeries(GameSeries.GUITAR_HERO);
		
		jshm.util.TestTimer.start();
		
		for (Game game : games) {
			List<String> tiers = GhTierScraper.scrape((GhGame) game);
			
			System.out.print(game + " = \"");
			for (int i = 0; i < tiers.size(); i++) {
				if (i > 0) System.out.print('|');
				System.out.print(tiers.get(i));
			}
			System.out.println("\",");
		}
		
		jshm.util.TestTimer.stop();
	}
}

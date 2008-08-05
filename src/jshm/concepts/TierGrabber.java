package jshm.concepts;

import java.util.*;

//import jshm.gh.*;
import jshm.sh.gh.*;
import jshm.sh.gh.scraper.*;

/**
 * This is an internal program to provide an easily
 * copy and paste-able list for the GameTitle enum.
 * @author Tim Mullin
 *
 */
public class TierGrabber {
	public static void main(String[] args) throws Exception {
		Game[] games = new Game[] {
			Game.GH1_PS2, Game.GH2_XBOX360, Game.GH80_PS2,
			Game.GH3_XBOX360, Game.GHOT_DS, Game.GHA_XBOX360
		};
		
		jshm.util.TestTimer.start();
		
		for (Game game : games) {
			List<String> tiers = TierScraper.scrape(game);
			
			System.out.print(game.title + "(\"");
			for (int i = 0; i < tiers.size(); i++) {
				if (i > 0) System.out.print('|');
				System.out.print(tiers.get(i));
			}
			System.out.println("\"),");
		}
		
		jshm.util.TestTimer.stop();
	}
}

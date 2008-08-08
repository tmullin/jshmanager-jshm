package jshm.concepts;

import java.util.*;

//import jshm.gh.*;
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
		GhGame[] games = new GhGame[] {
			GhGame.GH1_PS2, GhGame.GH2_XBOX360, GhGame.GH80_PS2,
			GhGame.GH3_XBOX360, GhGame.GHOT_DS, GhGame.GHA_XBOX360
		};
		
		jshm.util.TestTimer.start();
		
		for (GhGame game : games) {
			List<String> tiers = GhTierScraper.scrape(game);
			
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

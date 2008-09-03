package jshm.sh.links;

import java.util.List;

import jshm.Difficulty;
import jshm.Game;
import jshm.GameSeries;
import jshm.GameTitle;
import jshm.gh.GhGameTitle;
import jshm.sh.URLs;

class GhGamesTemplate extends Link {	
	GhGamesTemplate(final String name, final String urlFmt) {
		super(name);
		
		for (GameTitle t : GameTitle.getTitlesBySeries(GameSeries.GUITAR_HERO)) {
			GhGameTitle tt = (GhGameTitle) t;
			
			Link ttlLink = new Link(t.title);
			ttlLink.icon = t.getIcon();
			
			List<Game> games = Game.getByTitle(t);
			
			for (Game g : games) {
				Link gameLink = null;
				
				if (games.size() > 1) {
					gameLink = new Link(g.platform.toString());
					gameLink.icon = g.platform.getIcon();
				} else {
					gameLink = ttlLink;
				}
				
				for (Difficulty d : Difficulty.values()) {
					Link diffLink = new Link(
						d.toString(),
						String.format(URLs.BASE + "/" + urlFmt + "game=%s&diff=%s", g.scoreHeroId, d.scoreHeroId),
						d.getIcon());
					gameLink.add(diffLink);
				}
				
				if (gameLink != ttlLink) {
					ttlLink.add(gameLink);
				}
			}
			
			// create "All" item
			if (games.size() > 1) {
				Link allLink = new Link("All");
				
				for (Difficulty d : Difficulty.values()) {
					Link diffLink = new Link(
						d.toString(),
						String.format(URLs.BASE + "/" + urlFmt + "group=%s&game=0&diff=%s", tt.scoreHeroGroupId, d.scoreHeroId),
						d.getIcon());
					allLink.add(diffLink);
				}
				
				ttlLink.add(allLink);
			}
			
			add(ttlLink);
		}
	}
}

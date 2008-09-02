package jshm.sh.links;

import java.util.List;

import jshm.*;

public class ManageScoreLink {
	public static final Link GH_ROOT = new Link("Manage Scores", "http://www.scorehero.com/manage_scores.php");
	
	static {
		final String URL_FMT = "http://www.scorehero.com/manage_scores.php?&game=%s&diff=%s";
		
		for (GameTitle t : GameTitle.getTitlesBySeries(GameSeries.GUITAR_HERO)) {
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
					Link diffLink = new Link(d.toString(),
						String.format(URL_FMT, g.scoreHeroId, d.scoreHeroId));
					diffLink.icon = d.getIcon();
					gameLink.add(diffLink);
				}
				
				if (gameLink != ttlLink) {
					ttlLink.add(gameLink);
				}
			}
			
			GH_ROOT.add(ttlLink);
		}
		
		GH_ROOT.add("Manage Teams", "http://www.scorehero.com/teams.php");
		
		System.out.println(GH_ROOT);
	}
}

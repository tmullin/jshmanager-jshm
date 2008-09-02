package jshm.sh.links;

import jshm.*;
import jshm.sh.RbPlatform;

public class ManageScoreLink {
	public static final Link GH_ROOT =
		new GhGamesTemplate("Manage Scores", "manage_scores.php?&game=%s&diff=%s")
		.add("Manage Teams", "http://www.scorehero.com/teams.php");
	
	
	public static final Link RB_ROOT = new Link("Manage Scores", "http://rockband.scorehero.com/manage_scores.php");
	
	static {
		final String RB_URL_FMT = "http://rockband.scorehero.com/manage_scores.php?platform=%s&size=%s&group=%s&diff=%s";
		
		for (Platform p : new Platform[] {Platform.PS2, Platform.XBOX360, Platform.PS3, Platform.WII}) {
			Link platLink = new Link(p.toString(), p.getIcon());
			
			for (int groupSize = 1; groupSize <= 4; groupSize++) {
				Link sizeLink = new Link(groupSize + "-part");
				
				for (Instrument.Group g : Instrument.Group.getBySize(groupSize)) {
					Link groupLink =
						groupSize == 4
						? sizeLink
						: new Link(g.toString(),
							g.size == 1
							? Instrument.valueOf(g.toString()).getIcon()
							: null);
					
					for (Difficulty d : Difficulty.values()) {
						if (Difficulty.CO_OP == d) continue;
						
						Link diffLink = new Link(d.toString(),
							String.format(RB_URL_FMT, RbPlatform.getId(p), groupSize, g.id, d.scoreHeroId),
							d.getIcon());
						groupLink.add(diffLink);
					}
					
					if (groupLink != sizeLink)
						sizeLink.add(groupLink);
				}
				
				platLink.add(sizeLink);
			}
			
			RB_ROOT.add(platLink);
		}
		
		RB_ROOT.add("Manage Teams", "http://rockband.scorehero.com/teams.php");
	}
}

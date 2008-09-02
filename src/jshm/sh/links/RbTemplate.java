package jshm.sh.links;

import jshm.Difficulty;
import jshm.Instrument;
import jshm.Platform;
import jshm.sh.RbPlatform;
import jshm.sh.URLs;

public class RbTemplate extends Link {
	RbTemplate(final String name, final String urlFmt) {
		super(name);

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
							String.format(URLs.rb.BASE + "/" + urlFmt + "platform=%s&size=%s&group=%s&diff=%s", RbPlatform.getId(p), groupSize, g.id, d.scoreHeroId),
							d.getIcon());
						groupLink.add(diffLink);
					}
					
					if (groupLink != sizeLink)
						sizeLink.add(groupLink);
				}
				
				platLink.add(sizeLink);
			}
			
			add(platLink);
		}
	}
}

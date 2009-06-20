/*
 * -----LICENSE START-----
 * JSHManager - A Java-based tool for managing one's ScoreHero account.
 * Copyright (C) 2008 Tim Mullin
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * -----LICENSE END-----
*/
package jshm.sh.links;

import jshm.Difficulty;
import jshm.GameSeries;
import jshm.GameTitle;
import jshm.Instrument;
import jshm.Platform;
import jshm.rb.RbGameTitle;
import jshm.sh.RbPlatform;
import jshm.sh.URLs;

public class RbTemplate extends Link {
	RbTemplate(final String name, final String urlFmt) {
		super(name);

		for (GameTitle t : GameTitle.getBySeries(GameSeries.ROCKBAND)) {
			RbGameTitle tt = (RbGameTitle) t;
			
			Link ttlLink = new Link(t.getLongName());
			ttlLink.icon = t.getIcon();
			
			for (Platform p : t.platforms) {
				Link platLink = new Link(p.getShortName(), p.getIcon());
				
				for (int groupSize = 1; groupSize <= 4; groupSize++) {
					Link sizeLink = new Link(groupSize + "-part");
					
					for (Instrument.Group g : Instrument.Group.getBySize(groupSize)) {
						Link groupLink =
							groupSize == 4
							? sizeLink
							: new Link(g.getLongName(),
								g.size == 1
								? Instrument.valueOf(g.toString()).getIcon()
								: null);
						
						for (Difficulty d : Difficulty.values()) {
							if (Difficulty.CO_OP == d) break;
							
							Link diffLink = new Link(d.getLongName(),
								String.format(URLs.rb.BASE + "/" + urlFmt + "game=%s&platform=%s&size=%s&group=%s&diff=%s", tt.scoreHeroId, RbPlatform.getId(p), groupSize, g.id, d.scoreHeroId),
								d.getIcon());
							groupLink.add(diffLink);
						}
						
						if (groupLink != sizeLink)
							sizeLink.add(groupLink);
					}
					
					platLink.add(sizeLink);
				}
				
				ttlLink.add(platLink);
			}
			
			add(ttlLink);
		}
	}
}

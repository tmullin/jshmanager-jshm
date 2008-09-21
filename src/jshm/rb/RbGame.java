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
package jshm.rb;

import jshm.*;

public class RbGame extends Game {
	private static class RbTiers {
		public static final String[]
			RB1_DLC = "Warmup Songs|Apprentice Songs|Solid Songs|Moderate Songs|Skilled Songs|Challenging Songs|Blistering Songs|Nightmare Songs|Impossible Songs|Downloaded Songs".split("\\|"),
			RB1 = "Warmup Songs|Apprentice Songs|Solid Songs|Moderate Songs|Skilled Songs|Challenging Songs|Blistering Songs|Nightmare Songs|Impossible Songs|European Exclusives|Track Pack Volume 1".split("\\|"),
			
			RB2_DLC = "Warmup Songs|Apprentice Songs|Solid Songs|Moderate Songs|Challenging Songs|Nightmare Songs|Impossible Songs|Rock Band Imported|Downloaded Songs".split("\\|")
//			RB2 = "Warmup Songs|Apprentice Songs|Solid Songs|Moderate Songs|Challenging Songs|Nightmare Songs|Impossible Songs".split("\\|")
			;
	}
	
	public static final RbGame
		RB1_PS2 = new RbGame(RbGameTitle.RB1, RbTiers.RB1, Platform.PS2, false),
		RB1_XBOX360 = new RbGame(RbGameTitle.RB1, RbTiers.RB1_DLC, Platform.XBOX360, true),
		RB1_PS3 = new RbGame(RbGameTitle.RB1, RbTiers.RB1_DLC, Platform.PS3, true),
		RB1_WII = new RbGame(RbGameTitle.RB1, RbTiers.RB1, Platform.WII, false),
	
		RB2_XBOX360 = new RbGame(RbGameTitle.RB2, RbTiers.RB2_DLC, Platform.XBOX360, true)
		;
	
	public static void init() {}
	
	protected RbGame(RbGameTitle title, String[] tiers, Platform platform,
			boolean supportsDLC) {
		super(title.scoreHeroId, title, platform, supportsDLC);
		
		// rb tiers are the same for everything, unlike some gh games
		Tiers t = new Tiers(tiers);
		for (Instrument.Group g : title.getSupportedInstrumentGroups()) {
			mapTiers(g, t);
		}
	}
	
	public String getTierName(int tierLevel) {
		return getTierName(Instrument.Group.GUITAR, tierLevel);
	}
	
	public int getTierLevel(final String tierName) {
		return getTierLevel(Instrument.Group.GUITAR, tierName);
	}
	
	public int getTierCount() {
		return getTierCount(Instrument.Group.GUITAR);
	}
}

package jshm.rb;

import jshm.Game;
import jshm.GameTitle;
import jshm.Instrument;
import jshm.Platform;
import jshm.Tiers;

public class RbGame extends Game {
	private static class RbTiers {
		public static final String[]
			RB1_DLC = "Warmup Songs|Apprentice Songs|Solid Songs|Moderate Songs|Skilled Songs|Challenging Songs|Blistering Songs|Nightmare Songs|Impossible Songs|Downloaded Songs".split("\\|"),
			RB1 = "Warmup Songs|Apprentice Songs|Solid Songs|Moderate Songs|Skilled Songs|Challenging Songs|Blistering Songs|Nightmare Songs|Impossible Songs|Track Pack Volume 1".split("\\|")
			;
	}
	
	public static final RbGame
		RB1_PS2 = new RbGame(RbGameTitle.RB1, RbTiers.RB1, Platform.PS2, false),
		RB1_XBOX360 = new RbGame(RbGameTitle.RB1, RbTiers.RB1_DLC, Platform.XBOX360, true),
		RB1_PS3 = new RbGame(RbGameTitle.RB1, RbTiers.RB1_DLC, Platform.PS3, true),
		RB1_WII = new RbGame(RbGameTitle.RB1, RbTiers.RB1, Platform.WII, false)
		;
	
	public static void init() {}
	
	protected RbGame(GameTitle title, String[] tiers, Platform platform,
			boolean supportsDLC) {
		super(0, title, platform, supportsDLC);
		
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

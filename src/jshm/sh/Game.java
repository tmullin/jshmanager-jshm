package jshm.sh;

import jshm.gh.Platform;

/**
 * This class represents a game from ScoreHero's perspective.
 * @author Tim
 *
 */
public enum Game {
	UNKNOWN(0, Platform.UNKNOWN),
	
	GH1_PS2(1, Platform.PS2),
	
	GH2_PS2(2, Platform.PS2),
	GH2_XBOX360(3, Platform.XBOX360),
	
	GH80_PS2(4, Platform.PS2),
	
	GH3_PS2(5, Platform.PS2),
	GH3_XBOX360(6, Platform.XBOX360),
	GH3_PS3(7, Platform.PS3),
	GH3_WII(8, Platform.WII),
	GH3_PC(9, Platform.PC),
	
	;
	
	public final int		scoreHeroId;
	public final Platform 	platform;
	
	private Game(final int scoreHeroId, final Platform platform) {
		this.scoreHeroId = scoreHeroId;
		this.platform = platform;
	}
}

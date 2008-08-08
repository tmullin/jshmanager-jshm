package jshm.sh.gh;

import jshm.*;
import jshm.gh.GhGameTitle;

/**
 * This class represents a Guitar Hero game as far as it
 * is represented on ScoreHero.
 * @author Tim Mullin
 *
 */
public class GhGame extends jshm.Game {	
	public static final GhGame
		GH1_PS2 = new GhGame(1, GhGameTitle.GH1, Platform.PS2),
		
		GH2_PS2 = new GhGame(2, GhGameTitle.GH2, Platform.PS2),
		GH2_XBOX360 = new GhGame(3, GhGameTitle.GH2, Platform.XBOX360),
		
		GH80_PS2 = new GhGame(4, GhGameTitle.GH80, Platform.PS2),
		
		GH3_PS2 = new GhGame(5, GhGameTitle.GH3, Platform.PS2),
		GH3_XBOX360 = new GhGame(6, GhGameTitle.GH3, Platform.XBOX360),
		GH3_PS3 = new GhGame(7, GhGameTitle.GH3, Platform.PS3),
		GH3_WII = new GhGame(8, GhGameTitle.GH3, Platform.WII),
		GH3_PC = new GhGame(9, GhGameTitle.GH3, Platform.PC),
		
		GHOT_DS = new GhGame(10, GhGameTitle.GHOT, Platform.DS),
		
		GHA_PS2 = new GhGame(11, GhGameTitle.GHA, Platform.PS2),
		GHA_XBOX360 = new GhGame(12, GhGameTitle.GHA, Platform.XBOX360),
		GHA_PS3 = new GhGame(13, GhGameTitle.GHA, Platform.PS3),
		GHA_WII = new GhGame(14, GhGameTitle.GHA, Platform.WII)
	;
	
	/**
	 * Return the correct GhGame for the given id
	 * @param id
	 * @return
	 */
	public static GhGame getById(final int id) {
		for (Game g : values()) {
			if ((g instanceof GhGame) && g.scoreHeroId == id) return (GhGame) g;
		}
		
		throw new IllegalArgumentException("invalid GhGame id: " + id);
	}
	
	protected GhGame(final int scoreHeroId, final GhGameTitle title, final Platform platform) {
		super(scoreHeroId, title, platform);
	}

	
	// override Object methods
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof GhGame)) return false;
		
		GhGame g = (GhGame) o;
		
		return
			this.scoreHeroId == g.scoreHeroId &&
			this.title == g.title &&
			this.platform == g.platform;
	}
	
	@Override
	public String toString() {
		return title + "_" + platform;
	}
}

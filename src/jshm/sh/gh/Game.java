package jshm.sh.gh;

import jshm.gh.Platform;
import jshm.gh.GameTitle;

/**
 * This class represents a Guitar Hero game as far as it
 * is represented on ScoreHero.
 * @author Tim Mullin
 *
 */
public enum Game {	
	GH1_PS2(1, Platform.PS2, GameTitle.GH1),
	
	GH2_PS2(2, Platform.PS2, GameTitle.GH2),
	GH2_XBOX360(3, Platform.XBOX360, GameTitle.GH2),
	
	GH80_PS2(4, Platform.PS2, GameTitle.GH80),
	
	GH3_PS2(5, Platform.PS2, GameTitle.GH3),
	GH3_XBOX360(6, Platform.XBOX360, GameTitle.GH3),
	GH3_PS3(7, Platform.PS3, GameTitle.GH3),
	GH3_WII(8, Platform.WII, GameTitle.GH3),
	GH3_PC(9, Platform.PC, GameTitle.GH3),
	
	GHOT_DS(10, Platform.DS, GameTitle.GHOT),
	
	GHA_PS2(11, Platform.PS2, GameTitle.GHA),
	GHA_XBOX360(12, Platform.XBOX360, GameTitle.GHA),
	GHA_PS3(13, Platform.PS3, GameTitle.GHA),
	GHA_WII(14, Platform.WII, GameTitle.GHA)
	;
	
	/**
	 * The minimum number of stars that can be obtained.
	 */
	public static final int	MIN_STARS = 3;
	
	/**
	 * The maximum number of stars that the game will report.
	 */
	public static final int	MAX_OBSERVABLE_STARS = 5;
	
	/**
	 * The maximum number of stars that can be calculated given the score.
	 */
	public static final int	MAX_CALCULABLE_STARS = 9;
	
	/**
	 * Return the correct GhGame for the given id
	 * @param id
	 * @return
	 */
	public static Game getById(int id) {
		for (Game g : Game.values()) {
			if (g.id == id) return g;
		}
		
		return null;
	}
	
	public static Game getByPlatformAndTitle(Platform platform, GameTitle title) {
		for (Game g : Game.values()) {
			if (g.platform == platform && g.title == title) return g;
		}
		
		return null;
	}
	
	/**
	 * The internal ScoreHero id for this game.
	 */
	public final int		id;
	public final Platform 	platform;
	public final GameTitle	title;
	
	private Game(final int id, final Platform platform, final GameTitle title) {
		this.id = id;
		this.platform = platform;
		this.title = title;
	}
}

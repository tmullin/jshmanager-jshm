package jshm.sh.gh;

import jshm.Difficulty;
import jshm.Instrument;
import jshm.gh.Platform;
import jshm.gh.GameTitle;

// TODO pull up the platform and gmae title and comvert them
// to be more generic.

/**
 * This class represents a Guitar Hero game as far as it
 * is represented on ScoreHero.
 * @author Tim Mullin
 *
 */
public class GhGame extends jshm.Game {	
	public static final GhGame
		GH1_PS2 = new GhGame(1, Platform.PS2, GameTitle.GH1),
		
		GH2_PS2 = new GhGame(2, Platform.PS2, GameTitle.GH2),
		GH2_XBOX360 = new GhGame(3, Platform.XBOX360, GameTitle.GH2),
		
		GH80_PS2 = new GhGame(4, Platform.PS2, GameTitle.GH80),
		
		GH3_PS2 = new GhGame(5, Platform.PS2, GameTitle.GH3),
		GH3_XBOX360 = new GhGame(6, Platform.XBOX360, GameTitle.GH3),
		GH3_PS3 = new GhGame(7, Platform.PS3, GameTitle.GH3),
		GH3_WII = new GhGame(8, Platform.WII, GameTitle.GH3),
		GH3_PC = new GhGame(9, Platform.PC, GameTitle.GH3),
		
		GHOT_DS = new GhGame(10, Platform.DS, GameTitle.GHOT),
		
		GHA_PS2 = new GhGame(11, Platform.PS2, GameTitle.GHA),
		GHA_XBOX360 = new GhGame(12, Platform.XBOX360, GameTitle.GHA),
		GHA_PS3 = new GhGame(13, Platform.PS3, GameTitle.GHA),
		GHA_WII = new GhGame(14, Platform.WII, GameTitle.GHA)
	;
	
	private static final GhGame[] values = new GhGame[] {
		GH1_PS2,
		GH2_PS2, GH2_XBOX360,
		GH80_PS2, 
		GH3_PS2, GH3_XBOX360, GH3_PS3, GH3_WII, GH3_PC,
		GHOT_DS,
		GHA_PS2, GHA_XBOX360, GHA_PS3, GHA_WII
	};
	
	private static final Instrument.Group[] DEFAULT_SUPPORTED_INSTRUMENT_GROUPS = new Instrument.Group[] {
		Instrument.Group.GUITAR, Instrument.Group.GUITAR_BASS
	};
	
	private static final int
		MIN_STARS			 = 3,
		MAX_STARS			 = 5,
		MAX_CALCULABLE_STARS = 9;
	
	/**
	 * Return the correct GhGame for the given id
	 * @param id
	 * @return
	 */
	public static GhGame getById(int id) {
		for (GhGame g : values) {
			if (g.id == id) return g;
		}
		
		throw new IllegalArgumentException("invalid game id: " + id);
	}
	
	public static GhGame getByPlatformAndTitle(Platform platform, GameTitle title) {
		for (GhGame g : values) {
			if (g.platform == platform && g.title == title) return g;
		}
		
		throw new IllegalArgumentException("invalid platform and title combination: " + platform + " and " + title);
	}
	
	/**
	 * The internal ScoreHero id for this game.
	 */
	public final int		id;
	public final Platform 	platform;
	public final GameTitle	title;
	
	private GhGame(final int id, final Platform platform, final GameTitle title) {
		this.id = id;
		this.platform = platform;
		this.title = title;
	}

	
	// override Object methods
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof GhGame)) return false;
		
		GhGame g = (GhGame) o;
		
		return
			this.id == g.id &&
			this.title == g.title &&
			this.platform == g.platform;
	}
	
	@Override
	public String toString() {
		return title + "_" + platform;
	}
	
	
	// override abstract methods
	
	@Override
	public int getMaxStars() {
		return MAX_STARS;
	}

	@Override
	public int getMinStars() {
		return MIN_STARS;
	}

	@Override
	public boolean supportsCalculableStars() {
		return true;
	}
	
	@Override
	public float getMaxCalculableStars() {
		return MAX_CALCULABLE_STARS;
	}

	@Override
	public Difficulty.Strategy getDifficultyStrategy() {
		return Difficulty.Strategy.BY_SONG;
	}
	
	@Override
	public Instrument.Group[] getSupportedInstrumentGroups() {
		return DEFAULT_SUPPORTED_INSTRUMENT_GROUPS;
	}
}

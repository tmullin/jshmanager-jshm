package jshm.rb;

import jshm.GameSeries;
import jshm.GameTitle;
import jshm.Platform;
import jshm.StreakStrategy;
import jshm.Difficulty.Strategy;
import jshm.Instrument.Group;

public class RbGameTitle extends GameTitle {
	public static final RbGameTitle
		RB1 = new RbGameTitle(1, "RB1", Platform.PS2, Platform.XBOX360, Platform.PS3, Platform.WII),
		RB2 = new RbGameTitle(2, "RB2", /*Platform.PS2,*/ Platform.XBOX360/*, Platform.PS3, Platform.WII*/)
	;
	
	public final int scoreHeroId;
	
	protected RbGameTitle(int scoreHeroId, String title, Platform ... platforms) {
		super(GameSeries.ROCKBAND, title, platforms);
		this.scoreHeroId = scoreHeroId;
	}

	@Override
	public Strategy getDifficultyStrategy() {
		return Strategy.BY_SCORE;
	}

	@Override
	public int getMaxRating() {
		return 6;
	}

	@Override
	public int getMinRating() {
		return 1;
	}

	@Override
	public StreakStrategy getStreakStrategy() {
		return StreakStrategy.BY_PART;
	}

	@Override
	public Group[] getSupportedInstrumentGroups() {
		return Group.values();
	}

	@Override
	public boolean supportsCalculatedRating() {
		return false;
	}
}

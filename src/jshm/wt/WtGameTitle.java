package jshm.wt;

import jshm.*;
import jshm.Instrument.Group;
import jshm.Song.Sorting;

public class WtGameTitle extends GameTitle {
	public static void init() {}
	
	public static final WtGameTitle
		GHWT = new WtGameTitle("GHWT", 7, false, Platform.BIG_FOUR),
		GHM = new WtGameTitle("GHM", 9, true, Platform.BIG_FOUR),
		GHSH = new WtGameTitle("GHSH", 11, true, Platform.BIG_FOUR)
	;

	public final int scoreHeroGroupId;
	public final boolean supportsExpertPlus;
	
	protected WtGameTitle(String title, int scoreHeroGroupId, boolean supportsExpertPlus, Platform ... platforms) {
		super(GameSeries.WORLD_TOUR, title, platforms);
		this.scoreHeroGroupId = scoreHeroGroupId;
		this.supportsExpertPlus = supportsExpertPlus;
	}

	@Override
	public Difficulty.Strategy getDifficultyStrategy() {
		return Difficulty.Strategy.BY_SCORE;
	}

	@Override
	public int getMaxRating() {
		return 5;
	}

	@Override
	public int getMinRating() {
		return 3;
	}

	@Override
	public StreakStrategy getStreakStrategy() {
		return StreakStrategy.BY_PART;
	}

	// TODO add ghwt drums
	private static final Group[] SUPPORTED_GROUPS = {
		Group.GUITAR, Group.BASS, Group.DRUMS, Group.VOCALS
	};
	
	@Override
	public Group[] getSupportedInstrumentGroups() {
		return SUPPORTED_GROUPS;
	}

	private static final Sorting[] SUPPORTED_SORTINGS = {
		Sorting.SCOREHERO, Sorting.TITLE
	};
	
	@Override
	public Sorting[] getSupportedSortings() {
		return SUPPORTED_SORTINGS;
	}

	@Override
	public boolean supportsCalculatedRating() {
		return false;
	}
}

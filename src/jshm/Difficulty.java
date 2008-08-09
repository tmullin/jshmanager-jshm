package jshm;

/**
 * This enum represents a song difficulty from
 * ScoreHero's point of view.
 * @author Tim Mullin
 *
 */
public enum Difficulty {
	EASY(1, "E"),
	MEDIUM(2, "M"),
	HARD(3, "H"),
	EXPERT(4, "X");
	
	public  final int	 scoreHeroId;
	private final String shortName;
	
	private Difficulty(final int scoreHeroId, final String shortName) {
		this.scoreHeroId = scoreHeroId;
		this.shortName = shortName;
	}
	
	public String toShortString() {
		return shortName;
	}
	
	public static Difficulty getByScoreHeroId(final int scoreHeroId) {
		for (Difficulty d : Difficulty.values())
			if (d.scoreHeroId == scoreHeroId)
				return d;
		
		throw new IllegalArgumentException("invalid difficulty scoreHeroId: " + scoreHeroId);
	}
	
	/**
	 * This represents where in the data model difficulty
	 * information is stored. GH is BY_SONG, RB is BY_SCORE.
	 * 
	 * ScoreHero gives each GH song a separate id for each
	 * difficulty level whereas each RB song always has the
	 * same id.
	 * 
	 * @author Tim Mullin
	 *
	 */
	public static enum Strategy {
		BY_SONG, BY_SCORE
	}
}

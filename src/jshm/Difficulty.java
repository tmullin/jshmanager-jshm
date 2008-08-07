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
	
	public  final int	 id;
	private final String shortName;
	
	private Difficulty(final int id, final String shortName) {
		this.id = id;
		this.shortName = shortName;
	}
	
	public String toShortString() {
		return shortName;
	}
	
	@Override
	public String toString() {
		return toShortString();
	}
	
	public static Difficulty getById(final int id) {
		for (Difficulty d : Difficulty.values())
			if (d.id == id)
				return d;
		
		throw new IllegalArgumentException("invalid difficulty id: " + id);
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

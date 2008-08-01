package jshm.sh;

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
	EXPERT(4, "X"); //,
//	CO_OP(5); // don't worry about co-op for now
	
	public final int id;
	private final String shortName;
	
	private Difficulty(final int id, final String shortName) {
		this.id = id;
		this.shortName = shortName;
	}
	
	public String toShortString() {
		return shortName;
	}
}

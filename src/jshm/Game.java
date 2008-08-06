package jshm;

/**
 * Represents an abstract game from this program's
 * point of view. This exists in preparation for
 * eventual RockBand support.
 * @author Tim Mullin
 *
 */
public abstract class Game {
	/**
	 * The minimum number of stars that can be obtained
	 * for a song.
	 */
	public abstract int getMinStars();
	
	/**
	 * The maximum number of stars that the game will report.
	 */
	public abstract int getMaxObservableStars();
	
	/**
	 * Whether we can calculate more stars than the
	 * max number of observable stars.
	 * @return
	 */
	public abstract boolean supportsCalculableStars();
	
	/**
	 * The maximum number of stars that we know how to calculate.
	 * This method must be overridden if supportsCalculableStars()
	 * is overridden to return true.
	 */
	public int getMaxCalculableStars() {
		if (!supportsCalculableStars())
			throw new UnsupportedOperationException();
		
		throw new UnsupportedOperationException("supportsCalculableStars() is true but getMaxCalculableStars() is not implemented");
	}
}

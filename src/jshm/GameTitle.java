package jshm;

import java.util.*;


/**
 * Represents a specific game title such as Guitar Hero 2
 * or Rockband. This is more for containing information about
 * a given title that is consistent across platforms.
 * @author Tim Mullin
 *
 */
public abstract class GameTitle {
	private static final List<GameTitle> values =
		new ArrayList<GameTitle>();
	
	public static List<GameTitle> values() {
		return values;
	}
	
	public static GameTitle valueOf(String value) {
		for (GameTitle g : values)
			if (g.title.equals(value))
				return g;
		
		return null;
	}
	
	public static List<GameTitle> getTitlesBySeries(final GameSeries series) {
		List<GameTitle> ret = new ArrayList<GameTitle>();
		
		for (GameTitle g : values) {
			if (g.series.equals(series)) {
				ret.add(g);
			}
		}
		
		return ret;
	}
	
	static {
		jshm.gh.GhGameTitle.init();
	}
	
	
	public final GameSeries series;
	public final String		title;
	public final Platform[] platforms;
	
	protected GameTitle(final GameSeries series, final String title, final Platform ... platforms) {
		values.add(this);
		
		this.series = series;
		this.title = title;
		this.platforms = platforms;
	}
	
	public abstract Difficulty.Strategy getDifficultyStrategy();
	
	/**
	 * The minimum number of stars that can be obtained
	 * for a song.
	 */
	public abstract int getMinRating();
	
	/**
	 * The maximum number of stars that the game will report.
	 */
	public abstract int getMaxRating();
	
	/**
	 * 
	 * @return Whether we can calculate more stars than the
	 * max number of observable stars.
	 */
	public abstract boolean supportsCalculatedRating();
	
	/**
	 * If calculable stars are supported, this will return
	 * getMinStars() by default.
	 * @return
	 */
	public float getMinCalculatedRating() {
		if (!supportsCalculatedRating())
			throw new UnsupportedOperationException();
		return (float) getMinRating();
	}
	
	/**
	 * The maximum number of stars that we know how to calculate.
	 * This method must be overridden if supportsCalculableStars()
	 * is overridden to return true.
	 */
	public float getMaxCalculatedRating() {
		if (!supportsCalculatedRating())
			throw new UnsupportedOperationException();	
		throw new UnsupportedOperationException("supportsCalculatedRating() is true but getMaxCalculatedRating() is not implemented");
	}
	
	/**
	 * 
	 * @return An array of the possible instrument combinations this game supports.
	 */
	public abstract Instrument.Group[] getSupportedInstrumentGroups();
	
	public abstract StreakStrategy getStreakStrategy();
	
	// override Object methods
	
	public String toString() {
		return title;
	}
	
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof GameTitle)) return false;
		
		GameTitle g = (GameTitle) o;
		
		return
			this.series.equals(g.series) &&
			this.title.equals(g.title) &&
			Arrays.equals(this.platforms, g.platforms);
	}
}

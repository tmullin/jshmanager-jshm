package jshm;

import java.util.*;

/**
 * This represents a specific {@link GameTitle} and
 * {@link Platform} combination in case there are differences
 * between versions.
 * @author Tim Mullin
 *
 */
public abstract class Game {
	private static List<Game> values = new ArrayList<Game>();
	
	public static List<Game> values() {
		return values;
	}
	
	public static Game valueOf(final String value) {
		for (Game g : values)
			if (g.toString().equals(value))
				return g;
		
		throw new IllegalArgumentException("invalid value: " + value);
	}
	
	public static List<Game> getBySeries(final GameSeries series) {
		final List<Game> ret = new ArrayList<Game>();
		
		for (Game g : values)
			if (g.title.series.equals(series))
				ret.add(g);
		
		return ret;
	}
	
	public static List<Game> getByTitle(final GameTitle title) {
		final List<Game> ret = new ArrayList<Game>();
		
		for (Game g : values)
			if (g.title.equals(title))
				ret.add(g);
		
		return ret;
	}
	
	public static Game getByTitleAndPlatform(final GameTitle title, final Platform platform) {
		for (Game g : values) {
			if (g.platform.equals(platform) && g.title.equals(title)) return g;
		}
		
		throw new IllegalArgumentException("invalid title/platform combination: " + title + "_" + platform);
	}
	
	
	
	public final int scoreHeroId;
	public final GameTitle title;
	public final Platform platform;
	
	protected Game(final int scoreHeroId, final GameTitle title, final Platform platform) {
		this.scoreHeroId = scoreHeroId;
		this.title = title;
		this.platform = platform;
		values.add(this);
	}
	

	// override Object methods
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Game)) return false;
		
		Game g = (Game) o;
		
		return
			this.title.equals(title) &&
			this.platform.equals(g.platform);
	}
	
	@Override
	public String toString() {
		return title + "_" + platform;
	}
}

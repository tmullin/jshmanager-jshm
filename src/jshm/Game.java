/*
 * -----LICENSE START-----
 * JSHManager - A Java-based tool for managing one's ScoreHero account.
 * Copyright (C) 2008 Tim Mullin
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * -----LICENSE END-----
 */
package jshm;

import java.util.*;

import jshm.Instrument.Group;
import jshm.Song.Sorting;
import jshm.gh.GhGame;
import jshm.rb.RbGame;
import jshm.util.Text;
import jshm.wt.WtGame;

/**
 * This represents a specific {@link GameTitle} and
 * {@link Platform} combination in case there are differences
 * between versions.
 * @author Tim Mullin
 *
 */
public abstract class Game {
	private static final List<Game> values = new ArrayList<Game>();
	
	public static List<Game> values() {
		return values;
	}
	
	public static Game valueOf(final String value) {
		if (null == value) return null;
		
		for (Game g : values)
			if (g.toString().equals(value))
				return g;
		
		return null;
//		throw new IllegalArgumentException("invalid value: " + value);
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
	
	static {
		// needed to ensure the GhGames get put into values
		GhGame.init();
		WtGame.init();
		RbGame.init();
	}
	
	public final int scoreHeroId;
	public final GameTitle title;
	public final Platform platform;
	public final boolean supportsDLC;
	
	private final Map<Instrument.Group, Tiers> tiersMap =
		new HashMap<Instrument.Group, Tiers>();
	
	protected Game(
		final int scoreHeroId,
		final GameTitle title,
		final Platform platform,
		final boolean supportsDLC) {
		
		this.scoreHeroId = scoreHeroId;
		this.title = title;
		this.platform = platform;
		this.supportsDLC = supportsDLC;
		
		values.add(this);
	}
	
	protected void mapTiers(final Group group, final String[] tiers) {
		mapTiers(group, new Tiers(tiers));
	}
	
	protected void mapTiers(final Group group, final Tiers tiers) {
		tiersMap.put(group, tiers);
	}

	/**
	 * 
	 * @param tierLevel
	 * @return The name of the 1-based tier level
	 */
	public String getTierName(final int tierLevel) {
		throw new UnsupportedOperationException();
	}
	
	public String getTierName(final Sorting sorting, final int tierLevel) {
		if (Sorting.SCOREHERO == sorting)
			return getTierName(tierLevel);
		
		if (null != title.sortingTiersMap.get(sorting))
			return title.sortingTiersMap.get(sorting).getName(tierLevel);
		
		throw new UnsupportedOperationException("sorting not implemented: " + sorting);
	}
	
	public String getTierName(final Group group, final int tierLevel) {
		if (null == tiersMap.get(group)) return "UNKNOWN";
		return tiersMap.get(group).getName(tierLevel);
	}
	
	public int getTierLevel(final String tierName) {
		throw new UnsupportedOperationException();
	}
	
	public int getTierLevel(final Group group, final String tierName) {
		if (null == tiersMap.get(group)) return 0;
		return tiersMap.get(group).getLevel(tierName);
	}
	
	public int getTierCount() {
		throw new UnsupportedOperationException();
	}
	
	public int getTierCount(final Sorting sorting) {
		if (Sorting.SCOREHERO == sorting)
			return getTierCount();
		
		if (null != title.sortingTiersMap.get(sorting))
			return title.sortingTiersMap.get(sorting).getCount();
		
		throw new UnsupportedOperationException("sorting not implemented: " + sorting);
	}
	
	public int getTierCount(final Instrument.Group group) {
		if (null == tiersMap.get(group)) return 0;
		return tiersMap.get(group).getCount();
	}
	
	public Comparator<Song> getSortingComparator(final Sorting sorting) {
		if (null == title.sortingComparatorMap.get(sorting)) {
			if (null == sorting.comparator)
				throw new UnsupportedOperationException("sorting not implemented: " + sorting);
			return sorting.comparator;
		}
		
		return title.sortingComparatorMap.get(sorting);
	}
	
	public List<? extends Song> getSongs(Group group, Difficulty diff) {
		return getSongs(group, diff, null);
	}
	
	public abstract List<? extends Song> getSongs(Group group, Difficulty diff, Song.Sorting sorting);
	public abstract List<? extends Song> getAllSongsByTitle(String title, Difficulty diff);
	public abstract Song getSongByTitle(String title, Difficulty diff);
	public abstract Song getSongByScoreHeroId(int scoreHeroId, Difficulty diff);
	public abstract List<? extends Song> getSongsOrderedByTitle(Group group, Difficulty diff);

	public abstract List<? extends Score> getScores(Group group, Difficulty diff);
	public abstract List<? extends Score> getSubmittableScores(Group group, Difficulty diff);
	
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

	/**
	 * Creates a 1-part score. Note it is not saved to the db.
	 * @param song
	 * @param group
	 * @param diff
	 * @param score
	 * @param rating
	 * @param percent
	 * @param streak
	 * @param comment
	 * @return
	 */
	public abstract Score createNewScore(Song song, Group group,
			Difficulty diff, int score, int rating, float percent,
			int streak, String comment);
	
	
	
	private static Text t = null;
	
	protected final String getText(String key) {
		if (null == t)
			t = new Text(Game.class);
		
		return t.get(toString() + "." + key);
	}
}

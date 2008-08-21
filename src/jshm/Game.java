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

import jshm.gh.GhGame;

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
	
	static {
		// needed to ensure the GhGames get put into values
		GhGame.init();
	}
	
	public final int scoreHeroId;
	public final GameTitle title;
	public final Platform platform;
	public final boolean supportsDLC;
	
	protected final Map<Instrument.Group, Tiers> tiersMap =
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

	protected void mapTiers(final Instrument.Group group, final String[] tiers) {
		mapTiers(group, new Tiers(tiers));
	}
	
	protected void mapTiers(final Instrument.Group group, final Tiers tiers) {
		tiersMap.put(group, tiers);
	}
	
	public String getTierName(final Instrument.Group group, final int tierLevel) {
		if (!tiersMap.containsKey(group)) return "UNKNOWN";
		return tiersMap.get(group).getName(tierLevel);
	}
	
	public int getTierLevel(final Instrument.Group group, final String tierName) {
		if (!tiersMap.containsKey(group)) return 0;
		return tiersMap.get(group).getLevel(tierName);
	}
	
	public int getTierCount(final Instrument.Group group) {
		if (!tiersMap.containsKey(group)) return 0;
		return tiersMap.get(group).getCount();
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

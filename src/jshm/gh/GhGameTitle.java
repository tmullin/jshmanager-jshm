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
package jshm.gh;

import jshm.*;

public class GhGameTitle extends jshm.GameTitle {
	public static void init() {}
	
	public static final GhGameTitle
		GH1  = new GhGameTitle("GH1", 1, false, Platform.PS2),
		GH2  = new GhGameTitle("GH2", 2, Platform.PS2, Platform.XBOX360),
		GH80 = new GhGameTitle("GH80", 3, Platform.PS2),
		GH3  = new GhGameTitle("GH3", 4, Platform.PS2, Platform.XBOX360, Platform.PS3, Platform.WII, Platform.PC),
		GHOT = new GhGameTitle("GHOT", 5, false, Platform.DS),
		GHA  = new GhGameTitle("GHA", 6, Platform.PS2, Platform.XBOX360, Platform.PS3, Platform.WII)
	;
	
	public final int scoreHeroGroupId;
	
	/**
	 * Whether the game itself supports co-op, regardless of
	 * JSHManager's support for co-op.
	 */
	public final boolean supportsCoOp;
	
	private GhGameTitle(final String title, final int scoreHeroGroupId, final Platform ... platforms) {
		this(title, scoreHeroGroupId, true, platforms);
	}
	
	private GhGameTitle(final String title, final int scoreHeroGroupId, final boolean supportsCoOp, final Platform ... platforms) {
		super(GameSeries.GUITAR_HERO, title, platforms);
		this.scoreHeroGroupId = scoreHeroGroupId;
		this.supportsCoOp = supportsCoOp;
	}
	
	public static final Instrument.Group SINGLE_PLAYER_GROUP = Instrument.Group.GUITAR;
	public static final Instrument.Group CO_OP_PLAYER_GROUP = Instrument.Group.GUITAR_BASS;
	
	// override abstract methods
	
	private static final Instrument.Group[] SUPPORTED_INSTRUMENT_GROUPS = new Instrument.Group[] {
		SINGLE_PLAYER_GROUP /*, CO_OP_PLAYER_GROUP*/
	};
	
	private static final int
		MIN_STARS			 = 3,
		MAX_STARS			 = 5,
		MIN_CALCULABLE_STARS = 0,
		MAX_CALCULABLE_STARS = 9;
	
	@Override
	public int getMaxRating() {
		return MAX_STARS;
	}

	@Override
	public int getMinRating() {
		return MIN_STARS;
	}

	@Override
	public boolean supportsCalculatedRating() {
		return true;
	}
	
	@Override
	public float getMinCalculatedRating() {
		return MIN_CALCULABLE_STARS;
	}
	
	@Override
	public float getMaxCalculatedRating() {
		return MAX_CALCULABLE_STARS;
	}

	@Override
	public Difficulty.Strategy getDifficultyStrategy() {
		return Difficulty.Strategy.BY_SONG;
	}
	
	@Override
	public Instrument.Group[] getSupportedInstrumentGroups() {
		return SUPPORTED_INSTRUMENT_GROUPS;
	}

	@Override
	public StreakStrategy getStreakStrategy() {
		return StreakStrategy.BY_SCORE;
	}
}

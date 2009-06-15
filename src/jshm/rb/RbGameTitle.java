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
package jshm.rb;

import jshm.GameSeries;
import jshm.GameTitle;
import jshm.Platform;
import jshm.Song;
import jshm.StreakStrategy;
import jshm.Difficulty.Strategy;
import jshm.Instrument.Group;

public class RbGameTitle extends GameTitle {
	public static void init() {}
	
	public static final RbGameTitle
		RB1 = new RbGameTitle(1, "RB1", Platform.BIG_FOUR),
		RB2 = new RbGameTitle(2, "RB2", Platform.BIG_FOUR)
	;
	
	public final int scoreHeroId;
	
	protected RbGameTitle(int scoreHeroId, String title, Platform ... platforms) {
		super(GameSeries.ROCKBAND, title, platforms);
		this.scoreHeroId = scoreHeroId;
	}

	@Override
	public Strategy getDifficultyStrategy() {
		return Strategy.BY_SCORE;
	}

	@Override
	public int getMaxRating() {
		return 6;
	}

	@Override
	public int getMinRating() {
		return 1;
	}

	@Override
	public StreakStrategy getStreakStrategy() {
		return StreakStrategy.BY_PART;
	}

	@Override
	public Group[] getSupportedInstrumentGroups() {
		return Group.getBySize(1).toArray(new Group[0]);
	}
	
	private static final Song.Sorting[] SUPPORTED_SORTINGS = {
		Song.Sorting.SCOREHERO, Song.Sorting.TITLE
	};
	
	@Override
	public Song.Sorting[] getSupportedSortings() {
		return SUPPORTED_SORTINGS;
	}

	@Override
	public boolean supportsCalculatedRating() {
		return false;
	}
}

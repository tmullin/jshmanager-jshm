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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jshm.*;
import jshm.Song.Sorting;

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
	
	/*
	 * (non-Javadoc)
	 * implemented Sortings:
	 *   DECADE, GENRE, ARTIST
	 * @see jshm.Game#initDynamicTiersInternal()
	 */
	@SuppressWarnings("unchecked")
	@Override protected void initDynamicTiersInternal() {		
		List<String> tiers = new ArrayList<String>();
		List<String> list_s = null;
		List<Integer> list_i = null;
		
		org.hibernate.Session session = jshm.hibernate.HibernateUtil.getCurrentSession();
	    session.beginTransaction();
	    
	    // make the decades tier
	    list_i = (List<Integer>) session.createQuery(
	    	"SELECT DISTINCT (year / 10) FROM GhSong WHERE game LIKE :game AND year IS NOT NULL AND year <> 0")
	    	.setString("game", this.title + "_%")
	    	.list();
	    
	    tiers.clear();
	    tiers.add("<UNKNOWN>");
	    
    	Collections.sort(list_i);
    
	    for (Integer i : list_i) {
	    	tiers.add(String.valueOf(i * 10) + "s");
	    }
	    
//	    System.out.println("GH Decade List");
//	    jshm.util.Print.print(tiers, "\t");
	    
	    mapTiers(Sorting.DECADE, tiers);
	    
	    
	    // make genre tier
	    list_s = (List<String>) session.createQuery(
	    	"SELECT DISTINCT genre FROM GhSong WHERE game LIKE :game AND genre IS NOT NULL ORDER BY genre")
	    	.setString("game", this.title + "_%")
	    	.list();
	    
	    tiers.clear();
	    tiers.addAll(list_s);
	    
	    Collections.sort(tiers, String.CASE_INSENSITIVE_ORDER);
		tiers.add(0, "<UNKNOWN>");
//	    System.out.println("GH Genre List");
//	    jshm.util.Print.print(tiers, "\t");
	    
	    mapTiers(Sorting.GENRE, tiers);
	    
	    
	    // make artist tier
	    list_s = (List<String>) session.createQuery(
	    	"SELECT DISTINCT artist FROM GhSong WHERE game LIKE :game AND artist IS NOT NULL ORDER BY artist")
	    	.setString("game", this.title + "_%")
	    	.list();
	    
	    tiers.clear();
	    tiers.addAll(list_s);
	    
	    Collections.sort(tiers, String.CASE_INSENSITIVE_ORDER);
		tiers.add(0, "<UNKNOWN>");
//	    System.out.println("GH Artists List");
//	    jshm.util.Print.print(tiers, "\t");
	    
	    mapTiers(Sorting.ARTIST, tiers);
	    
	    session.getTransaction().commit();
	}
	
	public static final Instrument.Group SINGLE_PLAYER_GROUP = Instrument.Group.GUITAR;
	public static final Instrument.Group CO_OP_PLAYER_GROUP = Instrument.Group.GUITAR_BASS;
	
	// override abstract methods
	
	private static final Instrument.Group[] SUPPORTED_INSTRUMENT_GROUPS = {
		SINGLE_PLAYER_GROUP /*, CO_OP_PLAYER_GROUP*/
	};
	
	private static final Song.Sorting[] SUPPORTED_SORTINGS = {
		Song.Sorting.SCOREHERO, Song.Sorting.TITLE,
		Sorting.ARTIST, Sorting.GENRE, Sorting.DECADE
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
	public Song.Sorting[] getSupportedSortings() {
		return SUPPORTED_SORTINGS;
	}

	@Override
	public StreakStrategy getStreakStrategy() {
		return StreakStrategy.BY_SCORE;
	}
}

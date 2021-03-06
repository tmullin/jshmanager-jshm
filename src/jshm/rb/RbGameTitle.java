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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jshm.GameSeries;
import jshm.GameTitle;
import jshm.Platform;
import jshm.StreakStrategy;
import jshm.Difficulty.Strategy;
import jshm.Instrument.Group;
import jshm.Song.Sorting;
import jshm.rb.RbSong.Comparators;

public class RbGameTitle extends GameTitle {
	public static void init() {}
	
	public static final RbGameTitle
		RB1 = new RbGameTitle(1, "RB1", Platform.BIG_FOUR),
		RB2 = new RbGameTitle(2, "RB2", Platform.BIG_FOUR),
		RBN = new RbGameTitle(7, "RBN", Platform.BIG_THREE),
		RBNRB3 = new RbnRb3GameTitle(10, "RBNRB3", Platform.BIG_THREE),
		TBRB = new RbGameTitle(4, "TBRB", Platform.BIG_THREE),
		LRB = new RbGameTitle(5, "LRB", Platform.BIG_THREE),
		GDRB = new RbGameTitle(8, "GDRB", Platform.BIG_THREE),
		RB3 = new Rb3GameTitle(9, "RB3", Platform.BIG_THREE),
		RB4 = new RbnRb3GameTitle(13, "RB4", Platform.NEXT_NEXT_GEN) // RbnRb3GameTitle for supported group list.
	;
	
	public final int scoreHeroId;
	
	protected RbGameTitle(int scoreHeroId, String title, Platform ... platforms) {
		super(GameSeries.ROCKBAND, title, platforms);
		this.scoreHeroId = scoreHeroId;
	}

	/*
	 * (non-Javadoc)
	 * implemented Sortings:
	 *   DECADE, GENRE, ARTIST
	 * @see jshm.Game#initDynamicTiersInternal()
	 */
	@SuppressWarnings("unchecked")
	@Override protected void initDynamicTiersInternal() {
		// make difficulty tier
		// not necessarily dynamic but whatever
		mapTiers(Sorting.DIFFICULTY, RbGame.RbTiers.RB2_DIFFS);

		org.hibernate.Session session = jshm.hibernate.HibernateUtil.getCurrentSessionSilent();

		if (null == session) return;
		
		List<String> tiers = new ArrayList<String>();
		List<String> list_s = null;
		List<Integer> list_i = null;
		
	    session.beginTransaction();
	    
	    // make the decades tier
	    list_i = (List<Integer>) session.createQuery(
	    	"SELECT DISTINCT (year / 10) FROM RbSong WHERE year IS NOT NULL AND year <> 0") 
	    	.list();
	    
	    tiers.clear();
	    tiers.add("<UNKNOWN>");
	    
    	Collections.sort(list_i);
    
	    for (Integer i : list_i) {
	    	tiers.add(String.valueOf(i * 10) + "s");
	    }
	    
//	    System.out.println("RB Decade List");
//	    jshm.util.Print.print(tiers, "\t");
	    
	    mapTiers(Sorting.DECADE, tiers);
	    
	    
	    // make genre tier
	    list_s = (List<String>) session.createQuery(
	    	"SELECT DISTINCT genre FROM RbSong WHERE genre IS NOT NULL ORDER BY genre")
	    	.list();
	    
	    tiers.clear();
	    tiers.addAll(list_s);
	    
	    Collections.sort(tiers, String.CASE_INSENSITIVE_ORDER);
		tiers.add(0, "<UNKNOWN>");
//	    System.out.println("RB Genre List");
//	    jshm.util.Print.print(tiers, "\t");
	    
	    mapTiers(Sorting.GENRE, tiers);
	    
	    
	    // make artist tier
	    list_s = (List<String>) session.createQuery(
	    	"SELECT DISTINCT artist FROM RbSong WHERE artist IS NOT NULL ORDER BY artist")
	    	.list();
	    
	    tiers.clear();
	    tiers.addAll(list_s);
	    
	    Collections.sort(tiers, String.CASE_INSENSITIVE_ORDER);
		tiers.add(0, "<UNKNOWN>");
//	    System.out.println("RB Artists List");
//	    jshm.util.Print.print(tiers, "\t");
	    
	    mapTiers(Sorting.ARTIST, tiers);
	    
	    session.getTransaction().commit();
	}
	
	@Override protected void initSortingComparatorsInternal() {
		mapSortingComparator(Sorting.DIFFICULTY, Comparators.DIFFICULTY);
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

	private static final Group[] SUPPORTED_GROUPS = {
		Group.GUITAR, Group.BASS, Group.DRUMS, Group.VOCALS
	};
	
	@Override
	public Group[] getSupportedInstrumentGroups() {
		return SUPPORTED_GROUPS;
	}
	
	private static final Sorting[] SUPPORTED_SORTINGS = {
		Sorting.SCOREHERO, Sorting.DIFFICULTY, Sorting.TITLE,
		Sorting.ARTIST, Sorting.GENRE, Sorting.DECADE
	};
	
	@Override
	public Sorting[] getSupportedSortings() {
		return SUPPORTED_SORTINGS;
	}

	@Override
	public boolean supportsCalculatedRating() {
		return false;
	}
	
	/**
	 * Accomodates the only real difference with RB3 that we have to
	 * care about, additional instruments.
	 * @author Tim Mullin
	 *
	 */
	public static class Rb3GameTitle extends RbGameTitle {
		protected Rb3GameTitle(int scoreHeroId, String title,
				Platform ... platforms) {
			super(scoreHeroId, title, platforms);
		}
		
		private static final Group[] SUPPORTED_GROUPS = {
			Group.GUITAR, Group.BASS, Group.DRUMS, Group.VOCALS, Group.KEYS,
			Group.PROGUITAR, Group.PROBASS, Group.PRODRUMS, Group.PROKEYS
		};
		
		@Override
		public Group[] getSupportedInstrumentGroups() {
			return SUPPORTED_GROUPS;
		}
	}
	
	public static class RbnRb3GameTitle extends RbGameTitle {
		protected RbnRb3GameTitle(int scoreHeroId, String title,
				Platform ... platforms) {
			super(scoreHeroId, title, platforms);
		}
		
		private static final Group[] SUPPORTED_GROUPS = {
			Group.GUITAR, Group.BASS, Group.DRUMS, Group.VOCALS,
			Group.PRODRUMS
		};
		
		@Override
		public Group[] getSupportedInstrumentGroups() {
			return SUPPORTED_GROUPS;
		}
	}
}

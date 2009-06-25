package jshm.wt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jshm.*;
import jshm.Instrument.Group;
import jshm.Song.Sorting;

public class WtGameTitle extends GameTitle {
	public static void init() {}
	
	public static final WtGameTitle
		GHWT = new WtGameTitle("GHWT", 7, false, Platform.BIG_FOUR),
		GHM = new WtGameTitle("GHM", 9, true, Platform.BIG_FOUR),
		GHSH = new WtGameTitle("GHSH", 11, true, Platform.BIG_FOUR)
	;

	public final int scoreHeroGroupId;
	public final boolean supportsExpertPlus;
	
	protected WtGameTitle(String title, int scoreHeroGroupId, boolean supportsExpertPlus, Platform ... platforms) {
		super(GameSeries.WORLD_TOUR, title, platforms);
		this.scoreHeroGroupId = scoreHeroGroupId;
		this.supportsExpertPlus = supportsExpertPlus;
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
	    	"SELECT DISTINCT (year / 10) FROM WtSong WHERE gameTitle=:game AND year IS NOT NULL AND year <> 0")
	    	.setString("game", this.title)
	    	.list();
	    
	    tiers.clear();
	    tiers.add("<UNKNOWN>");
	    
    	Collections.sort(list_i);
    
	    for (Integer i : list_i) {
	    	tiers.add(String.valueOf(i * 10) + "s");
	    }
	    
//	    System.out.println("WT Decade List");
//	    jshm.util.Print.print(tiers, "\t");
	    
	    mapTiers(Sorting.DECADE, tiers);
	    
	    
	    // make genre tier
	    list_s = (List<String>) session.createQuery(
	    	"SELECT DISTINCT genre FROM WtSong WHERE gameTitle=:game AND genre IS NOT NULL ORDER BY genre")
	    	.setString("game", this.title)
	    	.list();
	    
	    tiers.clear();
	    tiers.addAll(list_s);
	    
	    Collections.sort(tiers, String.CASE_INSENSITIVE_ORDER);
		tiers.add(0, "<UNKNOWN>");
//	    System.out.println("WT Genre List");
//	    jshm.util.Print.print(tiers, "\t");
	    
	    mapTiers(Sorting.GENRE, tiers);
	    
	    
	    // make artist tier
	    list_s = (List<String>) session.createQuery(
	    	"SELECT DISTINCT artist FROM WtSong WHERE gameTitle=:game AND artist IS NOT NULL ORDER BY artist")
	    	.setString("game", this.title)
	    	.list();
	    
	    tiers.clear();
	    tiers.addAll(list_s);
	    
	    Collections.sort(tiers, String.CASE_INSENSITIVE_ORDER);
		tiers.add(0, "<UNKNOWN>");
//	    System.out.println("WT Artists List");
//	    jshm.util.Print.print(tiers, "\t");
	    
	    mapTiers(Sorting.ARTIST, tiers);
	    
	    session.getTransaction().commit();
	}
	
	@Override
	public Difficulty.Strategy getDifficultyStrategy() {
		return Difficulty.Strategy.BY_SCORE;
	}

	@Override
	public int getMaxRating() {
		return 5;
	}

	@Override
	public int getMinRating() {
		return 3;
	}

	@Override
	public StreakStrategy getStreakStrategy() {
		return StreakStrategy.BY_PART;
	}

	private static final Group[] SUPPORTED_GROUPS = {
		Group.GUITAR, Group.BASS, Group.WTDRUMS, Group.DRUMS, Group.VOCALS
	};
	
	@Override
	public Group[] getSupportedInstrumentGroups() {
		return SUPPORTED_GROUPS;
	}

	private static final Sorting[] SUPPORTED_SORTINGS = {
		Song.Sorting.SCOREHERO, Song.Sorting.TITLE,
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
}

package jshm.sh.gh;

/**
 * This contains ScoreHero's ids for the different
 * song stat pages.
 * @author Tim
 *
 */
public enum SongStat {
	FOUR_STAR_CUTOFF(1),
	FIVE_STAR_CUTOFF(2),
	TOTAL_NOTES(3),
	ALL_CUTOFFS(4);
	
	public final int id;
	
	private SongStat(final int id) {
		this.id = id;
	}
}

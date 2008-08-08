package jshm.sh;

/**
 * This contains ScoreHero's ids for the different
 * song stat pages.
 * @author Tim
 *
 */
public enum GhSongStat {
	FOUR_STAR_CUTOFF(1),
	FIVE_STAR_CUTOFF(2),
	TOTAL_NOTES(3),
	ALL_CUTOFFS(4);
	
	public final int id;
	
	private GhSongStat(final int id) {
		this.id = id;
	}
}

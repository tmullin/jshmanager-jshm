package jshm.sh.gh;

import java.util.*;


/**
 * This class represents one Guitar Hero score entry as far as
 * it is represented on ScoreHero.
 * @author Tim
 *
 */
public class Score {
	public GhSong song = null;
	public int score = 0;
	public int observedRating = 0;
	public float calculatedRating = 0.0f;
	public int noteHitPercent = 0;
	public int noteStreak = 0;
	public Date submissionDate = null;
	public String comment = null;
}

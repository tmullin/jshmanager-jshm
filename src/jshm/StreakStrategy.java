package jshm;

/**
 * This represents whether note streaks are managed on a
 * by score or by part basis.
 * 
 * <p>
 * GH is BY_SCORE in that if one player loses combo,
 * the streak is reset. RB is BY_PART in that each Part is
 * separately tracked.
 * </p>
 * @author Tim Mullin
 *
 */
public enum StreakStrategy {
	BY_SCORE, BY_PART
}
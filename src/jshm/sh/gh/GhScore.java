package jshm.sh.gh;

import javax.persistence.Transient;


/**
 * This class represents one Guitar Hero score entry as far as
 * it is represented on ScoreHero.
 * @author Tim Mullin
 *
 */
public class GhScore extends jshm.Score {
	private float calculatedRating	= 0.0f;

	public void setCalculatedRating(float calculatedRating) {
		if (calculatedRating < getGame().getMinCalculableStars() ||
			getGame().getMaxCalculableStars() < calculatedRating)
			throw new IllegalArgumentException("calculatedRating must be between " + getGame().getMinCalculableStars() + " and " + getGame().getMaxCalculableStars());
		
		this.calculatedRating = calculatedRating;
	}

	public float getCalculatedRating() {
		return calculatedRating;
	}
	
	@Override
	@Transient
	public StreakStrategy getStreakStrategy() {
		return StreakStrategy.BY_SCORE;
	}
}

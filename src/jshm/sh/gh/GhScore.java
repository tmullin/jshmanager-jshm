package jshm.sh.gh;

/**
 * This class represents one Guitar Hero score entry as far as
 * it is represented on ScoreHero.
 * @author Tim Mullin
 *
 */
public class GhScore extends jshm.Score {
	private float calculatedRating	= 0.0f;

	public void setCalculatedRating(float calculatedRating) {
		if (calculatedRating < getGame().title.getMinCalculatedRating() ||
			getGame().title.getMaxCalculatedRating() < calculatedRating)
			throw new IllegalArgumentException("calculatedRating must be between " + getGame().title.getMinCalculatedRating() + " and " + getGame().title.getMaxCalculatedRating());
		
		this.calculatedRating = calculatedRating;
	}

	public float getCalculatedRating() {
		return calculatedRating;
	}
}

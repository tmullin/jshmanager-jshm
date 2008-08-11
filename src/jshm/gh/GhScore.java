package jshm.gh;

import javax.persistence.Entity;

import jshm.Part;

import org.hibernate.validator.*;

/**
 * This class represents one Guitar Hero score entry as far as
 * it is represented on ScoreHero.
 * @author Tim Mullin
 *
 */
@Entity
public class GhScore extends jshm.Score {
	private int   streak			= 0; 
	private float calculatedRating	= 0.0f;
	
	@Min(0)
	@Override
	public int getStreak() {
		return this.streak;
	}
	
	@Override
	public void setStreak(final int streak) {
		if (streak < 0)
			throw new IllegalArgumentException("streak must be >= 0");
		this.streak = streak;
	}
	
	@Override
	public void setCalculatedRating(float calculatedRating) {
		if (0.0f != calculatedRating &&
			(calculatedRating < getGameTitle().getMinCalculatedRating() ||
			 getGameTitle().getMaxCalculatedRating() < calculatedRating))
			throw new IllegalArgumentException("calculatedRating must be 0 or between " + getGameTitle().getMinCalculatedRating() + " and " + getGameTitle().getMaxCalculatedRating());
		
		this.calculatedRating = calculatedRating;
	}

	@Override
	public float getCalculatedRating() {
		return calculatedRating;
	}
	
	
	// override object methods
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getGame());
		sb.append(',');
		sb.append(getSong().getTitle());
		sb.append(',');
		sb.append(getScore());
		sb.append(",r=");
		sb.append(getRating());
		sb.append(",cr=");
		sb.append(getCalculatedRating());
		sb.append(",%=");
		sb.append(getHitPercent());
		sb.append(",ns=");
		sb.append(getStreak());
		
		for (Part p : getParts()) {
			sb.append(',');
			sb.append('{');
			sb.append(p);
			sb.append('}');
		}
		
		return sb.toString();
	}
}

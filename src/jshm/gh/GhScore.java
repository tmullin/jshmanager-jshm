package jshm.gh;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Transient;
import javax.swing.ImageIcon;

import jshm.Difficulty;
import jshm.Instrument;
import jshm.Part;
import jshm.Score;

import org.hibernate.validator.*;

/**
 * This class represents one Guitar Hero score entry as far as
 * it is represented on ScoreHero.
 * @author Tim Mullin
 *
 */
@Entity
public class GhScore extends jshm.Score {
	public static GhScore createNewScoreTemplate(final GhSong song) {
		GhScore s = new GhScore();
		s.setStatus(Score.Status.TEMPLATE);
		s.setGame(song.getGame());
		s.setSong(song);
		Part p = new Part();
		p.setDifficulty(song.getDifficulty());
		p.setInstrument(Instrument.GUITAR);
		s.addPart(p);
		return s;
	}
	
	@SuppressWarnings("unchecked")
	public static List<GhScore> getScores(final GhGame game, final Difficulty difficulty) {
		org.hibernate.Session session = jshm.hibernate.HibernateUtil.getCurrentSession();
	    session.beginTransaction();
	    List<GhScore> result =
			(List<GhScore>)
			session.createQuery(
				String.format(
					"from GhScore where game='%s' and difficulty='%s'",
					game.toString(), difficulty.toString()))
				.list();
	    session.getTransaction().commit();
		
		return result;
	}
	
	
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
		
		if (getParts().size() == 1) {
			getPart(1).setStreak(streak);
		}
	}
	
	/**
	 * Set the score as well as the calculated rating automatically.
	 */
	@Override
	public void setScore(int score) {
		super.setScore(score);
		
		if (null != getSong())
			setCalculatedRating(
				((GhSong) getSong()).getCalculatedRating(score));
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

	@Transient
	public boolean isFullCombo() {
		return this.streak > 0 && this.streak == ((GhSong) getSong()).getNoteCount();
	}
	
	@Transient
	@Override
	public ImageIcon getRatingIcon() {
		return this.getRatingIcon(false);
	}
	
	@Transient
	public ImageIcon getRatingIcon(boolean ignoreCalculatedRating) {		
		int rating = ignoreCalculatedRating
		? getRating()
		: Math.max(getRating(), (int) Math.floor(getCalculatedRating()));
		
		return getRatingIcon(rating);
	}
	
	
	private static final ImageIcon[] RATING_ICONS = new ImageIcon[7];
	
	public static ImageIcon getRatingIcon(int rating) {
//		if (rating != 0 && (rating < 3 || 8 < rating))
//			throw new IllegalArgumentException("rating must be between 3 and 8 inclusive or 0");
		
		if (rating != 0 && rating < 3)
			rating = 3;
		else if (rating > 8)
			rating = 8;
		
		int index = rating;
		
		if (rating != 0)
			index = rating - 2;
		
		if (null == RATING_ICONS[index]) {
			RATING_ICONS[index] = new javax.swing.ImageIcon(
				GhScore.class.getResource(
					"/jshm/resources/images/ratings/gh/" + rating + ".gif"));
		}
		
		return RATING_ICONS[index];
	}
	
	// override object methods
	

}

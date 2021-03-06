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
package jshm.gh;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Transient;
import javax.swing.ImageIcon;

import jshm.Difficulty;
import jshm.Instrument;
import jshm.Part;
import jshm.Score;

import org.hibernate.LazyInitializationException;
import org.hibernate.validator.*;

/**
 * This class represents one Guitar Hero score entry as far as
 * it is represented on ScoreHero.
 * @author Tim Mullin
 *
 */
@Entity
public class GhScore extends jshm.Score {	
	public static GhScore createNewScoreTemplate(final GhGame game, final Instrument.Group group, final Difficulty difficulty, final GhSong song) {
		GhScore s = new GhScore();
		s.setStatus(Score.Status.TEMPLATE);
		s.setGroup(group);
		Part p = new Part();
		p.setInstrument(Instrument.GUITAR);
		s.addPart(p);
		s.setSong(song);
		return s;
	}
	
	@SuppressWarnings("unchecked")
	public static List<Score> getScores(final GhGame game, final Difficulty difficulty) {
		org.hibernate.Session session = jshm.hibernate.HibernateUtil.getCurrentSession();
	    session.beginTransaction();
	    List<Score> result =
			(List<Score>)
			session.createQuery(
				"FROM GhScore WHERE game=:game AND difficulty=:diff ORDER BY score DESC")
				.setString("game", game.toString())
				.setString("diff", difficulty.toString())
				.list();
	    session.getTransaction().commit();
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static List<Score> getSubmittableScores(final GhGame game, final Difficulty difficulty) {
		org.hibernate.Session session = jshm.hibernate.HibernateUtil.getCurrentSession();
	    session.beginTransaction();
	    List<Score> result =
			(List<Score>)
			session.createQuery(
				"FROM GhScore WHERE status IN ('NEW', 'UNKNOWN') AND score > 0 AND game=:game AND difficulty=:diff ORDER BY score DESC")
				.setString("game", game.toString())
				.setString("diff", difficulty.toString())
				.list();
	    session.getTransaction().commit();
		
	    java.util.Iterator<Score> it = result.iterator();
	    
	    while (it.hasNext()) {
	    	if (!it.next().isSubmittable())
	    		it.remove();
	    }
	    
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
		int old = this.streak;
		this.streak = streak;
		pcs.firePropertyChange("streak", old, streak);
		
		// This can throw LazyInitializationException but
		// it seems this isn't anything to worry about,
		// the exception would only be thrown when reading
		// the value from the db initially, in which case it's also
		// going to set part 1's streak anyway
		try {
			if (getParts().size() == 1) {
				// super since we override it ourself and it would recurse
				super.setPartStreak(1, streak);
			}
		} catch (LazyInitializationException e) {}
	}
	
	@Override
	public void setPartStreak(int index, int streak) {
		super.setPartStreak(index, streak);
		
		if (getParts().size() == 1)
			setStreak(streak);
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
		
		float old = this.calculatedRating;
		this.calculatedRating = calculatedRating;
		pcs.firePropertyChange("calculatedRating", old, calculatedRating);
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
		: Math.max(getRating(), (int) getCalculatedRating());
		
		return getRatingIcon(rating);
	}
	
	public static ImageIcon getRatingIcon(int rating) {
//		if (rating != 0 && (rating < 3 || 8 < rating))
//			throw new IllegalArgumentException("rating must be between 3 and 8 inclusive or 0");
		
		if (rating != 0 && rating < 3)
			rating = 3;
		else if (rating > 8)
			rating = 8;
		
		return jshm.gui.GuiUtil.getIcon(
			"ratings/gh/" + rating + ".gif");
	}
	
	
	public void submit() throws Exception {
		if (!isSubmittable())
			throw new IllegalArgumentException("This score is not submittable");
		
		jshm.sh.Api.submitGhScore(this);
	}
	
	
	// override object methods
	

}

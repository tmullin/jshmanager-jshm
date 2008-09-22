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
import java.util.logging.Logger;

import javax.persistence.*;

import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.validator.*;

import jshm.Difficulty;


/**
 * This class represents a Guitar Hero song as far as
 * it is represented on ScoreHero.
 * @author Tim Mullin
 *
 */
@Entity
public class GhSong extends jshm.Song {
	static final Logger LOG = Logger.getLogger(GhSong.class.getName());
	
	@SuppressWarnings("unchecked")
	public static List<GhSong> getSongs(final GhGame game, final Difficulty difficulty) {
		LOG.finer("Querying database for all songs for " + game + " on " + difficulty);
		
		org.hibernate.Session session = jshm.hibernate.HibernateUtil.getCurrentSession();
	    session.beginTransaction();
	    List<GhSong> result =
			(List<GhSong>)
			session.createQuery(
				String.format(
					"from GhSong where game='%s' and difficulty='%s'",
					game.toString(), difficulty.toString()))
				.list();
	    session.getTransaction().commit();
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static List<GhSong> getSongsOrderedByTitles(final GhGame game, final Difficulty difficulty) {
		org.hibernate.Session session = jshm.hibernate.HibernateUtil.getCurrentSession();
	    session.beginTransaction();
	    List<GhSong> result =
			(List<GhSong>)
			session.createQuery(
				String.format(
					"from GhSong where game='%s' and difficulty='%s' order by title ASC",
					game.toString(), difficulty.toString()))
				.list();
	    session.getTransaction().commit();
	    
	    return result;
	}
	
	public static GhSong getByScoreHeroId(final int id) {
		LOG.finer("Querying database for ghsong with scoreHeroId=" + id);
		
		org.hibernate.Session session = jshm.hibernate.HibernateUtil.getCurrentSession();
	    session.beginTransaction();
	    GhSong result =
			(GhSong)
			session.createQuery(
				String.format(
					"from GhSong where scoreHeroId=%d", id))
				.uniqueResult();
	    session.getTransaction().commit();
		
		return result;
	}
	
	public static GhSong getByTitle(GhGame game, String title, Difficulty diff) {
		List<GhSong> list = getAllByTitle(game, title, diff);
		return list.size() == 1 ? list.get(0) : null;
	}
	
	@SuppressWarnings("unchecked")
	public static List<GhSong> getAllByTitle(GhGame game, String title, Difficulty diff) {
		LOG.finer("Querying database for ghsong for " + game + " with title=" + title + " on " + diff);
		
		org.hibernate.Session session = jshm.hibernate.HibernateUtil.getCurrentSession();
	    session.beginTransaction();
	    List<GhSong> result =
			(List<GhSong>)
			session.createCriteria(GhSong.class)
				.add(Restrictions.eq("game", game))
				.add(Restrictions.ilike("title", title, MatchMode.ANYWHERE))
				.add(Restrictions.eq("difficulty", diff))
				.addOrder(Order.asc("title"))
			.list();
	    session.getTransaction().commit();
		
		return result;
	}
	
	private Difficulty	difficulty	= null;
	
	private int 	tierLevel		= 0;
	private int 	noteCount 		= -1;
	private int 	baseScore 		= -1;
	private int 	fourStarCutoff 	= -1;
	private int 	fiveStarCutoff 	= -1;
	private int 	sixStarCutoff 	= -1;
	private int 	sevenStarCutoff = -1;
	private int 	eightStarCutoff = -1;
	private int 	nineStarCutoff 	= -1;
	
	/**
	 * Sets the noteCount, baseScore and cutoffs of
	 * this song to that of <code>source</code>. 
	 * @param source
	 * @return Whether any values were changed
	 */
	public boolean update(final GhSong source) {
		boolean test = false;
		
		if (this.noteCount != source.noteCount) {
			test = true;
			this.noteCount = source.noteCount;
		}
		
		return test || this.setScoreAndCutoffs(source);
	}
	
	/**
	 * Sets the baseScore and cutoffs of this song
	 * to that of <code>source</code>.
	 * @param source
	 * @return Whether any values were changed
	 */
	public boolean setScoreAndCutoffs(final GhSong source) {
		boolean test = false;
		
		if (this.baseScore != source.baseScore) {
			test = true;
			this.baseScore = source.baseScore;
		}
		
		if (this.fourStarCutoff != source.fourStarCutoff) {
			test = true;
			this.fourStarCutoff = source.fourStarCutoff;
		}
		
		if (this.fiveStarCutoff != source.fiveStarCutoff) {
			test = true;
			this.fiveStarCutoff = source.fiveStarCutoff;
		}
		
		if (this.sixStarCutoff != source.sixStarCutoff) {
			test = true;
			this.sixStarCutoff = source.sixStarCutoff;
		}
		
		if (this.sevenStarCutoff != source.sevenStarCutoff) {
			test = true;
			this.sevenStarCutoff = source.sevenStarCutoff;
		}
		
		if (this.eightStarCutoff != source.eightStarCutoff) {
			test = true;
			this.eightStarCutoff = source.eightStarCutoff;
		}
		
		if (this.nineStarCutoff != source.nineStarCutoff) {
			test = true;
			this.nineStarCutoff = source.nineStarCutoff;
		}
		
		return test;
	}
	
	@Transient
	public boolean canCalculateRating() {
		return
			fourStarCutoff != -1 &&
			fiveStarCutoff != -1 &&
			sixStarCutoff != -1 &&
			sevenStarCutoff != -1 &&
			eightStarCutoff != -1 &&
			nineStarCutoff != -1;
	}
	
	@Transient
	public float getCalculatedRating(int score) {
		LOG.finer("Calculating rating for \"" + this.getTitle() + "\", score=" + score);
		
		if (!canCalculateRating()) {
			LOG.finer("Can't calculate rating, returning 0");
			return 0f;
		}
		
		if (score >= nineStarCutoff)
			return 9.0f;
		
		int majorRating = 0, highCutoff = 0, lowCutoff = 0;
		
		if (score >= eightStarCutoff) {
			majorRating = 8;
			highCutoff = nineStarCutoff;
			lowCutoff = eightStarCutoff;
		} else if (score >= sevenStarCutoff) {
			majorRating = 7;
			highCutoff = eightStarCutoff;
			lowCutoff = sevenStarCutoff;
		} else if (score >= sixStarCutoff){
			majorRating = 6;
			highCutoff = sevenStarCutoff;
			lowCutoff = sixStarCutoff;
		} else if (score >= fiveStarCutoff) {
			majorRating = 5;
			highCutoff = sixStarCutoff;
			lowCutoff = fiveStarCutoff;
		} else if (score >= fourStarCutoff) {
			majorRating = 4;
			highCutoff = fiveStarCutoff;
			lowCutoff = fourStarCutoff;
		} else {
			return 3f;
		}
		
		int scoreDiff = score - lowCutoff;
		int cutoffDiff = highCutoff - lowCutoff;
		
		float ret = majorRating + ((float) scoreDiff / (float) cutoffDiff);
		
		LOG.finer("Returning " + ret);
		return ret;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == this) return true;
		if (!(o instanceof GhSong)) return false;
		
		GhSong s = (GhSong) o;
		
		return (this.getId() == s.getId() ||
				(this.getTitle().equals(s.getTitle()) &&
				 this.getGame() == s.getGame() &&
				 this.getDifficulty() == s.getDifficulty()));
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getGame());
		sb.append(',');
		sb.append(getDifficulty().toShortString());
		sb.append(',');
		sb.append(getScoreHeroId());
		sb.append(',');
		sb.append(getTitle());
		sb.append(",t=");
		sb.append(getTierLevel());
		sb.append(",n=");
		sb.append(getNoteCount());
		sb.append(',');
		sb.append(getBaseScore());
		sb.append(',');
		sb.append(getFourStarCutoff());
		sb.append(',');
		sb.append(getFiveStarCutoff());
		
		return sb.toString();
	}

	
	// getters/setters
	
	@NotNull
	@Enumerated(EnumType.STRING)
	public Difficulty getDifficulty() {
		return difficulty;
	}

	public void setDifficulty(Difficulty difficulty) {
		this.difficulty = difficulty;
	}

	/**
	 * 
	 * @return The tier number of this song or 0 if it is unknown.
	 */
	@Min(0)
	public int getTierLevel() {
		return tierLevel;
	}

	public void setTierLevel(int tierLevel) {
		if (tierLevel < 0)
			throw new IllegalArgumentException("tierLevel must be >= 0");
		this.tierLevel = tierLevel;
	}
	
	/**
	 * 
	 * @return The name of this song's tier based on this song's tier level and game.
	 */
	@Transient
	public String getTierName() {
		return ((GhGame) getGame()).getTierName(this.getTierLevel());
	}

	/**
	 * 
	 * @return The number of notes in the song or -1 if it is unknown.
	 */
	@Min(-1)
	public int getNoteCount() {
		return noteCount;
	}

	public void setNoteCount(int noteCount) {
		if (noteCount < -1)
			throw new IllegalArgumentException("noteCount must be >= -1");
		this.noteCount = noteCount;
	}

	/**
	 * 
	 * @return The base score of the song if you didn't use star power or -1 if it is unknown.
	 */
	@Min(-1)
	public int getBaseScore() {
		return baseScore;
	}

	public void setBaseScore(int baseScore) {
		if (baseScore < -1)
			throw new IllegalArgumentException("baseScore must be >= -1");
		this.baseScore = baseScore;
	}

	/**
	 * 
	 * @return The minimum score required to achieve a 4-star rating or -1 if it is unknown.
	 */
	@Min(-1)
	public int getFourStarCutoff() {
		return fourStarCutoff;
	}

	public void setFourStarCutoff(int fourStarCutoff) {
		if (fourStarCutoff < -1)
			throw new IllegalArgumentException("fourStarCutoff must be >= -1");
		this.fourStarCutoff = fourStarCutoff;
	}

	/**
	 * 
	 * @return The minimum score required to achieve a 5-star rating or -1 if it is unknown.
	 */
	@Min(-1)
	public int getFiveStarCutoff() {
		return fiveStarCutoff;
	}

	public void setFiveStarCutoff(int fiveStarCutoff) {
		if (fiveStarCutoff < -1)
			throw new IllegalArgumentException("fiveStarCutoff must be >= -1");
		this.fiveStarCutoff = fiveStarCutoff;
	}

	/**
	 * 
	 * @return The minimum score required to achieve a calculated 6-star rating or -1 if it is unknown.
	 */
	@Min(-1)
	public int getSixStarCutoff() {
		return sixStarCutoff;
	}

	public void setSixStarCutoff(int sixStarCutoff) {
		if (sixStarCutoff < -1)
			throw new IllegalArgumentException("sixStarCutoff must be >= -1");
		this.sixStarCutoff = sixStarCutoff;
	}

	/**
	 * 
	 * @return The minimum score required to achieve a calculated 7-star rating or -1 if it is unknown.
	 */
	@Min(-1)
	public int getSevenStarCutoff() {
		return sevenStarCutoff;
	}

	public void setSevenStarCutoff(int sevenStarCutoff) {
		if (sevenStarCutoff < -1)
			throw new IllegalArgumentException("sevenStarCutoff must be >= -1");
		this.sevenStarCutoff = sevenStarCutoff;
	}

	/**
	 * 
	 * @return The minimum score required to achieve a calculated 8-star rating or -1 if it is unknown.
	 */
	@Min(-1)
	public int getEightStarCutoff() {
		return eightStarCutoff;
	}

	public void setEightStarCutoff(int eightStarCutoff) {
		if (eightStarCutoff < -1)
			throw new IllegalArgumentException("eightStarCutoff must be >= -1");
		this.eightStarCutoff = eightStarCutoff;
	}

	/**
	 * 
	 * @return The minimum score required to achieve a calculated 9-star rating or -1 if it is unknown.
	 */
	@Min(-1)
	public int getNineStarCutoff() {
		return nineStarCutoff;
	}

	public void setNineStarCutoff(int nineStarCutoff) {
		if (nineStarCutoff < -1)
			throw new IllegalArgumentException("nineStarCutoff must be >= -1");
		this.nineStarCutoff = nineStarCutoff;
	}
}

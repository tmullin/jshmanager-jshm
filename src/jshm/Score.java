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
package jshm;

import java.awt.Color;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.text.DateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;
import org.hibernate.annotations.Type;
import org.hibernate.validator.Min;
import org.hibernate.validator.NotNull;

@Entity
@Inheritance(strategy=InheritanceType.JOINED)
public abstract class Score {
	/**
	 * <ul>
	 *   <li>NEW - A newly entered score that hasn't been submitted to ScoreHero
	 *   <li>SUBMITTED - A score that has been submitted to ScoreHero
	 *   <li>DELETED - A score that has been submitted but was deleted on ScoreHero due to a new score overwriting it.
	 *   <li>TEMPLATE - A score that exists as a filler row in the GUI for adding a new score
	 *   <li>UNKNOWN - A score that may have been submitted but it is unknown if the score is actually on ScoreHero
	 * </ul>
	 * @author Tim Mullin
	 *
	 */
	public static enum Status {
		NEW(new Color(0x9fe4f1)),
		SUBMITTED,
		DELETED,
		TEMPLATE(new Color(0x9fe4f1)),
		UNKNOWN(new Color(0xF19F9F));
		
		public final Color highlightColor;
		
		private Status() {
			this(null);
		}
		
		private Status(Color highlightColor) {
			this.highlightColor = highlightColor;
		}
	}
	
	
	/**
	 * The id internal to JSHManager's database
	 */
	private int		id					= 0;
	
	/**
	 * The id in ScoreHero's database if this score has been submitted.
	 */
	private int		scoreHeroId			= 0;
	private Status	status				= Status.NEW;

	private Game	game				= null;
	private Song	song				= null;
	
	/**
	 * This will represent the highest difficulty of the parts.
	 */
	private Difficulty difficulty		= null;
	
	private int		score				= 0;
	private int		rating				= 0;
	private Date	creationDate		= new Date();
	private Date	submissionDate		= null;
	private String	comment				= "";
	
	private String	imageUrl			= "";
	private String	videoUrl			= "";
	
	private Instrument.Group group      = Instrument.Group.GUITAR;
	private SortedSet<Part> parts		= new TreeSet<Part>();
	
	@Id
	@GeneratedValue(generator="score-id")
	@GenericGenerator(name="score-id", strategy = "native")
	public int getId() {
		return id;
	}

	@SuppressWarnings("unused")
	private void setId(int id) {
		this.id = id;
	}

	public int getScoreHeroId() {
		return scoreHeroId;
	}

	public void setScoreHeroId(int scoreHeroId) {
		this.scoreHeroId = scoreHeroId;
	}
	
	@NotNull
	@Enumerated(EnumType.STRING)
	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		if (null == status)
			throw new IllegalArgumentException("status cannot be null");
		this.status = status;
	}
	
	/**
	 * A score can only be modified if it has not been
	 * submitted to ScoreHero.
	 * @return
	 */
	@Transient
	public boolean isEditable() {
		switch (status) {
			case NEW:
			case TEMPLATE:
			case UNKNOWN:
				return true;
		}
		
		return false;
	}
	
	public void setGame(Game game) {
		this.game = game;
	}
	
	@NotNull
	@Type(type="jshm.hibernate.GameUserType")
	public Game getGame() {
		return this.game;
	}
	
	public void setSong(Song song) {
		this.song = song;
		
		if (null != song) {
			if (null != song.getGame())
				this.game = song.getGame();
			
			if (Difficulty.Strategy.BY_SONG == getGameTitle().getDifficultyStrategy()) {
				setDifficulty(song.getDifficulty());
			}
		}
	}

	@ManyToOne(optional=true)
	public Song getSong() {
		return song;
	}
	
	@Transient
	public GameTitle getGameTitle() {
		return null != song ? song.getGameTitle() : null;
	}
	
	@NotNull
	@Enumerated(EnumType.STRING)
	public Difficulty getDifficulty() {
		return difficulty;
	}
	
	/**
	 * Sets the difficulty for this score as well as the difficulty
	 * for the first part if there is exactly 1 part.
	 * @param difficulty
	 */
	public void setDifficulty(Difficulty difficulty) {
		this.difficulty = difficulty;
		
		if (parts.size() == 1) {
			getPart(1).setDifficulty(difficulty);
		}
	}

	@Min(0)
	public int getScore() {
		return score;
	}
	
	public void setScore(int score) {
		if (score < 0)
			throw new IllegalArgumentException("score must be >= 0");
		int old = this.score;
		this.score = score;
		pcs.firePropertyChange("score", old, score);
	}

	@Column(name="instrumentgroup") // "group" is a reserved sql word
	@Enumerated(EnumType.STRING)
	public Instrument.Group getGroup() {
		return group;
	}
	
	public void setGroup(Instrument.Group group) {
		if (null == group) group = Instrument.Group.GUITAR;
		this.group = group;
	}
	
	@OneToMany(fetch=FetchType.EAGER, cascade=CascadeType.ALL)
	@LazyCollection(LazyCollectionOption.FALSE)
	@Fetch(FetchMode.SELECT)
	@JoinColumn(name="score_id", nullable=false)
	@Sort(type=SortType.NATURAL)
	public SortedSet<Part> getParts() {
		return parts;
	}

	public void setParts(SortedSet<Part> parts) {
		this.parts = parts;
	}
	
	public void addPart(Part part) {
		this.parts.add(part);
	}
	
	public void setPartStreak(int index, int streak) {
		Part p = getPart(index);
		
		int old = p.getStreak();
		p.setStreak(streak);
		pcs.firePropertyChange("part" + index + "Streak", old, streak);
	}
	
	@Transient
	public int getPartStreak(int index) {
		return getPart(index).getStreak();
	}
	
	public void setPartHitPercent(int index, float percent) {
		Part p = getPart(index);
		
		float old = p.getHitPercent();
		p.setHitPercent(percent);
		pcs.firePropertyChange("part" + index + "HitPercent", old, percent);
	}
	
	@Transient
	public float getPartHitPercent(int index) {
		return getPart(index).getHitPercent();
	}
	
	@Transient
	public Instrument getPartInstrument(int index) {
		return getPart(index).getInstrument();
	}
	
	@Transient
	public String getPartPerformer(int index) {
		return getPart(index).getPerformer();
	}
	
	/**
	 * 
	 * @param index The <b>1-based</b> index of the Part to retrieve
	 * @return The Part at the specified index
	 */
	@Transient
	public Part getPart(int index) {		
		if (index < 1 || parts.size() < index)
			throw new ArrayIndexOutOfBoundsException("index must be between 1 and " + parts.size() + ", got: " + index);
		
		Part ret = null;
		Iterator<Part> it = parts.iterator();
		
		for (int i = 0; i < index; i++) {
			ret = it.next();
		}
		
		return ret;
	}
	
	@Transient
	public Part getPart(Instrument instrument) {
		Iterator<Part> it = getPartsIterator();
		
		while (it.hasNext()) {
			Part p = it.next();
			if (p.getInstrument() == instrument) return p;
		}
		
		return null;
	}
	
	@Transient
	public Iterator<Part> getPartsIterator() {
		return parts.iterator();
	}
	
	/**
	 * This method must be overridden if the current
	 * {@link StreakStrategy} is BY_SCORE.
	 * @param streak
	 */
	public void setStreak(int streak) {
		throw new UnsupportedOperationException("setStreak() is not supported unless StreakStrategy == BY_SCORE");
	}
	
	/**
	 * If streakStrategy is BY_PART, the highest streak of the different parts
	 * is returned. Otherwise this method must be overridden.
	 * @return
	 */
	@Transient
	public int getStreak() {
		switch (getGameTitle().getStreakStrategy()) {
			case BY_PART:
				return getMaxStreak();
		}
		
		throw new UnsupportedOperationException("getStreak() may need to be overridden");
	}
	
	/**
	 * This returns false unless overridden.
	 * @return Whether this score is a full combo.
	 */
	@Transient
	public boolean isFullCombo() {
		return false;
	}
	
	
	/**
	 * 
	 * @return The highest streak among all of the Parts.
	 */
	@Transient
	public int getMaxStreak() {
		int highest = 0;
		
		for (Part p : parts)
			if (p.getStreak() > highest)
				highest = p.getStreak();
		
		return highest;
	}
	
	/**
	 * 
	 * @return The average hit percentage of all parts. 
	 * In the case of one part, this is of course the exact
	 * total percentage.
	 */
	@Transient
	public float getHitPercent() {
		float avg = 0.0f;
		
		for (Part p : parts)
			avg += p.getHitPercent();
		
		avg /= (float) parts.size();
		
		return avg;
	}

	public void setRating(int rating) {
//		if (rating != 0 &&
//			(rating < getGame().title.getMinRating() ||
//			 getGame().title.getMaxRating() < rating))
//			throw new IllegalArgumetException("rating must be 0 or between " + getGame().title.getMinRating() + " and " + getGame().title.getMaxRating());
		int old = this.rating;
		this.rating = rating;
		pcs.firePropertyChange("rating", old, rating);
	}

	/**
	 * 
	 * @return The rating as conveyed in-game.
	 */
	public int getRating() {
		return rating;
	}
	
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	/**
	 * 
	 * @return The {@link Date} that this score was created if it was created
	 * within JSHM (as opposed to scraped from ScoreHero)
	 */
	@Temporal(TemporalType.TIMESTAMP)
	public Date getCreationDate() {
		return creationDate;
	}
	
	public void setSubmissionDate(Date submissionDate) {
		this.submissionDate = submissionDate;
	}

	/**
	 * 
	 * @return The {@link Date} that this score was submitted to ScoreHero
	 */
	@Temporal(TemporalType.TIMESTAMP)
	public Date getSubmissionDate() {
		return submissionDate;
	}

	public void setComment(String comment) {
		if (null == comment)
			throw new IllegalArgumentException("comment cannot be null");
		String old = this.comment;
		this.comment = comment;
		pcs.firePropertyChange("comment", old, comment);
	}

	@NotNull
	public String getComment() {
		return comment;
	}
	
	@NotNull
	public String getImageUrl() {
		return imageUrl;
	}

	// TODO validate for empty or a url
	public void setImageUrl(String imageUrl) {
		String old = this.imageUrl;
		this.imageUrl = imageUrl;
		pcs.firePropertyChange("imageUrl", old, imageUrl);
	}

	@NotNull
	public String getVideoUrl() {
		return videoUrl;
	}

	public void setVideoUrl(String videoUrl) {
		String old = this.videoUrl;
		this.videoUrl = videoUrl;
		pcs.firePropertyChange("videoUrl", old, videoUrl);
	}
	
	public void setCalculatedRating(float calculatedRating) {
		if (getGameTitle().supportsCalculatedRating())
			throw new UnsupportedOperationException("supportsCalculableStars() is true but setCalculableRating() is not overridden");
		throw new UnsupportedOperationException();
	}

	/**
	 * This should be overridden if the GameTitle supports 
	 * calculated ratings.
	 * @return The rating as calculated by some means not in-game.
	 * @throws UnsupportedOperationException If this method is not overridden
	 */
	@Transient
	public float getCalculatedRating() {
		if (getGameTitle().supportsCalculatedRating())
			throw new UnsupportedOperationException("supportsCalculableStars() is true but getCalculatedRating() is not overridden");
		throw new UnsupportedOperationException();
	}
	
	/**
	 * 
	 * @return Whether this object is ok to submit to scorehero
	 */
	@Transient
	public boolean isSubmittable() {
		boolean ok =
			(getStatus() == Status.NEW ||
			 getStatus() == Status.UNKNOWN) &&
			getScore() > 0 &&
			getSong() != null &&
			getParts().size() > 0;
			
		if (!ok) return false;
		
		for (Part p : getParts()) {
			if (!p.isSubmittable()) return false;
		}
		
		return true;
	}
	
	/**
	 * Submit this score to ScoreHero. An exception should be
	 * thrown if the score is not submittable.
	 * @throws Exception
	 */
	public abstract void submit() throws Exception;
	
	@Transient
	public abstract javax.swing.ImageIcon getRatingIcon();
	
	/**
	 * If calculated ratings are supported this can be overridden to
	 * provide that, otherwise {@link #getRatingIcon()} will simply
	 * be called.
	 * @param ignoreCalculatedRating
	 * @return
	 */
	@Transient
	public javax.swing.ImageIcon getRatingIcon(boolean ignoreCalculatedRating) {
		return getRatingIcon();
	}
	
	// override object methods
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Score)) return false;
		if (this == o) return true;
		Score s = (Score) o;
		
		boolean partsEqual = this.parts.size() == s.parts.size();
		
		if (partsEqual) { // same size
			Iterator<Part> it1 = this.parts.iterator();
			Iterator<Part> it2 = s.parts.iterator();
			
			while (it1.hasNext() && it2.hasNext()) {
				if (!it1.next().equals(it2.next())) {
					partsEqual = false;
					break;
				}
			}
		}
		
		// TODO figure out why !this.parts.equals(s.parts)
		
		if ((null == this.song && null != s.song) ||
			(null != this.song && null == s.song))
			return false;
		
		if ((null == this.submissionDate && null != s.submissionDate) ||
			(null != this.submissionDate && null == s.submissionDate))
			return false;
		
		return 
			((null == this.song && null == s.song) || 
			 this.song.equals(s.song)) &&
			this.score == s.score &&
			((null == this.submissionDate && null == s.submissionDate) ||
			 (this.submissionDate.equals(s.submissionDate))) &&
			partsEqual;
//			this.parts.equals(s.parts);
	}
	
	@Override
	public String toString() {
		return toString(null);
	}
	
	/**
	 * Allows for subclasses to insert extra stuff into
	 * the String representation after the hitPercent.
	 * @param extra
	 * @return
	 */
	protected String toString(String extra) {
		StringBuilder sb = new StringBuilder();
		sb.append(getId());
		sb.append(',');
		sb.append(getStatus());
		sb.append(',');
		sb.append(getGame());
		sb.append(',');
		sb.append(getGroup());
		sb.append(',');
		sb.append(song.getTitle());
		sb.append(',');
		sb.append(difficulty.toShortString());
		
		sb.append(',');
		sb.append(score);
		sb.append(",r=");
		sb.append(rating);
		
		if (getGameTitle().supportsCalculatedRating()) {
			sb.append(",cr=");
			sb.append(getCalculatedRating());
		}
		
		sb.append(",%=");
		sb.append(getHitPercent());
		sb.append(",ns=");
		sb.append(getStreak());
		
		if (null != extra)
			sb.append(extra);
		
		for (Part p : parts) {
			sb.append(',');
			sb.append('{');
			sb.append(p);
			sb.append('}');
		}
		
		sb.append(",cdate=");
		sb.append(creationDate == null ? null : DateFormat.getInstance().format(creationDate));
		sb.append(",sdate=");
		sb.append(submissionDate == null ? null : DateFormat.getInstance().format(submissionDate));
		
		sb.append(",pic=");
		sb.append(imageUrl);
		sb.append(",vid=");
		sb.append(videoUrl);
		
		return sb.toString();
	}
	
	
	
	// implement property change support
	
	@Transient
	protected final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	
	public void addPropertyChangeListener(
			PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}
	
    public void addPropertyChangeListener(
            String propertyName,
            PropertyChangeListener listener) {
    	pcs.addPropertyChangeListener(propertyName, listener);
    }
    
    public void removePropertyChangeListener(
			PropertyChangeListener listener) {
    	pcs.removePropertyChangeListener(listener);
    }
    
    public void removePropertyChangeListener(
            String propertyName,
            PropertyChangeListener listener) {
    	pcs.removePropertyChangeListener(propertyName, listener);
    }
}

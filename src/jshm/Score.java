package jshm;

import java.text.DateFormat;
import java.util.*;
import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.validator.*;

@Entity
@OnDelete(action=OnDeleteAction.CASCADE)
@Inheritance(strategy=InheritanceType.JOINED)
public abstract class Score {
	/**
	 * <ul>
	 *   <li>NEW - A newly entered score that hasn't been submitted to ScoreHero
	 *   <li>SUBMITTED - A score that has been submitted to ScoreHero
	 *   <li>DELETED - A score that has been submitted but was deleted on ScoreHero due to a new score overwriting it.
	 *   <li>SUMMARY - Represents the highest overall score, hitPercent, noteStreak but
	 *  				may not directly correspond to a particular score. scoreHeroId would 0 in this case.
	 * </ul>
	 * @author Tim Mullin
	 *
	 */
	public static enum Status {
		NEW, SUBMITTED, DELETED, SUMMARY
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
	
	private Set<Part> parts				= new LinkedHashSet<Part>(4);
	
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
		return this.status == Status.NEW;
	}
	
	public void setSong(Song song) {
		this.song = song;
		
		if (Difficulty.Strategy.BY_SONG == getGameTitle().getDifficultyStrategy()) {
			this.difficulty = song.getDifficulty();
		}
	}

	@ManyToOne
	public Song getSong() {
		return song;
	}
	
	@Transient
	public Game getGame() {
		return getSong().getGame();
	}
	
	@Transient
	public GameTitle getGameTitle() {
		return getSong().getGameTitle();
	}
	
	@NotNull
	@Enumerated(EnumType.STRING)
	public Difficulty getDifficulty() {
		return difficulty;
	}
	
	public void setDifficulty(Difficulty difficulty) {
		this.difficulty = difficulty;
	}

	@Min(0)
	public int getScore() {
		return score;
	}
	
	public void setScore(int score) {
		if (score < 0)
			throw new IllegalArgumentException("score must be >= 0");
		this.score = score;
	}

	@OneToMany(cascade={CascadeType.ALL}, fetch=FetchType.EAGER)
	@JoinColumn(name="score_id", nullable=false)
	@OrderBy("instrument")
	public Set<Part> getParts() {
		return parts;
	}

	public void setParts(Set<Part> parts) {
		this.parts = parts;
	}
	
	public void addPart(Part part) {
		this.parts.add(part);
	}
	
	/**
	 * 
	 * @param index The <b>1-based</b> index of the Part to retrieve
	 * @return The Part at the specified index
	 */
	@Transient
	public Part getPart(int index) {		
		if (index < 1 || parts.size() < index)
			return null;
		
		Part ret = null;
		Iterator<Part> it = parts.iterator();
		
		for (int i = 0; i < index; i++) {
			ret = it.next();
		}
		
		return ret;
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
//			throw new IllegalArgumentException("rating must be 0 or between " + getGame().title.getMinRating() + " and " + getGame().title.getMaxRating());
		this.rating = rating;
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
		this.comment = comment;
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
		this.imageUrl = imageUrl;
	}

	@NotNull
	public String getVideoUrl() {
		return videoUrl;
	}

	public void setVideoUrl(String videoUrl) {
		this.videoUrl = videoUrl;
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
	
	
	// override object methods
	
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
		sb.append(getGame());
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
		
		return sb.toString();
	}
}

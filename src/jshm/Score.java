package jshm;

import java.util.*;
import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.*;

@Entity
public abstract class Score {
	/**
	 * The id internal to JSHManager's database
	 */
	private int		id					= 0;
	
	/**
	 * The id in ScoreHero's database if this score has been submitted.
	 */
	private int		scoreHeroId			= 0;
	
	private Song	song				= null;
	
	private int		score				= 0;
	private int		rating				= 0;
	private Date	submissionDate		= null;
	private String	comment				= null;
	
	private String	imageUrl			= null;
	private String	videoUrl			= null;
	
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
	
	/**
	 * A score can only be modified if it has not been
	 * submitted to ScoreHero.
	 * @return
	 */
	@Transient
	public boolean isEditable() {
		return this.scoreHeroId == 0;
	}
	
	public void setSong(Song song) {
		this.song = song;
	}

	@ManyToOne
	public Song getSong() {
		return song;
	}
	
	@Transient
	public Game getGame() {
		return getSong().getGame();
	}
	
	/**
	 * This must be overridden if the game's {@link Difficulty.Strategy}
	 * is BY_SCORE.
	 * @return The difficulty level that this score was achieved on.
	 */
	@Transient
	public Difficulty getDifficulty() {
		switch (getGame().title.getDifficultyStrategy()) {
			case BY_SONG:
				return getSong().getDifficulty();
		}
		
//		return null;
		throw new UnsupportedOperationException("getDifficulty() may need to be overridden");
	}
	
	/**
	 * This should be overridden in subclasses when the game's
	 * {@link Difficulty.Strategy} is BY_SCORE.
	 * @param difficulty
	 */
	public void setDifficulty(Difficulty difficulty) {
		throw new UnsupportedOperationException("DifficultyStrategy is not BY_SCORE for this game");
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

	@OneToMany
	@JoinColumn
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
		switch (getGame().title.getStreakStrategy()) {
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
		if (rating < getGame().title.getMinRating() ||
			getGame().title.getMaxRating() < rating)
			throw new IllegalArgumentException("rating must be between " + getGame().title.getMinRating() + " and " + getGame().title.getMaxCalculatedRating());
		this.rating = rating;
	}

	/**
	 * 
	 * @return The rating as conveyed in-game.
	 */
	public int getRating() {
		return rating;
	}
	
	public void setSubmissionDate(Date submissionDate) {
		this.submissionDate = submissionDate;
	}

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
	
	@Transient
	public void setCalculatedRating(float calculatedRating) {
		if (getGame().title.supportsCalculatedRating())
			throw new UnsupportedOperationException("supportsCalculableStars() is true but setCalculableRating() is not overridden");
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @return The rating as calculated by some means not in-game.
	 */
	public float getCalculatedRating() {
		if (getGame().title.supportsCalculatedRating())
			throw new UnsupportedOperationException("supportsCalculableStars() is true but getCalculatedRating() is not overridden");
		throw new UnsupportedOperationException();
	}
}

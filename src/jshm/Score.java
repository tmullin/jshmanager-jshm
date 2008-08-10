package jshm;

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
	 * The id internal to JSHManager's database
	 */
	private int		id					= 0;
	
	/**
	 * The id in ScoreHero's database if this score has been submitted.
	 */
	private int		scoreHeroId			= 0;
	
	private Song	song				= null;
	
	/**
	 * This will represent the highest difficulty of the parts.
	 */
	private Difficulty difficulty		= null;
	
	private int		score				= 0;
	private int		rating				= 0;
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
	
	public void setCalculatedRating(float calculatedRating) {
		if (getGameTitle().supportsCalculatedRating())
			throw new UnsupportedOperationException("supportsCalculableStars() is true but setCalculableRating() is not overridden");
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @return The rating as calculated by some means not in-game.
	 */
	@Transient
	public float getCalculatedRating() {
		if (getGameTitle().supportsCalculatedRating())
			throw new UnsupportedOperationException("supportsCalculableStars() is true but getCalculatedRating() is not overridden");
		throw new UnsupportedOperationException();
	}
	
	
	// override object methods
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getGameTitle());
		sb.append(',');
		sb.append(song.getTitle());
		sb.append(',');
		sb.append(score);
		sb.append(',');
		sb.append(rating);
		
		for (Part p : parts) {
			sb.append(',');
			sb.append('{');
			sb.append(p);
			sb.append('}');
		}
		
		return sb.toString();
	}
}

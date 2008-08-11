package jshm.gh;

import java.util.List;
import javax.persistence.*;

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
	public static List<GhSong> getSongs(final GhGame game, final Difficulty difficulty) {
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

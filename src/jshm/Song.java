package jshm;

import javax.persistence.*;

import org.hibernate.validator.*;

@Entity
public abstract class Song {
	/**
	 * The id internal to JSHManager's database. 
	 */
	private int id = 0;
	
	/**
	 * The ScoreHero id for this song.
	 */
	private int scoreHeroId	= 0;

	/**
	 * The default display order for this song.
	 */
	private int order	= 0;

	public abstract Game getGame();
	
	/**
	 * If the game's {@link Difficulty.Strategy} is BY_SONG this
	 * should be overridden to return it. Otherwise it should
	 * throw an {@link UnsupportedOperationException}.
	 * @return
	 * @throws UnsupportedOperationException If this song's game's difficulty strategy is not BY_SONG.
	 */
	@Transient
	public Difficulty getDifficulty() {
		throw new UnsupportedOperationException();
	}

	@Id
	@Min(1)
	public int getId() {
		return id;
	}

	@SuppressWarnings("unused")
	private void setId(int id) {
		this.id = id;
	}
	
	@Min(0)
	public int getScoreHeroId() {
		return scoreHeroId;
	}

	public void setScoreHeroId(int scoreHeroId) {
		this.scoreHeroId = scoreHeroId;
	}

	@Column(name = "ordering")
	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}
}

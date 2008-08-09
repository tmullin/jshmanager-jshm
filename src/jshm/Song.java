package jshm;

import javax.persistence.*;

import org.hibernate.validator.*;

/**
 * Represents common attributes among songs from
 * different games.
 * @author Tim Mullin
 *
 */
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

	private Game game = null;

	private String	title	= "UNKNOWN";
	
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

	@NotNull
	@Enumerated(EnumType.STRING) // this probably won't work
//	@Type(type="jshm.hibernate.FakeEnumUserType.Song")
	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}
	
	@NotEmpty
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		if (title.isEmpty())
			throw new IllegalArgumentException("title cannot be empty");
		this.title = title;
	}
}

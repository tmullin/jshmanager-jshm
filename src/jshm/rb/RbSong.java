package jshm.rb;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.validator.Min;

import jshm.Song;

public class RbSong extends Song {
	static final Logger LOG = Logger.getLogger(RbSong.class.getName());
	
	private List<RbGame> games = new ArrayList<RbGame>();
	private int tierLevel = 0;
	
	@OneToMany(fetch=FetchType.EAGER, cascade=CascadeType.ALL)
	@Fetch(FetchMode.SELECT)
	@JoinColumn(name="song_id", nullable=false)
	public List<RbGame> getGames() {
		return games;
	}
	
	@Transient
	public RbGame getGames(int index) {
		return games.get(index);
	}

	public void setGames(List<RbGame> games) {
		this.games = games;
	}
	
	public void setGames(int index, RbGame game) {
		games.set(index, game);
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
		return ((RbGame) getGame()).getTierName(this.getTierLevel());
	}
}

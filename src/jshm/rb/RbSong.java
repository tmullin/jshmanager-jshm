package jshm.rb;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.CollectionOfElements;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import jshm.Game;
import jshm.GameTitle;
import jshm.Song;

@Entity
public class RbSong extends Song {
	static final Logger LOG = Logger.getLogger(RbSong.class.getName());
	
	public static RbSong getByScoreHeroId(final int id) {
		LOG.finer("Querying database for song with scoreHeroId=" + id);
		
		org.hibernate.Session session = jshm.hibernate.HibernateUtil.getCurrentSession();
	    session.beginTransaction();
	    RbSong result =
			(RbSong)
			session.createQuery(
				String.format(
					"from RbSong where scoreHeroId=%d", id))
				.uniqueResult();
	    session.getTransaction().commit();
		
		return result;
	}
	

	private List<String> gameStrs = new ArrayList<String>();
	
	// this is a total hack since i can't figure out how to use
	// my fake enum type in a list
	@CollectionOfElements(fetch=FetchType.EAGER)
	@Cascade({CascadeType.ALL})
	@JoinTable(name="rbsong_games",
		joinColumns={
			@JoinColumn(name="song_id", nullable=false)})
	@Fetch(FetchMode.SELECT)
	public List<String> getGameStrs() {
		return gameStrs;
	}
	
	public void setGameStrs(List<String> gameStrs) {
		this.gameStrs = gameStrs;
		
		games.clear();
		for (String s : gameStrs)
			games.add((RbGame) Game.valueOf(s));
	}
	
	private List<RbGame> games = new ArrayList<RbGame>();
	
	@Transient
	public List<RbGame> getGames() {
		return games;
	}
	
	@Transient
	public RbGame getGames(int index) {
		return games.get(index);
	}

	public void setGames(List<RbGame> games) {
		this.games = games;
		
		gameStrs.clear();
		for (RbGame g : games)
			this.gameStrs.add(g.toString());
	}
	
	public void setGames(int index, RbGame game) {
		games.set(index, game);
		gameStrs.set(index, game.toString());
	}
	
	public void addGame(RbGame game) {
//		LOG.finer("adding game " + game + " to song " + getScoreHeroId() + ":" + getTitle());
		games.add(game);
		gameStrs.add(game.toString());
	}
	
	public boolean update(RbSong song) {
		return updateGames(song);
	}
	
	/**
	 * Updates this song to include the RbGame of the provided song
	 * if it is not already present for this song.
	 * @param song
	 * @return
	 */
	public boolean updateGames(RbSong song) {
		if (song.games.size() == 0) return false;
		if (games.contains(song.getGames(0))) return false;
		addGame(song.getGames(0));
		return true;
	}
	
	@Override
	@Transient
	public GameTitle getGameTitle() {
		return games.size() > 0 ? games.get(0).title : null;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append('[');
		if (getGames().size() > 0) {
			sb.append(games.get(0));
			
			final int count = games.size();
			for (int i = 1; i < count; i++) {
				sb.append(',');
				sb.append(games.get(i));
			}
		}
		sb.append(']');
		sb.append(',');
		sb.append(getScoreHeroId());
		sb.append(',');
		sb.append(getTitle());
		
		return sb.toString();
	}
}

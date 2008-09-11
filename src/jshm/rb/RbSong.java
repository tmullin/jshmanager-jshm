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

import jshm.Song;

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
	
	
	private List<RbGame> games = new ArrayList<RbGame>();
	
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
	
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append('[');
		for (RbGame g : getGames()) {
			sb.append(g);
			sb.append(',');
		}
		sb.append(']');
		sb.append(',');
		sb.append(getScoreHeroId());
		sb.append(',');
		sb.append(getTitle());
		
		return sb.toString();
	}
}

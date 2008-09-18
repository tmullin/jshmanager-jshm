package jshm.rb;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Logger;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;

import jshm.GameTitle;
import jshm.Instrument;
import jshm.Platform;
import jshm.Song;
import jshm.SongOrder;

import org.hibernate.annotations.CollectionOfElements;
import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;
import org.hibernate.annotations.Type;
import org.hibernate.validator.NotNull;

@Entity
public class RbSong extends Song {
	static final Logger LOG = Logger.getLogger(RbSong.class.getName());
	
	@SuppressWarnings("unchecked")
	public static List<RbSong> getSongs(final RbGameTitle game) {
		LOG.finest("Querying database for all songs for " + game);
		
		org.hibernate.Session session = jshm.hibernate.HibernateUtil.getCurrentSession();
	    session.beginTransaction();
	    List<RbSong> result =
			(List<RbSong>)
			session.createQuery(
				String.format(
					"from RbSong where gameTitle='%s'",
					game.toString()))
				.list();
	    session.getTransaction().commit();
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static List<SongOrder> getSongs(final RbGame game, Instrument.Group group) {
		LOG.finest("Querying database for all song orders for " + game + " with group " + group);
		
		// TODO change GUITAR_BASS to a constant in RbGameTitle
		if (group.size > 1)
			group = Instrument.Group.GUITAR_BASS;
		
		org.hibernate.Session session = jshm.hibernate.HibernateUtil.getCurrentSession();
	    session.beginTransaction();
	    List<SongOrder> result =
			(List<SongOrder>)
			session.createQuery(
				String.format(
					"from SongOrder where gameTitle='%s' and platform='%s' and instrumentgroup='%s' order by tier, ordering",
					game.title,
					game.platform,
					group))
				.list();
	    session.getTransaction().commit();
		
		return result;
	}
	
	public static RbSong getByScoreHeroId(final int id) {
		LOG.finest("Querying database for song with scoreHeroId=" + id);
		
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
	
	private GameTitle gameTitle;
	
	@Type(type="jshm.hibernate.GameTitleUserType")
	@NotNull
	public GameTitle getGameTitle() {
		return gameTitle;
	}
	
	public void setGameTitle(GameTitle gameTitle) {
		if (!(gameTitle instanceof RbGameTitle))
			throw new IllegalArgumentException("gameTitle must be an RbGameTitle, got a " + gameTitle.getClass().getName());
		this.gameTitle = gameTitle;
	}
	
	private SortedSet<Platform> platforms = new TreeSet<Platform>();
	
	@CollectionOfElements(fetch=FetchType.EAGER)
	@Enumerated(EnumType.STRING)
	@Sort(type=SortType.NATURAL)
	public SortedSet<Platform> getPlatforms() {
		return platforms;
	}

	public void setPlatforms(SortedSet<Platform> platforms) {
		this.platforms = platforms;
	}
	
	public void addPlatform(Platform platform) {
//		LOG.finer("adding game " + game + " to song " + getScoreHeroId() + ":" + getTitle());
		platforms.add(platform);
	}
	
	public boolean update(RbSong song) {
		return updatePlatforms(song);
	}
	
	/**
	 * Updates this song to include the RbGame of the provided song
	 * if it is not already present for this song.
	 * @param song
	 * @return
	 */
	public boolean updatePlatforms(RbSong song) {
		if (song.platforms.size() == 0) return false;
		Platform p = song.platforms.iterator().next();
		if (platforms.contains(p)) return false;
		addPlatform(p);
		return true;
	}
		
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append('[');
		sb.append(jshm.util.Util.implode(platforms.toArray()));
		sb.append(']');
		sb.append(',');
		sb.append(getScoreHeroId());
		sb.append(',');
		sb.append(getTitle());
		
		return sb.toString();
	}
}

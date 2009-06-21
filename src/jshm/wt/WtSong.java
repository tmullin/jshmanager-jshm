package jshm.wt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Logger;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;

import org.hibernate.Criteria;
import org.hibernate.annotations.CollectionOfElements;
import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;
import org.hibernate.annotations.Type;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.validator.NotNull;

import jshm.*;
import jshm.Instrument.Group;
import jshm.sh.URLs;

@Entity
public class WtSong extends Song {
	static final Logger LOG = Logger.getLogger(WtSong.class.getName());
	
	public static WtSong getByScoreHeroId(final int id) {
		return getByScoreHeroId(id, Difficulty.EXPERT);
	}
	
	public static WtSong getByScoreHeroId(final int id, Difficulty diff) {
		LOG.finest("Querying database for WtSong with scoreHeroId=" + id);
		
		org.hibernate.Session session = jshm.hibernate.HibernateUtil.getCurrentSession();
	    session.beginTransaction();
	    WtSong result = getByScoreHeroId(session, id, diff);
	    session.getTransaction().commit();
		
		return result;
	}
	
	public static WtSong getByScoreHeroId(org.hibernate.Session session, final int id, Difficulty diff) {
		WtSong ret = (WtSong)
			session.createQuery(
				"from WtSong where scoreHeroId=:shid")
				.setInteger("shid", id)
				.uniqueResult();
		
		if (Difficulty.EXPERT_PLUS == diff &&
			!((WtGameTitle) ret.gameTitle).supportsExpertPlus)
			return null;
			
		return ret;
	}
	
	@SuppressWarnings("unchecked")
	public static List<WtSong> getAllByTitle(WtGame game, String title, Difficulty diff) {
		LOG.finest("Querying database for WtSong for " + game + " with title=" + title);
		
		org.hibernate.Session session = jshm.hibernate.HibernateUtil.getCurrentSession();
	    session.beginTransaction();
		Criteria c = session.createCriteria(WtSong.class)
			.add(Restrictions.eq("gameTitle", game.title))
			.add(Restrictions.ilike("title", title, MatchMode.ANYWHERE))
			.addOrder(Order.asc("title"));
		
		if (Difficulty.EXPERT_PLUS == diff)
			c.add(Restrictions.eq("expertPlusSupported", true));
		
	    List<WtSong> result = (List<WtSong>) c.list();
	    session.getTransaction().commit();
		
		return result;
	}
	
	public static WtSong getByTitle(WtGame game, String title, Difficulty diff) {
		List<WtSong> list = getAllByTitle(game, title, diff);
		return list.size() == 1 ? list.get(0) : null;
	}
	
	public static List<WtSong> getOrderedByTitles(final WtGame game, final Instrument.Group group, final Difficulty diff) {
		List<WtSong> result = getSongs(true, game, group, diff);
		Collections.sort(result);
	    return result;
	}
	
	public static List<WtSong> getSongs(final boolean asSongList, final WtGame game, Instrument.Group group) {
		return getSongs(asSongList, game, group, Difficulty.EXPERT);
	}
	
	public static List<WtSong> getSongs(final boolean asSongList, final WtGame game, Instrument.Group group, Difficulty diff) {
		return getSongs(asSongList, game, group, diff, null);
	}
	
	@SuppressWarnings("unchecked")
	public static List<WtSong> getSongs(final boolean asSongList, final WtGame game, Instrument.Group group, final Difficulty diff, final Song.Sorting sorting) {
		if (null == sorting) {
			List<SongOrder> orders = getSongs(game, group, diff);
			List<WtSong> ret = new ArrayList<WtSong>(orders.size());
			
			for (SongOrder o : orders)
				ret.add((WtSong) o.getSong());
			
			return ret;
		}
		
		
		// TODO change GUITAR_BASS to a constant in RbGameTitle
		if (group.size > 1)
			group = Instrument.Group.GUITAR_BASS;
		else if (Instrument.Group.WTDRUMS == group)
			group = Instrument.Group.DRUMS;
		
		String orderClause = "title";
		
		switch (sorting) {
			case ARTIST: orderClause = "artist"; break;
			case DECADE: orderClause = "year"; break;
			case GENRE: orderClause = "genre"; break;
		}
		
		org.hibernate.Session session = jshm.hibernate.HibernateUtil.getCurrentSession();
	    session.beginTransaction();
	    List<WtSong> result =
			(List<WtSong>)
			session.createQuery(
				"from WtSong where gameTitle=:ttl and " +
				":plat in elements(platforms) " +
				"order by " + orderClause)
			.setString("ttl", game.title.toString())
			.setString("plat", game.platform.toString())
			.list();
	    session.getTransaction().commit();
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static List<SongOrder> getSongs(final WtGame game, Instrument.Group group, Difficulty diff) {
		LOG.finest("Querying database for all song orders for " + game + " with group " + group + " and diff " + diff);
		
		// TODO change GUITAR_BASS to a constant in WtGameTitle
		if (group.size > 1)
			group = Instrument.Group.GUITAR_BASS;
		else if (Instrument.Group.WTDRUMS == group)
			group = Instrument.Group.DRUMS;
		
		org.hibernate.Session session = jshm.hibernate.HibernateUtil.getCurrentSession();
	    session.beginTransaction();
	    List<SongOrder> result =
			(List<SongOrder>)
			session.createQuery(
				"from SongOrder where gameTitle=:ttl and platform=:plat and instrumentgroup=:group order by tier, ordering")
			.setString("ttl", game.title.toString())
			.setString("plat", game.platform.toString())
			.setString("group", group.toString())
			.list();
	    session.getTransaction().commit();
		
	    // FIXME this is inefficient since most songs do not support expert+
	    if (Difficulty.EXPERT_PLUS == diff) {
	    	for (Iterator<SongOrder> it = result.iterator(); it.hasNext(); ) {
	    		if (!((WtSong) it.next().getSong()).isExpertPlusSupported())
	    			it.remove();
	    	}
	    }
	    
		return result;
	}
	
	
	private boolean expertPlusSupported = false;

	public boolean isExpertPlusSupported() {
		return expertPlusSupported;
	}

	public void setExpertPlusSupported(boolean expertPlusSupported) {
		this.expertPlusSupported = expertPlusSupported;
	}


	private GameTitle gameTitle;
	
	@Type(type="jshm.hibernate.GameTitleUserType")
	@NotNull
	public GameTitle getGameTitle() {
		return gameTitle;
	}
	
	public void setGameTitle(GameTitle gameTitle) {
		if (!(gameTitle instanceof WtGameTitle))
			throw new IllegalArgumentException("gameTitle must be a WtGameTitle, got a " + gameTitle.getClass().getName());
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
	
	public boolean update(WtSong song, boolean replacePlatforms) {
		boolean updated = super.update(song);
		updated = updatePlatforms(song, replacePlatforms) || updated;
		return updated;
	}
	
	/**
	 * Updates this song to have the platforms of the provided song.
	 * This songs existing platforms are cleared first.
	 * @param song
	 * @return
	 */
	public boolean updatePlatforms(WtSong song, boolean replacePlatforms) {
		assert 0 != song.platforms.size();
		
		// see if we have the same ones first
		if (this.platforms.size() == song.platforms.size()) {
			Iterator<Platform>
				i1 = this.platforms.iterator(),
				i2 = song.platforms.iterator();
			
			while (i1.hasNext()) {
				// we can only do this since we're using a SortedSet
				if (!i1.next().equals(i2.next())) {
					// found a different one
					break;
				}
				
				return false;
			}
		}
		
		if (replacePlatforms)
			this.platforms.clear();
		this.platforms.addAll(song.platforms);
		
		return true;
	}
		
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append('[');
		sb.append(jshm.util.PhpUtil.implode(platforms));
		sb.append(']');
		sb.append(',');
		sb.append(getScoreHeroId());
		sb.append(',');
		sb.append(getTitle());
		
		return sb.toString();
	}
	
	
	@Override
	public String getRankingsUrl(Game game, Group group, Difficulty diff) {
		return URLs.wt.getRankingsUrl((WtGame) game, group, diff, this);
	}
}

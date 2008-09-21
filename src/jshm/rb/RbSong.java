/*
 * -----LICENSE START-----
 * JSHManager - A Java-based tool for managing one's ScoreHero account.
 * Copyright (C) 2008 Tim Mullin
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * -----LICENSE END-----
*/
package jshm.rb;

import java.util.ArrayList;
import java.util.Collections;
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
	
	public static List<RbSong> getSongs(final boolean asRbSongList, final RbGame game, Instrument.Group group) {
		List<SongOrder> orders = getSongs(game, group);
		List<RbSong> ret = new ArrayList<RbSong>(orders.size());
		
		for (SongOrder o : orders)
			ret.add((RbSong) o.getSong());
		
		return ret;
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

	public static List<RbSong> getSongsOrderedByTitles(final RbGame game, final Instrument.Group group) {
		List<RbSong> result = getSongs(true, game, group);
		Collections.sort(result);
	    return result;
	}
	
	public static RbSong getByScoreHeroId(final int id) {
		LOG.finest("Querying database for RbSong with scoreHeroId=" + id);
		
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

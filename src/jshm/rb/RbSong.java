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
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Logger;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Transient;

import jshm.Difficulty;
import jshm.Game;
import jshm.GameTitle;
import jshm.Instrument;
import jshm.Platform;
import jshm.Song;
import jshm.SongOrder;
import jshm.SongSource;
import jshm.Instrument.Group;
import jshm.sh.URLs;
import jshm.xml.RbSongInfoFetcher;

import org.hibernate.annotations.CollectionOfElements;
import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;
import org.hibernate.annotations.Type;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
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
				"from RbSong where gameTitle=:ttl")
				.setString("ttl", game.toString())
				.list();
	    session.getTransaction().commit();
		
		return result;
	}
	
	public static List<RbSong> getSongs(final boolean asRbSongList, final RbGame game, Instrument.Group group) {
		return getSongs(asRbSongList, game, group, null);
	}
	
	@SuppressWarnings("unchecked")
	public static List<RbSong> getSongs(final boolean asRbSongList, final RbGame game, Instrument.Group group, final Song.Sorting sorting) {
		if (null == sorting) {
			List<SongOrder> orders = getSongs(game, group);
			List<RbSong> ret = new ArrayList<RbSong>(orders.size());
			
			for (SongOrder o : orders)
				ret.add((RbSong) o.getSong());
			
			return ret;
		}
		
		
		// TODO change GUITAR_BASS to a constant in RbGameTitle
		if (group.size > 1)
			group = Instrument.Group.GUITAR_BASS;
		
		String orderClause = "title";
		
		switch (sorting) {
			case ARTIST: orderClause = "artist"; break;
			case DECADE: orderClause = "year"; break;
			case GENRE: orderClause = "genre"; break;
		}
		
		org.hibernate.Session session = jshm.hibernate.HibernateUtil.getCurrentSession();
	    session.beginTransaction();
	    List<RbSong> result =
			(List<RbSong>)
			session.createQuery(
				"from RbSong where gameTitle=:ttl and " +
				":plat in elements(platforms) " +
				"order by " + orderClause)
			.setString("ttl", game.title.toString())
			.setString("plat", game.platform.toString())
			.list();
	    session.getTransaction().commit();
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static List<SongOrder> getSongs(final RbGame game, Instrument.Group group) {
		LOG.finest("Querying database for all song orders for " + game + " with group " + group);
		
		org.hibernate.Session session = jshm.hibernate.HibernateUtil.getCurrentSession();
	    session.beginTransaction();
	    List<SongOrder> result =
			(List<SongOrder>)
			session.createQuery(
				"from SongOrder where gameTitle=:ttl and platform=:plat and instrumentgroup=:group order by tier, ordering")
			.setString("ttl", game.title.toString())
			.setString("plat", game.platform.toString())
			.setString("group", group.getEffectiveSongOrderGroup().toString())
			.list();
	    session.getTransaction().commit();
		
		return result;
	}

	public static List<RbSong> getOrderedByTitles(final RbGame game, final Instrument.Group group) {
		List<RbSong> result = getSongs(true, game, group);
		Collections.sort(result);
	    return result;
	}
	
	public static RbSong getByScoreHeroId(final int id) {
		LOG.finest("Querying database for RbSong with scoreHeroId=" + id);
		
		org.hibernate.Session session = jshm.hibernate.HibernateUtil.getCurrentSession();
	    session.beginTransaction();
	    RbSong result =	getByScoreHeroId(session, id);
	    session.getTransaction().commit();
		
		return result;
	}
	
	public static RbSong getByScoreHeroId(org.hibernate.Session session, final int id) {
		return (RbSong)
			session.createQuery(
				"from RbSong where scoreHeroId=:shid")
				.setInteger("shid", id)
				.uniqueResult();
	}
	
	public static RbSong getByTitle(RbGame game, String title) {
		List<RbSong> list = getAllByTitle(game, title);
		return list.size() == 1 ? list.get(0) : null;
	}
	
	@SuppressWarnings("unchecked")
	public static List<RbSong> getAllByTitle(RbGame game, String title) {
		LOG.finest("Querying database for RbSong for " + game + " with title=" + title);
		
		org.hibernate.Session session = jshm.hibernate.HibernateUtil.getCurrentSession();
	    session.beginTransaction();
	    List<RbSong> result =
			(List<RbSong>)
			session.createCriteria(RbSong.class)
				.add(Restrictions.eq("gameTitle", game.title))
				.add(Restrictions.ilike("title", title, MatchMode.ANYWHERE))
				.addOrder(Order.asc("title"))
			.list();
	    session.getTransaction().commit();
		
		return result;
	}
	
	@Transient
	public int getTierLevel(Sorting sorting) {
		switch (sorting) {
			case DIFFICULTY:
				int ret = -1;
				
				switch (getSongOrder().getGroup()) {
					case GUITAR: ret = rb2GuitarDiff; break;
					case BASS: ret = rb2BassDiff; break;
					case VOCALS: ret = rb2VocalsDiff; break;
					case DRUMS: ret = rb2DrumsDiff; break;
					default: ret = rb2BandDiff; break;
				}
				
				return ret + 2; // 1 is <UNKNOWN>
				
			default:
				return super.getTierLevel(sorting);
		}
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
	
	@Transient @Override
	public javax.swing.ImageIcon getSongSourceIcon() {
		javax.swing.ImageIcon ret = null;
		
		if (null != songSource) {
			SongSource ss = SongSource.smartValueOf(songSource);
			ret = null == ss ? null : ss.getIcon();
		}
		
		// this will return TBRB icon
		return null != ret ? ret : super.getSongSourceIcon();
	}
	
	// fields for http://pksage.com/xml.php
	private String
		songSource = null;
	
	private int
		rawGuitarDiff = -1,
		rawBassDiff = -1,
		rawVocalsDiff = -1,
		rawDrumsDiff = -1,
		rawBandDiff = -1,
		
		rb2GuitarDiff = -1,
		rb2BassDiff = -1,
		rb2VocalsDiff = -1,
		rb2DrumsDiff = -1,
		rb2BandDiff = -1;
	
	public String getSongSource() {
		return songSource;
	}

	public void setSongSource(String songSource) {
		this.songSource = songSource;
	}

	public int getRawGuitarDiff() {
		return rawGuitarDiff;
	}

	public void setRawGuitarDiff(Integer rawGuitarDiff) {
		this.rawGuitarDiff = rawGuitarDiff == null ? -1 : rawGuitarDiff;
	}

	public int getRawBassDiff() {
		return rawBassDiff;
	}

	public void setRawBassDiff(Integer rawBassDiff) {
		this.rawBassDiff = rawBassDiff == null ? -1 : rawBassDiff;
	}

	public int getRawVocalsDiff() {
		return rawVocalsDiff;
	}

	public void setRawVocalsDiff(Integer rawVocalsDiff) {
		this.rawVocalsDiff = rawVocalsDiff == null ? -1 : rawVocalsDiff;
	}

	public int getRawDrumsDiff() {
		return rawDrumsDiff;
	}

	public void setRawDrumsDiff(Integer rawDrumsDiff) {
		this.rawDrumsDiff = rawDrumsDiff == null ? -1 : rawDrumsDiff;
	}

	public int getRawBandDiff() {
		return rawBandDiff;
	}

	public void setRawBandDiff(Integer rawBandDiff) {
		this.rawBandDiff = rawBandDiff == null ? -1 : rawBandDiff;
	}
	
	public int getRb2GuitarDiff() {
		return rb2GuitarDiff;
	}

	public void setRb2GuitarDiff(Integer rb2GuitarDiff) {
		this.rb2GuitarDiff = rb2GuitarDiff == null ? -1 : rb2GuitarDiff;
	}

	public int getRb2BassDiff() {
		return rb2BassDiff;
	}

	public void setRb2BassDiff(Integer rb2BassDiff) {
		this.rb2BassDiff = rb2BassDiff == null ? -1 : rb2BassDiff;
	}

	public int getRb2VocalsDiff() {
		return rb2VocalsDiff;
	}

	public void setRb2VocalsDiff(Integer rb2VocalsDiff) {
		this.rb2VocalsDiff = rb2VocalsDiff == null ? -1 : rb2VocalsDiff;
	}

	public int getRb2DrumsDiff() {
		return rb2DrumsDiff;
	}

	public void setRb2DrumsDiff(Integer rb2DrumsDiff) {
		this.rb2DrumsDiff = rb2DrumsDiff == null ? -1 : rb2DrumsDiff;
	}

	public int getRb2BandDiff() {
		return rb2BandDiff;
	}

	public void setRb2BandDiff(Integer rb2BandDiff) {
		this.rb2BandDiff = rb2BandDiff == null ? -1 : rb2BandDiff;
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
		return update(song, false);
	}
	
	/**
	 * 
	 * @param song
	 * @param replacePlatforms true to clear platforms before adding or false to keep existing platforms and only add new ones
	 * @return
	 */
	public boolean update(RbSong song, boolean replacePlatforms) {
		boolean updated = super.update(song);
		updated = updatePlatforms(song, replacePlatforms) || updated;
		return updated;
	}
	
	public boolean update(final RbSongInfoFetcher.SongInfo info) {
		boolean updated = false;
		
		if ((null == artist && null != info.artist) ||
			(null != artist && !artist.equals(info.artist))) {
			setArtist(info.artist);
			updated = true;
		}
		
		if ((null == album && null != info.album) ||
			(null != album && !album.equals(info.album))) {
			setAlbum(info.album);
			updated = true;
		}
		
		if ((null == genre && null != info.genre) ||
			(null != genre && !genre.equals(info.genre))) {
			setGenre(info.genre);
			updated = true;
		}
		
		if ((null == songSource && null != info.game) ||
			(null != songSource && !songSource.equals(info.game))) {
			setSongSource(info.game);
			updated = true;
		}
		
		if ((null == songPack && null != info.pack) ||
			(null != songPack && !songPack.equals(info.pack))) {
			setSongPack(info.pack);
			updated = true;
		}
		
		if (year != info.year) {
			setYear(info.year);
			updated = true;
		}
		
		if (trackNum != info.trackNum) {
			setTrackNum(info.trackNum);
			updated = true;
		}
		
		if (recordingType != info.recording) {
			setRecordingType(info.recording);
			updated = true;
		}
		
		
		
		if (rawGuitarDiff != info.guitar) {
			rawGuitarDiff = info.guitar;
			updated = true;
		}
		
		if (rawBassDiff != info.bass) {
			rawBassDiff = info.bass;
			updated = true;
		}
		
		if (rawVocalsDiff != info.vocals) {
			rawVocalsDiff = info.vocals;
			updated = true;
		}
		
		if (rawDrumsDiff != info.drums) {
			rawDrumsDiff = info.drums;
			updated = true;
		}
		
		if (rawBandDiff != info.band) {
			rawBandDiff = info.band;
			updated = true;
		}
		
		
		if (rb2GuitarDiff != info.guitarRb2) {
			rb2GuitarDiff = info.guitarRb2;
			updated = true;
		}
		
		if (rb2BassDiff != info.bassRb2) {
			rb2BassDiff = info.bassRb2;
			updated = true;
		}
		
		if (rb2VocalsDiff != info.vocalsRb2) {
			rb2VocalsDiff = info.vocalsRb2;
			updated = true;
		}
		
		if (rb2DrumsDiff != info.drumsRb2) {
			rb2DrumsDiff = info.drumsRb2;
			updated = true;
		}
		
		if (rb2BandDiff != info.bandRb2) {
			rb2BandDiff = info.bandRb2;
			updated = true;
		}
		
		
		return updated;
	}
	
	/**
	 * Updates this song to have the platforms of the provided song.
	 * This songs existing platforms are cleared first if replacePlatforms is true.
	 * @param song
	 * @param replacePlatforms
	 * @return
	 */
	public boolean updatePlatforms(RbSong song, boolean replacePlatforms) {
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
		return URLs.rb.getRankingsUrl((RbGame) game, group, diff, this);
	}
	
	
	public static class Comparators {
		public static final Comparator<Song>
		DIFFICULTY = new Comparator<Song>() {
			@Override public int compare(Song o1, Song o2) {
				if (!(o1 instanceof RbSong && o2 instanceof RbSong))
					throw new ClassCastException();
				
				RbSong r1 = (RbSong) o1;
				RbSong r2 = (RbSong) o2;
				
				int raw1, raw2, tier1, tier2;
				
				switch (r1.songOrder.getGroup()) {
					case GUITAR:
						raw1 = r1.rawGuitarDiff;
						raw2 = r2.rawGuitarDiff;
						tier1 = r1.rb2GuitarDiff;
						tier2 = r2.rb2GuitarDiff;
						break;
						
					case BASS:
						raw1 = r1.rawBassDiff;
						raw2 = r2.rawBassDiff;
						tier1 = r1.rb2BassDiff;
						tier2 = r2.rb2BassDiff;
						break;
						
					case VOCALS:
						raw1 = r1.rawVocalsDiff;
						raw2 = r2.rawVocalsDiff;
						tier1 = r1.rb2VocalsDiff;
						tier2 = r2.rb2VocalsDiff;
						break;
						
					case DRUMS:
						raw1 = r1.rawDrumsDiff;
						raw2 = r2.rawDrumsDiff;
						tier1 = r1.rb2DrumsDiff;
						tier2 = r2.rb2DrumsDiff;
						break;
						
					default:
						raw1 = r1.rawBandDiff;
						raw2 = r2.rawBandDiff;
						tier1 = r1.rb2BandDiff;
						tier2 = r2.rb2BandDiff;
						break;
				}
				
				int ret = tier1 < tier2 ? -1 : tier1 == tier2 ? 0 : 1;
				
				if (0 == ret)
					ret = raw1 < raw2 ? -1 : raw1 == raw2 ? 0 : 1;
				
				if (0 == ret)
					ret = Song.Comparators.TITLE.compare(o1, o2);
				
				return ret;
			}
		};
	}
}

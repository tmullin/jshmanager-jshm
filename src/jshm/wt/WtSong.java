package jshm.wt;

import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;

import org.hibernate.annotations.CollectionOfElements;
import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;
import org.hibernate.annotations.Type;
import org.hibernate.validator.NotNull;

import jshm.*;
import jshm.Instrument.Group;

public class WtSong extends Song {
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
	
	public boolean update(WtSong song) {
		boolean updated = super.update(song);
		updated = updatePlatforms(song) || updated;
		return updated;
	}
	
	/**
	 * Updates this song to have the platforms of the provided song.
	 * This songs existing platforms are cleared first.
	 * @param song
	 * @return
	 */
	public boolean updatePlatforms(WtSong song) {
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
		// TODO Auto-generated method stub
		return null;
	}
}

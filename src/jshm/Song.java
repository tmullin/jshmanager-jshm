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
package jshm;

import java.util.Comparator;

import javax.persistence.*;

import jshm.Instrument.Group;
import jshm.util.Text;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.validator.*;

/**
 * Represents common attributes among songs from
 * different games.
 * @author Tim Mullin
 *
 */
@Entity
@Inheritance(strategy=InheritanceType.JOINED)
public abstract class Song implements Comparable<Song> {
	/**
	 * The id internal to JSHManager's database. 
	 */
	private int id = 0;
	
	/**
	 * The ScoreHero id for this song.
	 */
	protected int		scoreHeroId	= 0;
	private Game	game		= null;
	protected String
		title		= "UNKNOWN",
		artist		= null,
		album		= null,
		genre		= null,
		songPack	= null;
	
	protected int
		trackNum	= 0,
		year		= 0;
	
	protected RecordingType recordingType = null;
	
	protected SongOrder songOrder = null;
	
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
	@GeneratedValue(generator="song-id")
	@GenericGenerator(name="song-id", strategy="native")
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

	@Type(type="jshm.hibernate.GameUserType")
	public Game getGame() {
		return game;
	}
	
	public void setGame(Game game) {
		this.game = game;
	}
	
	/**
	 * This method is necessary to sidestep issues involving
	 * song sorting support.
	 * 
	 * @return The so-called "active game" that the user is
	 * currently dealing with.
	 */
	@Transient
	public Game getActiveGame() {
		return
		game != null
		? game
		: songOrder != null
		  ? songOrder.getGame()
		  : null;
	}
	
	@Transient
	public GameTitle getGameTitle() {
		return
		game != null
		? game.title
		: songOrder != null
		  ? songOrder.getGameTitle()
		  : null;
	}
	
	@Transient
	public javax.swing.ImageIcon getSongSourceIcon() {
		GameTitle ttl = getGameTitle();
		Game game = getGame();
		if (null == ttl || null == game)
			return null;
		
		// if the game supports DLC and this song is in the last tier then
		// it's almost certainly DLC so we can use the DLC icon
		boolean dlcVersion =
			game.supportsDLC &&
			getTierLevel(Sorting.SCOREHERO) == game.getTierCount(Sorting.SCOREHERO);
			
		SongSource ss = SongSource.smartValueOf(ttl.title, dlcVersion);
		return null == ss ? null : ss.getIcon();
	}
	
	@NotEmpty
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		if (null == title || title.isEmpty())
			throw new IllegalArgumentException("title cannot be null or empty");
		this.title = title;
	}
	
	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public String getAlbum() {
		return album;
	}

	public void setAlbum(String album) {
		this.album = album;
	}

	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}

	public String getSongPack() {
		return songPack;
	}

	public void setSongPack(String songPack) {
		this.songPack = songPack;
	}

	public int getTrackNum() {
		return trackNum;
	}

	public void setTrackNum(Integer trackNum) {
		this.trackNum = null == trackNum ? 0 : trackNum;
	}

	public int getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = null == year ? 0 : year;
	}

	@Enumerated(EnumType.STRING)
	public RecordingType getRecordingType() {
		return recordingType;
	}
	
	/**
	 * 
	 * @return <code>true</code> if this song's recording type is not {@link RecordingType#COVER},
	 * 			even if it's <code>null</code> (since we don't know and probably don't want to say
	 * 			"as made fameous by" unless we're sure.
	 */
	@Transient
	public boolean isMasterRecording() {
		return RecordingType.COVER != recordingType;
	}

	public void setRecordingType(RecordingType recordingType) {
		this.recordingType = recordingType;
	}

	@Transient
	public SongOrder getSongOrder() {
		return songOrder;
	}
	
	public void setSongOrder(SongOrder songOrder) {
		this.songOrder = songOrder;
	}
	
	/**
	 * 
	 * @return Return the <b>1-based</b> index of this song's tier.
	 */
	@Transient
	public int getTierLevel() {
		if (null == songOrder) return 0;
		return songOrder.getTier();
	}
	
	// TODO this probably isn't that efficient
	@Transient
	public int getTierLevel(Sorting sorting) {
		switch (sorting) {
			case SCOREHERO:
				return getTierLevel();
				
			case TITLE:
				char c = Character.toUpperCase(title.charAt(0));
				
				if ('A' <= c && c <= 'Z')
					return c - 'A' + 2; // 2 because it's 1-based, not 0
				return 1; // 1 is for 123/Symbol
				
			case ARTIST:
				if (null == artist)
					return 1; // <UNKNOWN>
				for (int i = 2; i <= getActiveGame().getTierCount(sorting); i++) {
					if (artist.equals(getActiveGame().getTierName(sorting, i)))
						return i;
				}
				
				throw new IllegalStateException("no tier found for artist");
				
			case GENRE:
				if (null == genre)
					return 1; // <UNKNOWN>
				for (int i = 2; i <= getActiveGame().getTierCount(sorting); i++) {
					if (genre.equals(getActiveGame().getTierName(sorting, i)))
						return i;
				}
				
				throw new IllegalStateException("no tier found for genre");
				
			case DECADE:
				if (0 == year) return 1; // <UNKNOWN>
				
				String decadeStr = (year / 10 * 10) + "s";
				
				for (int i = 2; i <= getActiveGame().getTierCount(sorting); i++) {
					if (decadeStr.equals(getActiveGame().getTierName(sorting, i)))
						return i;
				}
				
				throw new IllegalStateException("no tier found for decade");
		}
		
		throw new UnsupportedOperationException("sorting not implemented: " + sorting);
	}
	
	
	public boolean update(final Song other) {
		boolean updated = false;
		
		if (!title.equals(other.getTitle())) {
			setTitle(other.getTitle());
			updated = true;
		}
		
		if ((null == artist && null != other.artist) ||
			(null != artist && !artist.equals(other.artist))) {
			setArtist(other.artist);
			updated = true;
		}
		
		if ((null == album && null != other.album) ||
			(null != album && !album.equals(other.album))) {
			setAlbum(other.album);
			updated = true;
		}
		
		if ((null == genre && null != other.genre) ||
			(null != genre && !genre.equals(other.genre))) {
			setGenre(other.genre);
			updated = true;
		}
		
		if ((null == songPack && null != other.songPack) ||
			(null != songPack && !songPack.equals(other.songPack))) {
			setSongPack(other.songPack);
			updated = true;
		}
		
		if (year != other.year) {
			setYear(other.year);
			updated = true;
		}
		
		if (trackNum != other.trackNum) {
			setTrackNum(other.trackNum);
			updated = true;
		}
		
		if (recordingType != other.recordingType) {
			setRecordingType(other.recordingType);
			updated = true;
		}
		
		return updated;
	}
	
	
	// override object methods
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Song)) return false;
		if (this == o) return true;
		Song s = (Song) o;
		
		if ((this.game == null && s.game != null) ||
			(this.game != null && s.game == null))
			return false;
		
		return this.scoreHeroId == s.scoreHeroId &&
			this.title.equals(s.title) &&
			((this.game == null && s.game == null) ||
			 this.game.equals(s.game));
	}
	
	public int compareTo(Song song) {
		return this.title.compareTo(song.title);
	}
	
	
	public abstract String getRankingsUrl(Game game, Group group, Difficulty diff);
	
	
	public static enum RecordingType {
		MASTER, COVER, RE_RECORDED;
		
		public char getAbbrChar() {
			return name().charAt(0);
		}
		
		public static RecordingType smartValueOf(String str) {
			if (str.length() < 1)
				throw new IllegalArgumentException("str must have length >= 1");
			
			char c = Character.toUpperCase(str.charAt(0));
			
			for (RecordingType t : values()) {
				if (t.getAbbrChar() == c) return t;
			}
			
			return null;
		}
	}
	
	public static enum Sorting {
		SCOREHERO(Comparators.SCOREHERO),
		TITLE(Comparators.TITLE),
		ARTIST(Comparators.ARTIST_TITLE),
		GENRE(Comparators.GENRE),
		DECADE(Comparators.DECADE),
		DIFFICULTY,
		LOCATION;
		
		public final Comparator<Song> comparator;
		
		private Sorting() {
			this(null);
		}
		
		private Sorting(Comparator<Song> comp) {
			this.comparator = comp;
		}
		
		public String getShortName() {
			return getText("shortName");
		}
		
		public String getShortDescription() {
			return getText("shortDescription");
		}
		
		private static Text t = null;
		
		public final String getText(String key) {
			if (null == t)
				t = new Text(Sorting.class);
			
			return t.get(name() + "." + key);
		}
	}
	
	public static class Comparators {
		public static final Comparator<Song>
		SCOREHERO = new Comparator<Song>() {
			@Override public int compare(Song o1, Song o2) {
				return o1.getSongOrder().compareTo(o2.getSongOrder());
			}
		},
		
		TITLE = new Comparator<Song>() {
			@Override public int compare(Song o1, Song o2) {
				return o1.getTitle().compareToIgnoreCase(o2.getTitle());
			}
		},
		
		ARTIST_TITLE = new Comparator<Song>() {
			@Override public int compare(Song o1, Song o2) {
				int ret = 0;
				
				if (null == o1.artist && null != o2.artist) {
					ret = -1;
				} else if (null != o1.artist && null == o2.artist) {
					ret = 1;
				} else if (null == o1.artist && null == o2.artist) {
					ret = 0;
				} else {
					ret = o1.artist.compareToIgnoreCase(o2.artist);
					
					if (0 == ret)
						ret = TITLE.compare(o1, o2);
				}
				
				return ret;
			}
		},
		
		GENRE = new Comparator<Song>() {
			@Override public int compare(Song o1, Song o2) {
				int ret = 0;
				
				if (null == o1.genre && null != o2.genre) {
					ret = -1;
				} else if (null != o1.genre && null == o2.genre) {
					ret = 1;
				} else if (null == o1.genre && null == o2.genre) {
					ret = 0;
				} else {
					ret = o1.genre.compareToIgnoreCase(o2.genre);
					
					if (0 == ret)
						ret = ARTIST_TITLE.compare(o1, o2);
				}
				
				return ret;
			}
		},
		
		DECADE = new Comparator<Song>() {
			@Override public int compare(Song o1, Song o2) {
				int ret = 0;
				
				// unknown years come first
				if (0 == o1.year && 0 != o2.year) {
					ret = -1;
				} else if (0 != o1.year && 0 == o2.year) {
					ret = 1;
				} else {
					// multiplying by 10 probably isn't necessary
					int decade1 = o1.year / 10 * 10;
					int decade2 = o2.year / 10 * 10;
					
					ret = decade1 < decade2 ? -1 : decade1 == decade2 ? 0 : 1;
					
					if (0 == ret)
						ret = ARTIST_TITLE.compare(o1, o2);
				}
				
				return ret;
			}
		},
		
		REVERSE_DECADE = new Comparator<Song>() {
			@Override public int compare(Song o1, Song o2) {
				int ret = 0;
				
				// unknown years come first
				if (0 == o1.year && 0 != o2.year) {
					ret = -1;
				} else if (0 != o1.year && 0 == o2.year) {
					ret = 1;
				} else {
					// multiplying by 10 probably isn't necessary
					int decade1 = o1.year / 10 * 10;
					int decade2 = o2.year / 10 * 10;
					
					ret = decade2 < decade1 ? -1 : decade2 == decade1 ? 0 : 1;
					
					if (0 == ret)
						ret = ARTIST_TITLE.compare(o1, o2);
				}
				
				return ret;
			}
		};
	}
}

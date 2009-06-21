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

import java.util.*;

import jshm.util.Text;

public enum Instrument {
	GUITAR("gtr"),
	BASS("bass"), // will also mean rhythm for GH
	WTDRUMS("drums"),
	DRUMS("drums"),
	VOCALS("vocals");
	
	// needed for submitting rb scores
	// TODO move into Text
	private final String shortString; 
	
	private Instrument(final String shortString) {
		this.shortString = shortString;
	}
	
	public javax.swing.ImageIcon getIcon() {
		String s = WTDRUMS == this ? "DRUMS" : name();
		return jshm.gui.GuiUtil.getIcon(
			"instruments/" + s + "_32.png");
	}
	
	public boolean isDrums() {
		switch (this) {
			case WTDRUMS:
			case DRUMS: return true;
		}
		
		return false;
	}
	
	public String toShortString() {
		return shortString;
	}
	
	public String getLongName() {
		return getLongName(false);
	}
	
	public String getLongName(boolean getWtVersion) {
		if (getWtVersion) {
			try {
				return getText("wt.longName");
			} catch (MissingResourceException e) {
			} catch (NullPointerException e) {}
		}
		
		return getText("longName");
	}
	
	private static Text t = null;
	
	public final String getText(String key) {
		if (null == t)
			t = new Text(Instrument.class);
		
		return t.get(name() + "." + key);
	}
	
	public static Instrument smartValueOf(String value) {
		String value2 = value.toUpperCase().replace("_", "");
		
		if ("RBDRUMS".equals(value))
			return DRUMS;
		
		for (Instrument i : values()) {
			if (i.name().startsWith(value2))
				return i;
		}
		
		return null;
	}
	
	public static enum Group {
		GUITAR(1, 1, Instrument.GUITAR),
		BASS(2, 2, Instrument.BASS),
		WTDRUMS(0, 3, Instrument.WTDRUMS),
		DRUMS(3, 4, Instrument.DRUMS),
		VOCALS(4, 5, Instrument.VOCALS),
		
		GUITAR_BASS(5, 6, Instrument.GUITAR, Instrument.BASS),
		GUITAR_WTDRUMS(0, 7, Instrument.GUITAR, Instrument.WTDRUMS),
		GUITAR_DRUMS(6, 8, Instrument.GUITAR, Instrument.DRUMS),
		GUITAR_VOCALS(7, 9, Instrument.GUITAR, Instrument.VOCALS),
		BASS_WTDRUMS(0, 10, Instrument.BASS, Instrument.WTDRUMS),
		BASS_DRUMS(8, 11, Instrument.BASS, Instrument.DRUMS),
		BASS_VOCALS(9, 12, Instrument.BASS, Instrument.VOCALS),
		WTDRUMS_VOCALS(0, 13, Instrument.WTDRUMS, Instrument.VOCALS),
		DRUMS_VOCALS(10, 14, Instrument.DRUMS, Instrument.VOCALS),
		
		GUITAR_BASS_WTDRUMS(0, 15, Instrument.GUITAR, Instrument.BASS, Instrument.DRUMS),
		GUITAR_BASS_DRUMS(11, 16, Instrument.GUITAR, Instrument.BASS, Instrument.DRUMS),
		GUITAR_BASS_VOCALS(12, 17, Instrument.GUITAR, Instrument.BASS, Instrument.VOCALS),
		GUITAR_WTDRUMS_VOCALS(0, 18, Instrument.GUITAR, Instrument.WTDRUMS, Instrument.VOCALS),
		GUITAR_DRUMS_VOCALS(13, 19, Instrument.GUITAR, Instrument.DRUMS, Instrument.VOCALS),
		BASS_WTDRUMS_VOCALS(0, 20, Instrument.BASS, Instrument.WTDRUMS, Instrument.VOCALS),
		BASS_DRUMS_VOCALS(14, 21, Instrument.BASS, Instrument.DRUMS, Instrument.VOCALS),
		
		GUITAR_BASS_WTDRUMS_VOCALS(0, 22, Instrument.GUITAR, Instrument.BASS, Instrument.WTDRUMS, Instrument.VOCALS),
		GUITAR_BASS_DRUMS_VOCALS(15, 23, Instrument.GUITAR, Instrument.BASS, Instrument.DRUMS, Instrument.VOCALS)
		
		;
		
		public final int rockbandId;
		public final int worldTourId;
		public final int size;
		public final Instrument[] instruments;
		
		private Group(final int rockbandId, final int worldTourId, final Instrument ... instruments) {
			this.rockbandId = rockbandId;
			this.worldTourId = worldTourId;
			this.size = instruments.length;
			this.instruments = instruments;
		}
		
		public javax.swing.ImageIcon getIcon() {
			if (size != 1) return null;
			return instruments[0].getIcon();
		}
		
		public String getLongName() {
			return getLongName(false);
		}
		
		public String getLongName(boolean getWtVersion) {
			if (getWtVersion) {
				try {
					return getText("wt.longName");
				} catch (MissingResourceException e) {
				} catch (NullPointerException e) {}
			}
			
			return getText("longName");
		}
		
		public String getWikiUrl() {
			return getWikiUrl(false);
		}
		
		public String getWikiUrl(boolean getWtVersion) {
			if (getWtVersion) {
				try {
					return getText("wt.wikiUrl");
				} catch (MissingResourceException e) {
				} catch (NullPointerException e) {}
			}
			
			return getText("wikiUrl");
		}
		
		public final String getText(String key) {
			if (null == t)
				t = new Text(Instrument.class);
			
			return t.get(name() + "." + key);
		}
		
		
		// not used anywhere
//		/**
//		 * 
//		 * @param id
//		 * @return The group with id of <code>id</code>.
//		 * @throws IllegalArgumentException If no group exists for the given id.
//		 */
//		public static Group getById(final int id) {
//			for (Group g : Group.values())
//				if (g.id == id) return g;
//			
//			throw new IllegalArgumentException("invalid group id: " + id);
//		}
		
		public static List<Group> getBySize(final int size) {
			return getBySize(size, false);
		}
		
		/**
		 * 
		 * @param size
		 * @return A List of Groups that have <code>size</code> number of instruments.
		 * @throws IllegalArgumentException If <code>size</code> < 1.
		 */
		public static List<Group> getBySize(final int size, final boolean includeWtDrums) {
			if (size < 1)
				throw new IllegalArgumentException("size must be >= 1");
			
			List<Group> ret = new ArrayList<Group>();
			
			for (Group g : Group.values()) {
				if (g.size == size) {
					if (0 == g.rockbandId && !includeWtDrums) continue; // backward compat
					ret.add(g);
				}
			}
			
			return ret;
		}
		
		/**
		 * 
		 * @param instruments
		 * @return The Group that has instruments matching those provided in <code>instruments</code>.
		 * @throws IllegalArgumentException If no Group matches <code>instruments</code>.
		 */
		public static Group getByInstruments(final Instrument ... instruments) {
			for (Group g : Group.values()) {
				int foundInstruments = 0;
				
				// for each instrument we require
				for (Instrument i : instruments) {
					// for each instrument the current group has
					for (Instrument i2 : g.instruments) {
						// if the current group has the current required instrument
						if (i == i2) {
							foundInstruments++;
							break;
						}
					}
				}
				
				// we must have found the required instruments
				if (instruments.length == foundInstruments)
					return g;
			}
			
			throw new IllegalArgumentException("invalid instrument list");
		}
	}
}

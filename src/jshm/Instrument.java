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

public enum Instrument {
	GUITAR("gtr"),
	BASS("bass"), // will also mean rhythm for GH
	DRUMS("drums"),
	VOCALS("vocals");
	
	private transient javax.swing.ImageIcon icon = null;
	
	// needed for submitting rb scores
	private final String shortString; 
	
	private Instrument(final String shortString) {
		this.shortString = shortString;
	}
	
	public javax.swing.ImageIcon getIcon() {
		if (null == icon) {
			try {
				icon = new javax.swing.ImageIcon(
					Instrument.class.getResource("/jshm/resources/images/instruments/" + this.toString() + "_32.png"));
			} catch (Exception e) {}
		}
		
		return icon;
	}
	
	public String toShortString() {
		return shortString;
	}
	
	public static enum Group {
		GUITAR(1, Instrument.GUITAR),
		BASS(2, Instrument.BASS),
		DRUMS(3, Instrument.DRUMS),
		VOCALS(4, Instrument.VOCALS),
		
		GUITAR_BASS(5, Instrument.GUITAR, Instrument.BASS),
		GUITAR_DRUMS(6, Instrument.GUITAR, Instrument.DRUMS),
		GUITAR_VOCALS(7, Instrument.GUITAR, Instrument.VOCALS),
		BASS_DRUMS(8, Instrument.BASS, Instrument.DRUMS),
		BASS_VOCALS(9, Instrument.BASS, Instrument.VOCALS),
		DRUMS_VOCALS(10, Instrument.DRUMS, Instrument.VOCALS),
		
		GUITAR_BASS_DRUMS(11, Instrument.GUITAR, Instrument.BASS, Instrument.DRUMS),
		GUITAR_BASS_VOCALS(12, Instrument.GUITAR, Instrument.BASS, Instrument.VOCALS),
		GUITAR_DRUMS_VOCALS(13, Instrument.GUITAR, Instrument.DRUMS, Instrument.VOCALS),
		BASS_DRUMS_VOCALS(14, Instrument.BASS, Instrument.DRUMS, Instrument.VOCALS),
		
		GUITAR_BASS_DRUMS_VOCALS(15, Instrument.GUITAR, Instrument.BASS, Instrument.DRUMS, Instrument.VOCALS)
		
		;
		
		public final int id;
		public final int size;
		public final Instrument[] instruments;
		
		private Group(final int id, final Instrument ... instruments) {
			this.id = id;
			this.size = instruments.length;
			this.instruments = instruments;
		}
		
		public javax.swing.ImageIcon getIcon() {
			if (size != 1) return null;
			return Instrument.valueOf(this.name()).getIcon();
		}
		
		/**
		 * 
		 * @param id
		 * @return The group with id of <code>id</code>.
		 * @throws IllegalArgumentException If no group exists for the given id.
		 */
		public static Group getById(final int id) {
			for (Group g : Group.values())
				if (g.id == id) return g;
			
			throw new IllegalArgumentException("invalid group id: " + id);
		}
		
		/**
		 * 
		 * @param size
		 * @return A List of Groups that have <code>size</code> number of instruments.
		 * @throws IllegalArgumentException If <code>size</code> < 1.
		 */
		public static List<Group> getBySize(final int size) {
			if (size < 1)
				throw new IllegalArgumentException("size must be >= 1");
			
			List<Group> ret = new ArrayList<Group>();
			
			for (Group g : Group.values()) {
				if (g.size == size) ret.add(g);
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

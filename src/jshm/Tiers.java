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

/**
 * Represents a collection of tier headings.
 * Ideally this will be able to handle multiple
 * tiers with different orderings like in RockBand.
 * @author Tim Mullin
 *
 */
public class Tiers {
	public static final Tiers ALPHA_NUM_TIERS;
	
	static {
		String[] strs = new String[27];
		strs[0] = "123/Symbol";
		
		for (char c = 'A'; c <= 'Z'; c++)
			strs[c - 'A' + 1] = Character.toString(c);
		
		ALPHA_NUM_TIERS = new Tiers(strs);
	}
	
	private final String[] tiers;
	
	public Tiers(final String tiers) {
		this(tiers.split("\\|"));
	}
	
	public Tiers(final String[] tiers) {
		this.tiers = tiers;
	}

	/**
	 * 
	 * @param index The <b>1-based</b> index to retreive the name for
	 * @return The name of the tier the given index maps to
	 */
	public String getName(final int index) {
		if (0 == index) return "UNKNOWN";
		return tiers[index - 1];
	}
	
	/**
	 * 
	 * @param name The name of the tier to find the level for
	 * @return The <b>1-based</b> level for the given tier name or 0 if not found
	 */
	public int getLevel(final String name) {
		for (int i = 0; i < tiers.length; i++) {
			if (tiers[i].equals(name)) return i + 1;
		}
		
		return 0;
	}
	
	public int getCount() {
		return tiers.length;
	}
}

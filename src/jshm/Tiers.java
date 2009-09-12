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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import jshm.util.PhpUtil;
import jshm.util.Properties;

/**
 * Represents a collection of tier headings.
 * Ideally this will be able to handle multiple
 * tiers with different orderings like in RockBand.
 * @author Tim Mullin
 *
 */
public class Tiers {
	static final Logger LOG = Logger.getLogger(Tiers.class.getName());
			
	// save/loading stuff
	public static final File propsFile = new File("data/tiers.properties");
	public static final Properties DEFAULTS = new Properties();
	static final Properties p = new Properties(DEFAULTS, false);
	
	static {
		try {
			DEFAULTS.load(
				Config.class.getResourceAsStream("/jshm/properties/tiers.defaults.properties"));
		} catch (Throwable e) {
			LOG.log(Level.SEVERE, "Unable to load default tier properties", e);
			System.exit(-1);
		}
		
		if (propsFile.exists()) {
			try {
				FileInputStream fs = new FileInputStream(propsFile);
				p.load(fs);
			} catch (Throwable e) {
				LOG.log(Level.SEVERE, "Unable to load tier properties", e);
				System.exit(-1);
			}
		}
	}
	
	public static Tiers getTiers(Game game) {
		return
		p.has(game.toString())
		? new Tiers(p.get(game.toString()))
		: null;
	}
	
	public static void setTiers(Game game, Tiers tiers) {
		p.set(game.toString(), tiers.getPacked());
	}
	
	public static void write() {
		LOG.fine("Saving tiers to " + propsFile.getName());

		try {
			FileOutputStream fs = new FileOutputStream(propsFile);
			p.store(fs, "Tier Settings");
		} catch (Throwable t) {
			LOG.log(Level.WARNING, "Error saving tier properties", t);
		}
	}
	
	
	
	public static final Tiers ALPHA_NUM_TIERS;
	
	static {
		String[] strs = new String[27];
		strs[0] = "123";
		
		for (char c = 'A'; c <= 'Z'; c++)
			strs[c - 'A' + 1] = Character.toString(c);
		
		ALPHA_NUM_TIERS = new Tiers(strs);
	}
	
	public final String[] tiers;
	
	public Tiers(final String packed) {
		this(packed.split("\\|"));
	}
	
	public Tiers(final Collection<String> tiers) {
		this(tiers.toArray(new String[0]));
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
	
	/**
	 * 
	 * @return A piped delimited packed form of the tier names
	 */
	public String getPacked() {
		return PhpUtil.implode("|", tiers);
	}
}

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

import jshm.util.Properties;

import java.util.logging.*;
import java.io.*;

public class Config {
	static final Logger LOG = Logger.getLogger(Config.class.getName());
	
	public static final File propsFile = new File("data/JSHManager.properties");
	public static final Properties DEFAULTS = new Properties();
	static final Properties p = new Properties(DEFAULTS);
	
	static {
		try {
			DEFAULTS.load(
				Config.class.getResourceAsStream("/jshm/properties/JSHManager.defaults.properties"));
		} catch (Throwable e) {
			LOG.log(Level.SEVERE, "Unable to load default config properties", e);
			System.exit(-1);
		}
		
		if (propsFile.exists()) {
			try {
				FileInputStream fs = new FileInputStream(propsFile);
				p.load(fs);
			} catch (Throwable e) {
				LOG.log(Level.SEVERE, "Unable to load config properties", e);
				System.exit(-1);
			}
		}
	}

	public static void init() {}
	
	private Config() {}
	
	public static boolean fileExists() {
		return propsFile.exists();
	}
		
	public static boolean has(String propertyName) {
		return p.getProperty(propertyName) != null;
	}
	
	public static int getInt(String propertyName) {
		return p.getInt(propertyName);
	}
	
	public static boolean getBool(String propertyName) {
		return p.getBool(propertyName);
	}
	
	public static long getLong(String propertyName) {
		return p.getLong(propertyName);
	}
	
	public static double getDouble(String propertyName) {
		return p.getDouble(propertyName);
	}
	
	public static String getString(String propertyName) {
		return get(propertyName);
	}
	
	public static String get(String propertyName) {
		return p.get(propertyName);
	}

	public static void set(String propertyName, Object value) {
		p.set(propertyName, value);
	}
	
	/**
	 * Returns an array of elements for config values
	 * stored like, some.list.0, some.list.1, etc.
	 * @param propertyName
	 * @return
	 */
	public static String[] getArray(String propertyName) {
		return p.getArray(propertyName);
	}
	
	/**
	 * Sets values such that propertyName.0 = values[0],
	 * propertyName.1 = values[1], etc.
	 * @param propertyName
	 * @param values
	 */
	public static void setArray(String propertyName, String ... values) {
		p.setArray(propertyName, values);
	}
	
	/**
	 * Removes all properties whose key starts
	 * with any of the supplied keys.
	 * @param removeKeys
	 */
	public static void clearKeys(String ... removeKeys) {
		p.clearKeys(removeKeys);
	}
	
	public static void restoreDefaults(String ... restoreKeys) {
		String[] keys = DEFAULTS.keySet().toArray(new String[] {});
		for (String key : keys) {
			for (String restoreKey : restoreKeys)
				if (key.startsWith(restoreKey))
					p.set(key, DEFAULTS.get(key));
		}
	}
	
	public static void validate() {

	}
	
	public static void write() {
		LOG.fine("Saving configuration to " + propsFile.getName());
		
		validate();

		try {
			FileOutputStream fs = new FileOutputStream(propsFile);
			p.store(fs, "JSHManager Settings");
		} catch (Throwable t) {
			LOG.log(Level.WARNING, "Error saving config properties", t);
		}
	}
}

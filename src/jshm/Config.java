package jshm;

import jshm.util.Properties;

import java.util.*;
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
		if (!propertyName.endsWith("."))
			propertyName += ".";
		
		ArrayList<String> out = new ArrayList<String>();
		
		for (int i = 0; ; i++) {
			try {
				out.add(get(propertyName + i));
			} catch (NullPointerException e) {
				break;
			}
		}
		
		return out.toArray(new String[] {});
	}
	
	/**
	 * Sets values such that propertyName.0 = values[0],
	 * propertyName.1 = values[1], etc.
	 * @param propertyName
	 * @param values
	 */
	public static void setArray(String propertyName, String[] values) {
		if (!propertyName.endsWith("."))
			propertyName += ".";
		
		clearKeys(propertyName);
		
		for (int i = 0; i < values.length; i++) {
			set(propertyName + i, values[i]);
		}
	}
	
	/**
	 * Removes all properties whose key starts
	 * with any of the supplied keys.
	 * @param removeKeys
	 */
	public static void clearKeys(String ... removeKeys) {
		String[] keys = p.keySet().toArray(new String[] {});
		for (String key : keys) {
			for (String removeKey : removeKeys)
				if (key.startsWith(removeKey))
					p.remove(key);
		}
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

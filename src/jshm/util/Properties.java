package jshm.util;

public class Properties extends java.util.Properties {
	public Properties() {
		super();
	}
	
	public Properties(Properties defaults) {
		super(defaults);
	}
	
	public boolean has(String key) {
		return null != getProperty(key);
	}
	
	public int getInt(String propertyName) {
		int defaultValue = 0;
		
		try {
			defaultValue = 
				Integer.parseInt(get(propertyName));
		} catch (NumberFormatException e) {}
		
		return defaultValue;
	}
	
	public boolean getBool(String propertyName) {
		boolean defaultValue = false;
		
		defaultValue =
			Boolean.parseBoolean(get(propertyName));
		
		return defaultValue;
	}
	
	public long getLong(String propertyName) {
		long defaultValue = 0L;
		
		try {
			defaultValue =
				Long.parseLong(get(propertyName));
		} catch (NumberFormatException e) {}
		
		return defaultValue;
	}
	
	public double getDouble(String propertyName) {
		double defaultValue = 0.0;
		
		try {
			defaultValue =
				Double.parseDouble(get(propertyName));
		} catch (NumberFormatException e) {}
		
		return defaultValue;
	}
	
	public String get(String propertyName) {
		propertyName = propertyName.toLowerCase();
		
		if (!has(propertyName))
			throw new NullPointerException("Key not found: " + propertyName);
		
		return getProperty(propertyName);
	}

	public void set(String propertyName, Object value) {
		setProperty(propertyName, value.toString());
	}
}

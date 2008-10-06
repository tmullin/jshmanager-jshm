package jshm.util;

import java.util.Enumeration;
import java.util.ResourceBundle;

/**
 * This class wraps around a {@link ResourceBundle} to provide
 * easier usage.
 * @author Tim Mullin
 *
 */
public class Text {
	public static final String RESOURCE_BASE = "jshm.resources.text.";
	
//	Class<?> clazz;
	ResourceBundle rb;
	
	public Text(Class<?> clazz) {
//		this.clazz = clazz;
		rb = ResourceBundle.getBundle(RESOURCE_BASE + clazz.getSimpleName());
	}
	
	public String get(String key) {
		return rb.getString(key);
	}
	
	public String[] getArr(String key) {
		return rb.getStringArray(key);
	}
	
	public Object getObj(String key) {
		return rb.getObject(key);
	}
	
	public Enumeration<String> getKeys() {
		return rb.getKeys();
	}
}

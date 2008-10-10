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

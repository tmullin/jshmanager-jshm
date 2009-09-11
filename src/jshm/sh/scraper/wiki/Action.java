/*
 * -----LICENSE START-----
 * JSHManager - A Java-based tool for managing one's ScoreHero account.
 * Copyright (C) 2008, 2009 Tim Mullin
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
package jshm.sh.scraper.wiki;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a single wiki action. Ex:
 * <pre>
 * {{songinfo
 *     genre="Metal"
 *     album="City of Evil"
 *     year="2005"
 *     artist="Avenged Sevenfold"}}
 * </pre>
 * @author Tim Mullin
 *
 */
public class Action {
	public String name;
	public final Map<String, String> args = new HashMap<String, String>();
	
	public Action() {
		this(null);
	}
	
	public Action(String name) {
		this.name = name;
	}
	
	public String set(String key, String value) {
		return args.put(key, value);
	}
	
	public String get(String key) {
		return args.get(key);
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder("{{");
		sb.append(name);
		
		for (String key : args.keySet()) {
			sb.append('\n');
			sb.append("    ");
			sb.append(key);
			sb.append('=');
			sb.append('"');
			sb.append(args.get(key));
			sb.append('"');
		}
		
		sb.append("}}");
		
		return sb.toString();
	}
}

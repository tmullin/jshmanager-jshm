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
package jshm.sh;

import java.util.logging.Logger;

public class Wiki {
	static final Logger LOG = Logger.getLogger(Wiki.class.getName());
	
	public static String wikiize(String str) {
		StringBuilder sb = new StringBuilder();
		String[] parts = str
			.replace("&", "And")
			.replaceAll("[^\\p{L}\\p{N}\\s]+", "")
			.split("\\s+");
		
		for (String s : parts) {
			sb.append(Character.toUpperCase(s.charAt(0)));
			sb.append(s.substring(1));
		}
		
		String ret = sb.toString();
		LOG.finest(String.format("wikiized \"%s\" to \"%s\"", str, ret));
		return ret;
	}
}

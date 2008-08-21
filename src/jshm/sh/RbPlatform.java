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

import java.util.*;

import jshm.Platform;

/**
 * This maps a {@link jshm.Platform} to the specifc id
 * that ScoreHero uses for that platform for Rockband
 * games. 
 * @author Tim Mullin
 *
 */
public class RbPlatform {
	private static final Map<Platform, RbPlatform> map =
		new HashMap<Platform, RbPlatform>();
	
	static {
		map.put(Platform.PS2, new RbPlatform(1));
		map.put(Platform.XBOX360, new RbPlatform(2));
		map.put(Platform.PS3, new RbPlatform(3));
		map.put(Platform.WII, new RbPlatform(4));
	}
	
	public static int getId(Platform platform) {
		if (!map.containsKey(platform))
			throw new IllegalArgumentException("invalid platform: " + platform);
		return map.get(platform).id;
	}
	
	public final int id;
	
	private RbPlatform(final int id) {
		this.id = id;
	}
}

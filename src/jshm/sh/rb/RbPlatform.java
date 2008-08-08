package jshm.sh.rb;

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

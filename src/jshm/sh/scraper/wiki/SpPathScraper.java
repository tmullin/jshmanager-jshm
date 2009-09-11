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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jshm.Difficulty;
import jshm.Song;
import jshm.Instrument.Group;
import jshm.exceptions.ScraperException;
import jshm.sh.URLs;

/**
 * This gets text descriptions of paths added to the wiki with
 * the {{path}} action.
 * @author Tim Mullin
 *
 */
public class SpPathScraper {
	private static Map<String, List<PathInfo>> cache = null;
	
	public static List<PathInfo> scrape(Song song, Group group, Difficulty diff) throws IOException, ScraperException {
		return scrape(true, song, group, diff);
	}
	
	public static List<PathInfo> scrape(boolean useCache, Song song, Group group, Difficulty diff) throws IOException, ScraperException {
		final String url = URLs.wiki.getSongSpUrl(song, group, diff); 
		
		if (useCache && null != cache && null != cache.get(url))
			return cache.get(url);
		
		Map<String, List<Action>> actionMap =
			ActionsScraper.scrape(url);
		
		List<PathInfo> ret = null;
		
		if (null != actionMap.get("path")) {
			ret = new ArrayList<PathInfo>();
			
			for (Action a : actionMap.get("path")) {
				if (a.args.isEmpty())
					continue;
				
				PathInfo pi = new PathInfo();
				
				pi.label = a.get("label");
				try { pi.score = Integer.parseInt(a.get("score")); }
				catch (NumberFormatException e) {}
				pi.shortDescription = a.get("short");
				pi.notes = a.get("notes");
				pi.image = a.get("image");
				pi.video = a.get("video");
				pi.credit = a.get("credit");
				
				ret.add(pi);
			}
			
			if (null == cache)
				cache = new HashMap<String, List<PathInfo>>();
			
			cache.put(url, ret);
		}
		
		return ret;
	}
	
	public static class PathInfo {
		// yes i see the irony of calling short description "shortDescription"
		public String label, shortDescription, notes, credit, image, video;
		public int score = 0;
		
		private PathInfo() {}
		
		public String toString() {
			StringBuilder sb = new StringBuilder();
			
			if (null != label) {
				sb.append(label);
			}
			
			if (0 != score) {
				if (null != label)
					sb.append(" - ");
				sb.append("Est. Score: ");
				sb.append(String.valueOf(score));
			}
			
			if (null != label || 0 != score)
				sb.append('\n');
			
			sb.append(shortDescription);
			
			if (null != notes) {
				sb.append("\n");
				sb.append(notes);
			}
			
			if (null != image) {
				sb.append("\nImage: ");
				sb.append(image);
			}
			
			if (null != video) {
				sb.append("\nVideo: ");
				sb.append(video);
			}
			
			if (null != credit) {
				sb.append("\nCredit: ");
				sb.append(credit);
			}
			
			return sb.toString();
		}
	}
}

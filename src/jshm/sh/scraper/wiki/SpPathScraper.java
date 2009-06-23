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
		final String cacheKey = String.format("%s_%s_%s",
			song.getScoreHeroId(), group, diff); 
		
		if (useCache && null != cache && null != cache.get(cacheKey))
			return cache.get(cacheKey);
		
		Map<String, List<Action>> actionMap =
			ActionsScraper.scrape(
				URLs.wiki.getSongSpUrl(song, group, diff));
		
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
			
			cache.put(cacheKey, ret);
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

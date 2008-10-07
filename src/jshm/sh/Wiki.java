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

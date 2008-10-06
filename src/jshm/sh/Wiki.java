package jshm.sh;

public class Wiki {
	public static String wikiize(String str) {
		StringBuilder sb = new StringBuilder();
		String[] parts = str
			.replace("&", "And")
			.replaceAll("[^\\w\\s]+", "")
			.split("\\s+");
		
		for (String s : parts) {
			sb.append(Character.toUpperCase(s.charAt(0)));
			sb.append(s.substring(1));
		}
		
		return sb.toString();
	}
}

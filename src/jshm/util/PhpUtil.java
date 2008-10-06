package jshm.util;

/**
 * This class contains Java equivalents of various PHP functions.
 * @author Tim Mullin
 *
 */
public class PhpUtil {
	/**
	 * Simple linear search of an array (array_search).
	 * @param <T>
	 * @param needle
	 * @param haystack
	 * @return
	 */
	public static <T> boolean contains(Object needle, T ... haystack) {
		for (T t : haystack) {
			if (t.equals(needle)) return true;
		}

		return false;
	}

	public static <T> String implode(T ... pieces) {
		return implode(",", pieces);
	}

	public static <T> String implode(String glue, T ... pieces) {
		if (pieces.length == 0) return "";
		
		StringBuilder sb = new StringBuilder(pieces[0].toString());
		
		for (int i = 1; i < pieces.length; i++) {
			sb.append(glue);
			sb.append(pieces[i]);
		}
		
		return sb.toString();
	}

	public static String ltrim(String str) {
		return ltrim(str, null);
	}
	
	public static String ltrim(String str, String charList) {
		int start = 0,
			len = str.length();
		
		if (null == charList)
			for (int i = 0; i < len; i++)
				if (Character.isWhitespace(str.charAt(i)))
					start++;
				else
					break;
		else
			for (int i = 0; i < len; i++)
				if (charList.indexOf(str.charAt(i)) >= 0)
					start++;
				else
					break;
		
		return str.substring(start);
	}
	
	public static String rtrim(String str) {
		return rtrim(str, null);
	}
	
	public static String rtrim(String str, String charList) {
		int end = str.length() - 1;
		
		if (null == charList)
			for (int i = end; i >= 0; i--)
				if (Character.isWhitespace(str.charAt(i)))
					end--;
				else
					break;
		else
			for (int i = end; i >= 0; i--)
				if (charList.indexOf(str.charAt(i)) >= 0)
					end--;
				else
					break;
		
		return str.substring(0, end + 1);
	}
	
	public static String trim(String str) {
		return trim(str, null);
	}
	
	public static String trim(String str, String charList) {
		return ltrim(rtrim(str, charList), charList);
	}
}

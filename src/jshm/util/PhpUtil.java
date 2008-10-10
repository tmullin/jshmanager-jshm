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

import java.util.List;

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

	public static String implode(List<?> pieces) {
		return implode(pieces.toArray());
	}
	
	public static <T> String implode(T ... pieces) {
		return implode(",", pieces);
	}

	public static String implode(String glue, List<?> pieces) {
		return implode(glue, pieces.toArray());
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

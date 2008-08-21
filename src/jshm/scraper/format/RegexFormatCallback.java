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
package jshm.scraper.format;

import java.util.regex.*;

public class RegexFormatCallback implements NodeFormatCallback {
	protected Pattern pattern;
	protected String replacement;
	protected int group;
	
	public RegexFormatCallback(final String regex) {
		this(regex, null);
	}
	
	public RegexFormatCallback(final String regex, final String replacement) {
		this(regex, replacement, -1);
	}
	
	public RegexFormatCallback(final String regex, final int group) {
		this(regex, null, group);
	}
	
	public RegexFormatCallback(final String regex, final String replacement, int group) {
		if (group < -1)
			throw new IllegalArgumentException("Group must be >= -1, given: " + group);
		
		this.pattern = Pattern.compile(regex);
		this.replacement = null != replacement ? replacement : "";
		this.group = group;
	}

	public String format(final String text) {
		Matcher m = pattern.matcher(text);
		
		if (-1 == group) {
			return m.replaceAll(replacement);
		} else {
			return m.matches() ? m.group(group) : replacement;
		}
	}

}

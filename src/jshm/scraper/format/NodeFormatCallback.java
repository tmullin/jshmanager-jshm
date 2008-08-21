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

/**
 * This specifies an interface for what you would
 * like to do with the text retrieved from a node, such
 * as removing commas from what would otherwise be a
 * numeric string.
 * @author Tim
 *
 */
public interface NodeFormatCallback {
	/**
	 * Use to modify the input string as desired for
	 * the given situation.
	 * @param text The text to modify. Guaranteed not null.
	 * @return
	 */
	public String format(String text);
}

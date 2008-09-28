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
package jshm.csv;

import jshm.exceptions.CsvException;

public enum CsvColumn {
	SONG,
	SCORE,
	RATING,
	PERCENT,
	STREAK,
	COMMENT,
	DIFFICULTY,
	INSTRUMENT,
	IGNORE;
	
	public static CsvColumn smartValueOf(final String value) throws CsvException {
		if (value.length() < 2)
			return null;
//			throw new CsvException("value too short to determine column type: " + value);
		
		String value2 = value.trim().toUpperCase();
		
		if (value2.equals("PCT")) return PERCENT;
		
		for (CsvColumn c : values()) {
			if (c.name().startsWith(value2))
				return c;
		}
		
		return null;
	}
}

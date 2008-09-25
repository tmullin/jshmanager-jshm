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

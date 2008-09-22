package jshm.csv;

import jshm.exceptions.CsvException;

public enum CsvColumn {
	IGNORE,
	DIFFICULTY,
	SONG,
	SCORE,
	RATING,
	PERCENT,
	STREAK,
	COMMENT,
	INSTRUMENT;
	
	public static CsvColumn smartValueOf(final String value) throws CsvException {
		if (value.length() < 2)
			throw new CsvException("value too short to determine column type: " + value);
		
		String value2 = value.toUpperCase();
		
		for (CsvColumn c : values()) {
			if (c.name().startsWith(value2))
				return c;
		}
		
		throw new CsvException("no column found that matches " + value);
	}
}

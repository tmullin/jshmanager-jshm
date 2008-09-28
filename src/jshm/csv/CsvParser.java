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

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import au.com.bytecode.opencsv.CSVReader;

import jshm.*;
import jshm.Instrument.Group;
import jshm.exceptions.CsvException;
import jshm.util.Util;

public class CsvParser {
	public static final CsvColumn[] REQUIRED_COLUMNS = new CsvColumn[] {
		CsvColumn.SONG, CsvColumn.SCORE
	};
	
	static final Logger LOG = Logger.getLogger(CsvParser.class.getName());
	
	public static List<Score> parse(final File csvFile, CsvColumn[] columns, final Game game, Group group, Difficulty diff) throws Exception {
		return parse(null, csvFile, columns, game, group, diff);
	}
	
	public static List<Score> parse(final List<String> summary, final File csvFile, CsvColumn[] columns, final Game game, Group group, Difficulty diff) throws Exception {
		CSVReader in = null;
		
		try {
			in = new CSVReader(new FileReader(csvFile));
			
			List<Score> scores = new ArrayList<Score>(); 
			String[] line = null;
			
			// try to guess the columns ourself
			if (null == columns) {
				LOG.fine("going to guess column names");
				line = in.readNext();
				
				if (null == line)
					throw new CsvException("tried to guess columns but csvFile didn't have any data");
				
				columns = new CsvColumn[line.length];
				
				for (int i = 0; i < columns.length; i++) {
					columns[i] = CsvColumn.smartValueOf(line[i]);
					if (null == columns[i])
						columns[i] = CsvColumn.IGNORE;
				}
			}
			
			LOG.fine("Using columns: " + Util.implode(columns));
			
			Map<CsvColumn, Integer> columnMap = new HashMap<CsvColumn, Integer>();
			
			// this could map IGNORE several times but it doesn't matter since
			// it is IGNORED, heh heh....
			for (int i = 0; i < columns.length; i++) {
				columnMap.put(columns[i], i);
			}
			
			for (CsvColumn c : REQUIRED_COLUMNS) {
				if (null == columnMap.get(c))
					throw new CsvException("Required column not found: " + c);
			}
			
			Song curSong = null;
			Group curGroup = group;
			Difficulty curDiff = diff;
			
			int lineNumber = 0;
			
			while (null != (line = in.readNext())) {
				lineNumber++;
				
				if (line.length == 0 ||
					(line.length == 1 && line[0].trim().isEmpty())) {
					LOG.finest("Skipped line " + lineNumber + " (empty)");
					continue;
				}
				
				final String implodedLine = Util.implode(line);
				
				LOG.fine("Parsing line " + lineNumber + " (" + line.length + " elements): " + implodedLine);
				
				// get song
				Integer col = columnMap.get(CsvColumn.SONG);
				
				if (null == col) {
					throw new CsvException("No song column found on line " + lineNumber);
				}
				
				String songStr = line[col].trim();
				
				if (!songStr.isEmpty()) {
					List<? extends Song> songs = game.getAllSongsByTitle(songStr, curDiff);
					
SongCheckSwitch:
					switch (songs.size()) {
						case 1:
							curSong = songs.get(0);
							break;
							
						case 0:
							String s = "Skipped line " + lineNumber + ", song not found: " + songStr;
							LOG.warning(s);
							if (null != summary) summary.add(s);
							continue;
							
						default:
							StringBuilder sb = new StringBuilder("Skipped line ");
							sb.append(lineNumber);
							sb.append(", multiple songs matched \"");
							sb.append(songStr);
							sb.append("\": ");
						
							for (Song ss : songs) {
								sb.append(ss.getTitle());
								sb.append(", ");
								
								if (ss.getTitle().equalsIgnoreCase(songStr)) {
									curSong = ss;
									break SongCheckSwitch;
								}
							}
							
							// -2 because of adding ", " after each song
							String s1 = sb.substring(0, sb.length() - 2);
							LOG.warning(s1);
							if (null != summary) summary.add(s1);
							continue;
					}
				}
				
				if (null == curSong) {
					String s = String.format("Skipped line %s, no song specified", lineNumber);
					LOG.warning(s);
					if (null != summary) summary.add(s);
					continue;
				}
				
				
				// get score
				col = columnMap.get(CsvColumn.SCORE);
				
				if (null == col)
					throw new CsvException("No score column found on line " + lineNumber);
				
				String scoreStr = line[col].trim();
				
				int curScore = 0;
				
				try {
					curScore = Integer.parseInt(scoreStr);
					
					if (curScore < 0)
						throw new NumberFormatException("Invalid score value: " + curScore);
				} catch (NumberFormatException e) {
					String s = String.format("Skipped line %s, invalid score: \"%s\"", lineNumber, scoreStr); 
					LOG.log(Level.WARNING, s, e);
					if (null != summary) summary.add(s);
					continue;
				}
				
				
				// get rating
				col = columnMap.get(CsvColumn.RATING);
				int curRating = 0;
				
				if (null != col && col < line.length) {
					String str = line[col].trim();
					
					if (!str.isEmpty()) {
						try {
							curRating = Integer.parseInt(str);
							if (!game.title.isValidRating(curRating))
								throw new NumberFormatException("Invalid rating value: " + curRating);
						} catch (NumberFormatException e) {
							String s = String.format("Skipped line %s, invalid rating: \"%s\"", lineNumber, str);
							LOG.log(Level.WARNING, s, e);
							if (null != summary) summary.add(s);
							continue;
						}
					}
				}
				
				
				// get percent
				col = columnMap.get(CsvColumn.PERCENT);
				float curPercent = 0.0f;
				
				if (null != col && col < line.length) {
					String str = line[col].trim();
					
					if (!str.isEmpty()) {
						try {
							int i = Integer.parseInt(str.replaceAll("[^\\d]+", ""));
							curPercent = ((float) i) / 100.0f;
							
							if (!(0.0f <= curPercent && curPercent <= 1.0f))
								throw new NumberFormatException("Invalid percent value: " + curPercent);
						} catch (NumberFormatException e) {
							String s = String.format("Skipped line %s, invalid percent: \"%s\"", lineNumber, str);
							LOG.log(Level.WARNING, s, e);
							if (null != summary) summary.add(s);
							continue;
						}
					}
				}
				
				
				// get streak
				col = columnMap.get(CsvColumn.STREAK);
				int curStreak = 0;
				
				if (null != col && col < line.length) {
					String str = line[col].trim();
					
					if (!str.isEmpty()) {
						try {
							curStreak = Integer.parseInt(str);
							if (curStreak < 0)
								throw new NumberFormatException("invalid streak value: " + curStreak);
						} catch (NumberFormatException e) {
							String s = String.format("Skipped line %s, invalid streak: \"%s\"", lineNumber, str);
							LOG.log(Level.WARNING, s, e);
							if (null != summary) summary.add(s);
							continue;
						}
					}
				}
				
				
				// get comment
				col = columnMap.get(CsvColumn.COMMENT);
				String curComment = "";
				
				if (null != col && col < line.length) {
					String str = line[col].trim();
					curComment = str;
				}
				
				
				// get diff
				col = columnMap.get(CsvColumn.DIFFICULTY);
				
				if (null != col && col < line.length) {
					String diffStr = line[col].trim();
					
					if (!diffStr.isEmpty()) {
						Difficulty newDiff = Difficulty.smartValueOf(diffStr);
						
						if (null == newDiff) {
							String s = String.format("Skipped line %s, invalid difficulty: \"%s\"", lineNumber, diffStr);
							LOG.warning(s);
							if (null != summary) summary.add(s);
							continue;
						}
						
						curDiff = newDiff;
					}
				}
				
				
				// get instrument
				col = columnMap.get(CsvColumn.INSTRUMENT);
				
				if (null != col && col < line.length) {
					String instrumentStr = line[col].trim();
					
					if (!instrumentStr.isEmpty()) {
						Instrument newInstrument = Instrument.smartValueOf(instrumentStr);
						
						if (null == newInstrument) {
							String s = String.format("Skipped line %s, invalid instrument: \"%s\"", lineNumber, instrumentStr);
							LOG.warning(s);
							if (null != summary) summary.add(s);
							continue;
						}
						
						curGroup = Group.getByInstruments(newInstrument);
					}
				}
				
				LOG.finer(
					String.format("Parsed line %s: {%s}, %s, %s, %s, %s, %s, %s, %s",
						lineNumber, curSong, curScore, curRating, curPercent, curStreak, curComment, curDiff, curGroup));
				
				Score score = game.createNewScore(curSong, curGroup, curDiff, curScore, curRating, curPercent, curStreak, curComment);
				score.setStatus(Score.Status.NEW);
				
				scores.add(score);
			}
			
			return scores;
		} finally {
			if (null != in)
				in.close();
		}
	}
}

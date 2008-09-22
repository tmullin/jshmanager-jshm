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
	static final Logger LOG = Logger.getLogger(CsvParser.class.getName());
	
	public static List<Score> parse(final File csvFile, CsvColumn[] columns, final Game game, Group group, Difficulty diff) throws Exception {
		CSVReader in = null;
		
		in = new CSVReader(new FileReader(csvFile));
		
		List<Score> scores = new ArrayList<Score>(); 
		String[] line = null;
		
		// try to guess the columns ourself
		if (null == columns) {
			LOG.info("going to guess column names");
			line = in.readNext();
			
			if (null == line)
				throw new CsvException("tried to guess columns but csvFile didn't have any data");
			
			columns = new CsvColumn[line.length];
			
			for (int i = 0; i < columns.length; i++) {
				columns[i] = CsvColumn.smartValueOf(line[i]);
			}
		}
		
		LOG.info("using columns: " + Util.implode((Object[]) columns));
		
		Map<CsvColumn, Integer> columnMap = new HashMap<CsvColumn, Integer>();
		
		for (int i = 0; i < columns.length; i++) {
			columnMap.put(columns[i], i);
		}
		
		Song curSong = null;
		Group curGroup = group;
		Difficulty curDiff = diff;
		
		while (null != (line = in.readNext())) {
			if (line.length == 0 ||
				(line.length == 1 && line[0].trim().isEmpty())) {
				LOG.finest("skipping empty line");
				continue;
			}
			
			final String implodedLine = Util.implode((Object[]) line);
			
			LOG.info("Parsing line (" + line.length + " elements): " + implodedLine);
			
			// get song
			Integer col = columnMap.get(CsvColumn.SONG);
			
			if (null == col) {
				throw new CsvException("no song column found");
			}
			
			String songStr = line[col].trim();
			
			if (!songStr.isEmpty())
				curSong = game.getSongByTitle(songStr, curDiff);
			
			if (null == curSong) {
				LOG.warning("curSong == null, skipping line -> " + implodedLine);
				continue;
			}
			
			
			// get score
			col = columnMap.get(CsvColumn.SCORE);
			
			if (null == col)
				throw new CsvException("no score column found");
			
			String scoreStr = line[col].trim();
			
			int curScore = 0;
			
			try {
				curScore = Integer.parseInt(scoreStr);
				
				if (curScore < 0)
					throw new NumberFormatException("invalid score value: " + curScore);
			} catch (NumberFormatException e) {
				LOG.log(Level.WARNING,
					String.format("invalid score \"%s\", skipping line -> %s", scoreStr, implodedLine), e);
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
							throw new NumberFormatException("invalid rating value: " + curRating);
					} catch (NumberFormatException e) {
						LOG.log(Level.WARNING,
							String.format("Invalid rating \"%s\", skipping line -> %s", str, implodedLine), e);
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
						int i = Integer.parseInt(str);
						curPercent = ((float) i) / 100.0f;
						
						if (!(0.0f <= curPercent && curPercent <= 1.0f))
							throw new NumberFormatException("invalid percent value: " + curPercent);
					} catch (NumberFormatException e) {
						LOG.log(Level.WARNING,
							String.format("Invalid percent \"%s\", skipping line -> %s", str, implodedLine), e);
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
						LOG.log(Level.WARNING,
							String.format("Invalid streak \"%s\", skipping line -> %s", str, implodedLine), e);
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
						LOG.warning(
							String.format("invalid difficulty \"%s\", skipping line -> %s", diffStr, implodedLine));
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
						LOG.warning(
							String.format("invalid instrument \"%s\", skipping line -> %s", instrumentStr, implodedLine));
						continue;
					}
					
					curGroup = Group.getByInstruments(newInstrument);
				}
			}
			
			LOG.finer(
				String.format("Parsed: {%s}, %s, %s, %s, %s, %s, %s, %s",
					curSong, curScore, curRating, curPercent, curStreak, curComment, curDiff, curGroup));
			
			scores.add(
				game.createNewScore(curSong, curGroup, curDiff, curScore, curRating, curPercent, curStreak, curComment));
		}
		
		return scores;
	}
}

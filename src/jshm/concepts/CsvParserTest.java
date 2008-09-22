package jshm.concepts;

import java.io.File;
import java.util.List;

import jshm.Difficulty;
import jshm.Score;
import jshm.Instrument.Group;
import jshm.csv.CsvParser;
import jshm.logging.Log;
import jshm.rb.RbGame;

public class CsvParserTest {
	public static void main(String[] args) throws Exception {
		Log.configTestLogging();
		
		File f = new File("scores.csv");
		
		List<Score> scores = 
			CsvParser.parse(f, null,
				RbGame.RB2_XBOX360,
				Group.VOCALS,
				Difficulty.EXPERT);
		
		for (Score s : scores)
			System.out.println(s);
	}
}

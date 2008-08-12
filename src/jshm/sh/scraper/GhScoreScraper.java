package jshm.sh.scraper;

import java.util.*;

import org.htmlparser.util.NodeList;

import jshm.Difficulty;
import jshm.Instrument;
import jshm.Part;
import jshm.Score;
import jshm.exceptions.ScraperException;
import jshm.gh.*;
import jshm.scraper.Scraper;
import jshm.scraper.TieredTabularDataAdapter;
import jshm.scraper.TieredTabularDataExtractor;
import jshm.scraper.TieredTabularDataHandler;
import jshm.sh.DataTable;
import jshm.sh.DateFormats;
import jshm.sh.URLs;

public class GhScoreScraper {
	static {
		Formats.init();
	}
	
	public static List<GhScore> scrapeSummaries(
		final GhGame game, final Difficulty difficulty)
	throws ScraperException {
		if (Difficulty.CO_OP == difficulty)
			throw new IllegalArgumentException("co-op is not yet supported");
		
		List<GhScore> scores = new ArrayList<GhScore>();
		
		TieredTabularDataHandler handler =
			new SummariesHandler(difficulty, scores);
		
		NodeList nodes = Scraper.scrape(
			URLs.gh.getManageScoresUrl(game, difficulty),
			handler.getDataTable(), jshm.sh.Client.getAuthCookies());
		
		if (nodes.size() == 0)
			throw new ScraperException("nodes.size() == 0, invalid page format?");
		
//		System.out.println(nodes.toHtml());

		TieredTabularDataExtractor.extract(nodes, handler);
		
		return scores;
	}
	
	private static class SummariesHandler extends TieredTabularDataAdapter {
		private final Difficulty difficulty;
		private final List<GhScore> scores;
		
		public SummariesHandler(
			final Difficulty difficulty,
			final List<GhScore> scores) {
			
			this.difficulty = difficulty;
			this.scores = scores;
		}
		
		@Override
		public DataTable getDataTable() {
			return DataTable.GH_MANAGE_SCORES;
		}
		
		// "-|-|link=songid~text|-|-|text=int|img=rating~text=float|text=int|text=int|text|text"
		@Override
		public void handleDataRow(String[][] data) throws ScraperException {
			GhSong song =
				GhSong.getByScoreHeroId(
					Integer.parseInt(data[2][0]));
			
			if (null == song)
				throw new ScraperException("GhSong not found, scoreHeroId: " + data[2][0]);
			
			GhScore score = new GhScore();
			score.setStatus(Score.Status.SUMMARY);
			score.setSong(song);
			
			score.setScore(Integer.parseInt(data[5][0]));
			
			if (data[6].length == 2 && !data[6][1].isEmpty()) {
				// there is a calculated rating available
				float f = Float.parseFloat(data[6][1]);
				score.setCalculatedRating(f);
				score.setRating(Math.min(5, (int) Math.floor(f)));
			} else if (!data[6][0].isEmpty()) {
				// assuming SH doesn't know the cuttoffs
				// and the user put in his rating himself
				// we have to infer from the image
				int i = Integer.parseInt(data[6][0]);
				score.setRating(i);
			} else {
				// don't know the rating
				score.setRating(0);
			}
			
			Part p = new Part();
			p.setDifficulty(difficulty);
			p.setInstrument(Instrument.GUITAR);
			
			if (!data[7][0].isEmpty()) {
				p.setHitPercent(
					Integer.parseInt(data[7][0]) / 100.0f);
			}
			
			if (!data[8][0].isEmpty()) {
				p.setStreak(
					Integer.parseInt(data[8][0]));
				score.setStreak(p.getStreak());
			}
			
			score.addPart(p);
			
			score.setCreationDate(null);
			
			// null date is fine
//			if (!data[9][0].isEmpty()) {
				try {
					Date date = DateFormats.GH_MANAGE_SCORES.parse(data[9][0]);
					score.setSubmissionDate(date);
				} catch (Exception e) {}
//			}
			
			if (!data[10][0].isEmpty() && !"N/A".equals(data[10][0])) {
				score.setComment(data[10][0]);
			}
			
			scores.add(score);
		}
	}
}

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
package jshm.sh.scraper;

import java.util.*;
import java.util.logging.Logger;

import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.netbeans.spi.wizard.ResultProgressHandle;

import jshm.*;
import jshm.exceptions.ScraperException;
import jshm.gh.*;
import jshm.rb.*;
import jshm.scraper.*;
import jshm.sh.DateFormats;
import jshm.sh.URLs;

public class RbScoreScraper {
	static final Logger LOG = Logger.getLogger(RbScoreScraper.class.getName());
	
	static {
		Formats.init();
	}
	
	public static List<RbScore> scrapeAll(
			final RbGame game, final Instrument.Group group, final Difficulty difficulty)
		throws ScraperException, ParserException {
		return scrapeAll(null, game, group, difficulty);
	}
	
	/**
	 * This should retrieve <i>all</i> submitted scores for
	 * the given game/difficulty. This will require one http
	 * requests per song.
	 * @param game
	 * @param difficulty
	 * @return
	 * @throws ScraperException
	 * @throws ParserException 
	 */
	public static List<RbScore> scrapeAll(
			final ResultProgressHandle progress, final RbGame game, final Instrument.Group group, final Difficulty difficulty)
		throws ScraperException, ParserException {
		if (Difficulty.CO_OP == difficulty)
			throw new IllegalArgumentException("invalid rockband difficulty");
		
		if (null != progress) progress.setBusy("Downloading all scores");
		LOG.info("Downloading all scores for " + game + " on " + difficulty);
		
		List<Integer> scoreCounts = new ArrayList<Integer>();
		List<RbScore> scores = scrapeLatest(game, group, difficulty, scoreCounts);
		
		LOG.fine("scrapeLatest() returned " + scores.size() + " scores");
		LOG.finest("scoreCounts.size() = " + scoreCounts.size());
		
		List<RbScore> ret =  new ArrayList<RbScore>();
		
		int i = 0;
		final int totalSongs = scores.size();
		for (RbScore s : scores) {
			if (null != progress)
				progress.setProgress(
					String.format("Downloading %s of %s", i + 1, totalSongs),
					i, totalSongs);
			
			// s is the latest score for a given song
			// it will get added regardless
			ret.add(s);
			
			LOG.finer("Adding score: " + s);
			
			if (scoreCounts.get(i) > 1) {
				try {
					Thread.sleep(150);
					
					// try not to hammer SH too badly
					if (i % 5 == 4) {
						LOG.fine("Sleeping so we don't spam SH");
						Thread.sleep(2000);
					}
				} catch (InterruptedException e) {}
				
				// there is more than 1 score submitted for this song
				// so retrieve the other scores
				List<RbScore> moreScores = scrapeScoresForSong((RbSong) s.getSong(), group, difficulty);
				
				LOG.fine("scrapeScoresForSong() returned " + moreScores.size() + " scores");
				
				Iterator<RbScore> it = moreScores.iterator();
				
				while (it.hasNext()) {
					RbScore cur = it.next();
					
					// scrapeScoresForSong() contains s plus some other
					// scores so we don't want to add s again
					if (cur.equals(s)) {
						LOG.fine("Skipping score (cur==s): " + cur);
						it.remove();
					} else {
						LOG.finer("Adding score: " + cur);
						// a new score that isn't s so add it
						ret.add(cur);
					}
				}
			}
			
			i++;
		}
		
		return ret;
	}
	
	public static List<RbScore> scrapeLatest(
			final RbGame game, final Instrument.Group group, final Difficulty difficulty) throws ScraperException, ParserException {
		return scrapeLatest(game, group, difficulty, null);
	}
	
	public static List<RbScore> scrapeLatest(
			final RbGame game, final Instrument.Group group, final Difficulty difficulty, final List<Integer> scoreCounts)
		throws ScraperException, ParserException {
		return scrapeLatest(null, game, group, difficulty, scoreCounts);
	}
	
	public static List<RbScore> scrapeLatest(
		final ResultProgressHandle progress, final RbGame game, final Instrument.Group group, final Difficulty difficulty, final List<Integer> scoreCounts)
	throws ScraperException, ParserException {
		if (Difficulty.CO_OP == difficulty)
			throw new IllegalArgumentException("invalid rockband difficulty");
		
		if (null != progress) progress.setBusy("Downloading latest scores");
		LOG.info("Downloading latest scores for " + game + " on " + difficulty + " for " + group);
		
		List<RbScore> scores = new ArrayList<RbScore>();
		
		TieredTabularDataHandler handler =
			new LatestScoresHandler(game, group, difficulty, scores, scoreCounts);
		
		NodeList nodes = Scraper.scrape(
			URLs.rb.getManageScoresUrl(game, group, difficulty),
			handler.getDataTable(), jshm.sh.Client.getAuthCookies());
		
		LOG.finer("scrape() returned " + nodes.size() + " nodes");
		
		if (nodes.size() == 0)
			throw new ScraperException("nodes.size() == 0, invalid page format?");
		
//		System.out.println(nodes.toHtml());

		TieredTabularDataExtractor.extract(nodes, handler);
		
		LOG.fine("extract() returned " + scores.size() + " scores");
		
		return scores;
	}
	
	private static class LatestScoresHandler extends TieredTabularDataAdapter {
		private final RbGame game;
		private final Instrument.Group group;
		private final Difficulty difficulty;
		private final List<RbScore> scores;
		private final List<Integer> scoreCounts;
		private int curTierLevel = 0;
		
		public LatestScoresHandler(
			final RbGame game,
			final Instrument.Group group,
			final Difficulty difficulty,
			final List<RbScore> scores,
			final List<Integer> scoreCounts) {
			
			this.game = game;
			this.group = group;
			this.difficulty = difficulty;
			this.scores = scores;
			this.scoreCounts = scoreCounts;
		}
		
		@Override
		public DataTable getDataTable() {
			return RbDataTable.MANAGE_SCORES;
		}
		
		@Override
		public void handleTierRow(String tierName) throws ScraperException {
			// this will prevent retrieval of the demo tier
			if (curTierLevel >= game.getTierCount()) {
				ignoreNewData = true;
				return;
			}
			
			curTierLevel++;
		}
		
		// "-|text=int|link=songid~text|-|text=int|img=rating|text=int|text=int|text|text"
		@Override
		public void handleDataRow(String[][] data) throws ScraperException {
			RbSong song =
				RbSong.getByScoreHeroId(
					Integer.parseInt(data[2][0]));
			
			if (null == song) {
				LOG.warning("RbSong not found, scoreHeroId: " + data[2][0] + ". Try updating your song data.");
				return;
			}
			
			// see how many total scores there are so we know if
			// we need to make an additional request
			if (null != scoreCounts)
				scoreCounts.add(Integer.parseInt(data[1][0]));
			
			LOG.finest("scoreCount for " + data[2][1] + " = " + data[1][0]);
			
			RbScore score = new RbScore();
			score.setGame(game);
			score.setDifficulty(difficulty);
			score.setGroup(group);
			score.setStatus(Score.Status.SUBMITTED);
			score.setSong(song);
			
			int i = 4;
			
			// i = 4 for score
			
			score.setScore(Integer.parseInt(data[i][0]));
			
			
			// check for pic/vid links
			
			if (data[i].length >= 2) {
				for (int j = 1; j < data[i].length && j < 3; j++) {
					if (null == data[i][j] || data[i][j].isEmpty()) break;
					
					if (data[i][j].startsWith("pic:"))
						score.setImageUrl(data[i][j].substring(4));
					else if (data[i][j].startsWith("vid:"))
						score.setVideoUrl(data[i][j].substring(4));
				}
			}
			
			
			i++; // i = 5 for rating
			
			if (null != data[i] && data[i].length > 0 && !data[i][0].isEmpty()) {
				// infer rating from the image
				score.setRating(Integer.parseInt(data[i][0]));
			} else {
				// don't know the rating
				score.setRating(0);
			}
			
			i++; // i = 6 for %
			
			Part p = new Part();
			p.setDifficulty(difficulty);
			p.setInstrument(group.instruments[0]);
			
			if (null != data[i] && data[i].length > 0 && !data[i][0].isEmpty()) {
				p.setHitPercent(
					Float.parseFloat(data[i][0]) / 100.0f);
			}
			
			i++; // i = 7 for streak
			
			if (null != data[i] && data[i].length > 0 && !data[i][0].isEmpty()) {
				p.setStreak(
					Integer.parseInt(data[i][0]));
			}
			
			score.addPart(p);
			
			score.setCreationDate(null);
			
			i++; // i = 8 for submit date
			
			// null date is fine
//			if (!data[i][0].isEmpty()) {
				try {
					Date date = DateFormats.GH_MANAGE_SCORES.parse(data[i][0]);
					score.setSubmissionDate(date);
				} catch (Exception e) {}
//			}
			
			i++; // i = 9 for comment
				
			if (null != data[i] && data[i].length > 0 && !data[i][0].isEmpty() && !"N/A".equals(data[i][0])) {
				score.setComment(data[i][0]);
			}
			
			scores.add(score);
		}
	}
	
	
	/**
	 * 
	 * @param song
	 * @return A List of of all scores for the given song
	 * @throws ScraperException
	 * @throws ParserException 
	 */
	public static List<RbScore> scrapeScoresForSong(final RbSong song, final Instrument.Group group, final Difficulty difficulty) throws ScraperException, ParserException {
		// TODO scrape all scores for rb scores
		return null;
//		LOG.info("Downloading additional scores for " + song);
//		
//		List<RbScore> scores = new ArrayList<RbScore>();
//		
//		TieredTabularDataHandler handler =
//			new AllScoresHandler(song, scores);
//		
//		NodeList nodes = Scraper.scrape(
//			URLs.gh.getDeleteScoresUrl(song),
//			handler.getDataTable(), jshm.sh.Client.getAuthCookies());
//		
//		LOG.finest("scrape() returned " + nodes.size() + " nodes");
//		
//		if (nodes.size() == 0)
//			throw new ScraperException("nodes.size() == 0, invalid page format?");
//		
////		System.out.println(nodes.toHtml());
//
//		TieredTabularDataExtractor.extract(nodes, handler);
//		
//		LOG.fine("extract() returned " + scores.size() + " scores");
//		
//		return scores;
	}
	
	@SuppressWarnings("unused")
	// TODO handle all rb scores
	private static class AllScoresHandler extends TieredTabularDataAdapter {
		private final GhSong song;
		private final List<GhScore> scores;
		
		public AllScoresHandler(final GhSong song, final List<GhScore> scores) {
			this.song = song;
			this.scores = scores;
		}
		
		@Override
		public DataTable getDataTable() {
			return GhDataTable.DELETE_SCORES;
		}
		
		// input|text=int|img=rating~text=float|text=int|text=int|text
		@Override
		public void handleDataRow(String[][] data) throws ScraperException {			
			GhScore score = new GhScore();
			score.setStatus(Score.Status.SUBMITTED);
			score.setSong(song);
			
			score.setScore(Integer.parseInt(data[1][0]));
			
			if (data[2].length == 2 && !data[2][1].isEmpty()) {
				// there is a calculated rating available
				float f = Float.parseFloat(data[2][1]);
				score.setCalculatedRating(f);
				score.setRating(Math.min(5, (int) Math.floor(f)));
			} else if (!data[2][0].isEmpty()) {
				// assuming SH doesn't know the cuttoffs
				// and the user put in his rating himself
				// we have to infer from the image
				int i = Integer.parseInt(data[2][0]);
				score.setRating(i);
			} else {
				// don't know the rating
				score.setRating(0);
			}
			
			Part p = new Part();
			p.setDifficulty(song.getDifficulty());
			p.setInstrument(Instrument.GUITAR);
			
			if (!data[3][0].isEmpty()) {
				p.setHitPercent(
					Float.parseFloat(data[3][0]) / 100.0f);
			}
			
			if (!data[4][0].isEmpty()) {
				p.setStreak(
					Integer.parseInt(data[4][0]));
				score.setStreak(p.getStreak());
			}
			
			score.addPart(p);
			
			score.setCreationDate(null);
			
			// null date is fine
//			if (!data[5][0].isEmpty()) {
				try {
					Date date = DateFormats.GH_VIEW_SCORES.parse(data[5][0]);
					score.setSubmissionDate(date);
				} catch (java.text.ParseException e) {}
//			}
			
			scores.add(score);
		}
	}
}

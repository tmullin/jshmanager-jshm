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
package jshm.sh;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.net.URLCodec;

import jshm.*;
import jshm.Instrument.Group;
import jshm.gh.*;
import jshm.rb.*;
import jshm.sh.Forum.PostMode;
import jshm.wt.WtGame;
import jshm.wt.WtGameTitle;
import jshm.wt.WtScore;
import jshm.wt.WtSong;

public class URLs {
	public static final URLCodec urlCodec = new URLCodec();
	
	public static final String
		ROOT_DOMAIN = "scorehero.com",
		DOMAIN = "www." + ROOT_DOMAIN,
		BASE = "http://" + DOMAIN,
		LOGIN_URL = BASE + "/login.php"
		;
	
	public static class gh {
		public static final String
			BASE = URLs.BASE,
			SONG_STATS  = BASE + "/songstats.php?stat=%s&game=%s&diff=%s",
			MANAGE_SCORES = BASE + "/manage_scores.php?game=%s&diff=%s",
			TOP_SCORES = BASE + "/top_scores.php?game=%s&diff=%s",
			RANKINGS = BASE + "/rankings.php?group=%s&game=%s&diff=%s&song=%s",
			INSERT_SCORE = BASE + "/insert_score.php?song=%s",
			DELETE_SCORES = BASE + "/delete_scores.php?song=%s"
			;
		
		public static String getSongStatsUrl(final GhSongStat stat, final GhGame game, final Difficulty difficulty) {
			return String.format(SONG_STATS, stat.id, game.scoreHeroId, difficulty.scoreHeroId);
		}
		
		public static String getManageScoresUrl(final GhGame game, final Difficulty difficulty) {
			return String.format(MANAGE_SCORES, game.scoreHeroId, difficulty.scoreHeroId);
		}
		
		public static String getTopScoresUrl(final GhGame game, final Difficulty difficulty) {
			return String.format(TOP_SCORES, game.scoreHeroId, difficulty.scoreHeroId);
		}
		
		public static String getRankingsUrl(final GhSong song) {
			return String.format(RANKINGS,
				((GhGameTitle) song.getGame().title).scoreHeroGroupId,
				song.getGame().scoreHeroId,
				song.getDifficulty().scoreHeroId,
				song.getScoreHeroId());
		}
		
		public static String getInsertScoreUrl(final GhScore score) {
			return String.format(INSERT_SCORE, score.getSong().getScoreHeroId());
		}
		
		public static String getDeleteScoresUrl(final GhSong song) {
			return String.format(DELETE_SCORES, song.getScoreHeroId());
		}
	}
	
	public static class rb {
		public static final String
			ARGS_FMT = "game=%s&platform=%s&size=%s&group=%s&diff=%s",
			DOMAIN = "rockband." + ROOT_DOMAIN,
			BASE = "http://" + DOMAIN,
			MANAGE_SCORES = BASE + "/manage_scores.php?" + ARGS_FMT,
			TOP_SCORES = BASE + "/top_scores.php?" + ARGS_FMT,
			RANKINGS = BASE + "/rankings.php?%s&song=%s",
			INSERT_SCORE = BASE + "/insert_score.php?" + ARGS_FMT,
			DELETE_SCORES = BASE + "/delete_scores.php?" + ARGS_FMT
			;
				
		public static String getManageScoresUrl(final RbGame game, final Group group, final Difficulty difficulty) {
			return format(MANAGE_SCORES, game, group, difficulty);
		}
		
		public static String getTopScoresUrl(final RbGame game, final Group group, final Difficulty difficulty) {
			return format(TOP_SCORES, game, group, difficulty);
		}
		
		public static String getRankingsUrl(final RbGame game, final Group group, final Difficulty diff, final RbSong song) {
			return String.format(RANKINGS,
				format(ARGS_FMT, game, group, diff),
				song.getScoreHeroId()
			);
		}

		public static String getInsertScoreUrl(final RbScore score) {
			return getInsertScoreUrl((RbGame) score.getGame(), score.getGroup(), score.getDifficulty());
		}
		
		public static String getInsertScoreUrl(final RbGame game, final Group group, final Difficulty difficulty) {
			return format(INSERT_SCORE, game, group, difficulty);
		}
		
		public static String getDeleteScoresUrl(final RbGame game, final Group group, final Difficulty difficulty) {
			return format(DELETE_SCORES, game, group, difficulty);
		}
		
		private static String format(final String fmt, final RbGame game, final Group group, final Difficulty difficulty) {
			return String.format(fmt, game.scoreHeroId, RbPlatform.getId(game.platform), group.instruments.length, group.rockbandId, difficulty.scoreHeroId);
		}
	}
	
	public static class wt {
		public static final String
		ARGS_FMT = "group=%s&game=%s&size=%s&inst=%s&diff=%s&team=0",
		MANAGE_SCORES = BASE + "/manage_scores.php?" + ARGS_FMT,
		TOP_SCORES = BASE + "/top_scores.php?" + ARGS_FMT,
		INSERT_SCORE = BASE + "/insert_score.php?" + ARGS_FMT + "&song=%s",
		RANKINGS = BASE + "/rankings.php?" + ARGS_FMT + "&song=%s"
		;
		
		public static String getManageScoresUrl(WtGame game, Group group, Difficulty diff) {
			return String.format(MANAGE_SCORES,
				((WtGameTitle) game.title).scoreHeroGroupId,
				game.scoreHeroId,
				group.size, group.worldTourId, diff.scoreHeroId);
		}
		
		public static String getTopScoresUrl(WtGame game, Group group, Difficulty diff) {
			return String.format(TOP_SCORES,
				((WtGameTitle) game.title).scoreHeroGroupId,
				game.scoreHeroId,
				group.size, group.worldTourId, diff.scoreHeroId);
		}
		
		public static String getInsertScoreUrl(WtScore score) {
			return getInsertScoreUrl((WtGame) score.getGame(), score.getGroup(), score.getDifficulty(), (WtSong) score.getSong());
		}
		
		public static String getInsertScoreUrl(WtGame game, Group group, Difficulty diff, WtSong song) {
			return String.format(INSERT_SCORE,
				((WtGameTitle) game.title).scoreHeroGroupId,
				game.scoreHeroId,
				group.size, group.worldTourId, diff.scoreHeroId, song.getScoreHeroId());
		}
		
		public static String getRankingsUrl(final WtGame game, final Group group, final Difficulty diff, final WtSong song) {
			return String.format(RANKINGS,
				((WtGameTitle) game.title).scoreHeroGroupId,
				game.scoreHeroId,
				group.size, group.worldTourId, diff.scoreHeroId, song.getScoreHeroId());
		}
	}
	
	
	public static class wiki {
		public static final String
			DOMAIN = "wiki." + ROOT_DOMAIN,
			BASE = "http://" + DOMAIN,
			PAGE = "%s",
			GAME = "Game_%s",
			SONG = "Song_%s_%s",
			SONG_SP = SONG + "_%s_%s";
		
		/**
		 * Returns a formatted string that is additionally
		 * url-encoded to try to account for special chars.
		 * @param format
		 * @param args
		 * @return
		 */
		private static String format(String format, Object ... args) {
			try {
				return BASE + "/" +
					urlCodec.encode(
						String.format(format, args));
			} catch (EncoderException e) {
				return null;
			}
		}
		
		public static String getPageUrl(String page) {
			return format(PAGE, page);
		}
		
		public static String getGameUrl(GameTitle game) {
			return format(GAME, Wiki.wikiize(game.getLongName()));
		}
		
		public static String getSongUrl(Song song) {
			return format(SONG,
				song.getGameTitle().getWikiAbbr(),
				Wiki.wikiize(song.getTitle()));
		}
		
		public static String getSongSpUrl(Song song, Group group, Difficulty diff) {
			if (Difficulty.CO_OP == diff)
				throw new IllegalArgumentException("diff cannot be CO_OP");
			
			String groupStr = group.getWikiUrl();
			
			// for GH, use "Solo" or "Coop" instead of the actual group
			if (song instanceof GhSong) {
				groupStr = Group.GUITAR_BASS == group
				? "Coop" : "Solo";
			}
			
			return format(SONG_SP,
				song.getGameTitle().getWikiAbbr(),
				Wiki.wikiize(song.getTitle()),
				groupStr,
				Wiki.wikiize(diff.getLongName()));
		}
	}
	
	public static class forum {		
		public static final String
			BASE = "%s/forum",
			POST = BASE + "/posting.php?mode=%s&%s=%s";
		
		public static String getDomain(GameSeries series) {
			switch (series) {
				case GUITAR_HERO:
					return gh.BASE;
				case ROCKBAND:
					return rb.BASE;
			}

			assert false;
			return null;
		}
		
		public static String getPostUrl(GameSeries series, PostMode mode, int id) {
			return String.format(POST,
				getDomain(series), mode.value, mode.idName, id);
		}
	}
}

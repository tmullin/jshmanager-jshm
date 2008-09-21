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

import jshm.Difficulty;
import jshm.Instrument;
import jshm.gh.*;
import jshm.rb.RbGame;
import jshm.rb.RbScore;

public class URLs {
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
			INSERT_SCORE = BASE + "/insert_score.php?" + ARGS_FMT,
			DELETE_SCORES = BASE + "/delete_scores.php?" + ARGS_FMT
			;
				
		public static String getManageScoresUrl(final RbGame game, final Instrument.Group group, final Difficulty difficulty) {
			return format(MANAGE_SCORES, game, group, difficulty);
		}
		
		public static String getTopScoresUrl(final RbGame game, final Instrument.Group group, final Difficulty difficulty) {
			return format(TOP_SCORES, game, group, difficulty);
		}

		public static String getInsertScoreUrl(final RbScore score) {
			return getInsertScoreUrl((RbGame) score.getGame(), score.getGroup(), score.getDifficulty());
		}
		
		public static String getInsertScoreUrl(final RbGame game, final Instrument.Group group, final Difficulty difficulty) {
			return format(INSERT_SCORE, game, group, difficulty);
		}
		
		public static String getDeleteScoresUrl(final RbGame game, final Instrument.Group group, final Difficulty difficulty) {
			return format(DELETE_SCORES, game, group, difficulty);
		}
		
		private static String format(final String fmt, final RbGame game, final Instrument.Group group, final Difficulty difficulty) {
			return String.format(fmt, game.scoreHeroId, RbPlatform.getId(game.platform), group.instruments.length, group.id, difficulty.scoreHeroId);
		}
	}
}

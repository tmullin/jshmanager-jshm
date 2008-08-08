package jshm.sh;

import jshm.Difficulty;
import jshm.gh.GhGame;

public class URLs {
	public static final String
		ROOT_DOMAIN = "scorehero.com",
		DOMAIN = "www." + ROOT_DOMAIN,
		BASE = "http://" + DOMAIN,
		LOGIN_URL = "/login.php"
		;
	
	public static class gh {
		public static final String
			BASE = URLs.BASE,
			SONG_STATS  = BASE + "/songstats.php?stat=%s&game=%s&diff=%s",
			MANAGE_SCORES = BASE + "/manage_scores.php?game=%s&diff=%s",
			TOP_SCORES = BASE + "/top_scores.php?game=%s&diff=%s"
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
	}
}

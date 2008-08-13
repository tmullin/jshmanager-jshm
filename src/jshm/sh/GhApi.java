package jshm.sh;

import jshm.gh.GhScore;
import jshm.sh.client.HttpForm;

public class GhApi {
	public static void submitGhScore(final GhScore score) throws Exception {
		Client.getAuthCookies();
		
		new HttpForm((Object) URLs.gh.getInsertScoreUrl(score),
			"song", String.valueOf(score.getSong().getScoreHeroId()),
			"score", String.valueOf(score.getScore()),
			"percent", score.getHitPercent() != 0.0f ? String.valueOf((int) score.getHitPercent() * 100) : "",
			"streak", score.getStreak() != 0 ? String.valueOf(score.getStreak()) : "",
			"comment", score.getComment(),
			"link", score.getImageUrl(),
			"videolink", score.getVideoUrl()) {
			
			// TODO check the response we get when submitting this for errors
		}.submit();
	}
}

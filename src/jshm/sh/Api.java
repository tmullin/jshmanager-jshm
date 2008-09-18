package jshm.sh;

import jshm.Score;
import jshm.gh.GhScore;

public class Api {
	public static void submitScore(Score score) throws Exception {
		if (score instanceof GhScore) {
			GhApi.submitGhScore((GhScore) score);
		} /*else if (score instanceof RbScore) {
			
		}*/ else {
			throw new IllegalArgumentException("invalid score");
		}
	}
}

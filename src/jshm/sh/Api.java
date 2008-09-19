package jshm.sh;

import jshm.Score;
import jshm.gh.GhScore;
import jshm.rb.RbScore;

public class Api {
	public static void submitScore(Score score) throws Exception {
		if (score instanceof GhScore) {
			GhApi.submitGhScore((GhScore) score);
		} else if (score instanceof RbScore) {
			throw new UnsupportedOperationException("rb score uploading not yet implemented");
		} else {
			throw new IllegalArgumentException("invalid score");
		}
	}
}

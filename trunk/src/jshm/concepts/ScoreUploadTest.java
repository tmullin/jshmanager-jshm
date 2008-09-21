package jshm.concepts;

import jshm.Difficulty;
import jshm.Instrument.Group;
import jshm.gui.LoginDialog;
import jshm.rb.*;
import jshm.sh.Api;

public class ScoreUploadTest {
	static final RbGame game = RbGame.RB1_XBOX360;
	static final Group group = Group.GUITAR;
	static final Difficulty diff = Difficulty.EASY;
	
	public static void main(String[] args) throws Exception {
		RbSong song = RbSong.getByScoreHeroId(9);
		RbScore score = RbScore.createNewScoreTemplate(game, group, diff, song);
		score.setScore(1234);
		score.setRating(3);
		score.setPartHitPercent(1, 0.12f);
		score.setPartStreak(1, 34);
		
		LoginDialog.showDialog();
		
		Api.submitScore(score);
	}
}

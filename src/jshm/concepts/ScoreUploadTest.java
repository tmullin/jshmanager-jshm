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

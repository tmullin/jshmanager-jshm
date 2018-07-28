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
package jshm.rb;

//import java.util.Arrays;
import java.util.List;

import jshm.Difficulty;
import jshm.Game;
import jshm.Instrument;
import jshm.Platform;
import jshm.Score;
import jshm.Song;
import jshm.Tiers;
import jshm.Instrument.Group;

public class RbGame extends Game {
	static class RbTiers {
		public static final Tiers
			RB2_DIFFS = new Tiers("<UNKNOWN>|Warmup Songs|Apprentice Songs|Solid Songs|Moderate Songs|Challenging Songs|Nightmare Songs|Impossible Songs"),
			TBRB_DIFFS = new Tiers("<UNKNOWN>|Beginner|Apprentice|Moderate|Solid|Tricky|Challenging|Demanding")
			;
	}
	
	public static final RbGame
		RB1_PS2 = new RbGame(RbGameTitle.RB1, Platform.PS2, false),
		RB1_XBOX360 = new RbGame(RbGameTitle.RB1, Platform.XBOX360, true),
		RB1_PS3 = new RbGame(RbGameTitle.RB1, Platform.PS3, true),
		RB1_WII = new RbGame(RbGameTitle.RB1, Platform.WII, false),
	
		RB2_PS2 = new RbGame(RbGameTitle.RB2, Platform.PS2, false),
		RB2_XBOX360 = new RbGame(RbGameTitle.RB2, Platform.XBOX360, true),
		RB2_PS3 = new RbGame(RbGameTitle.RB2, Platform.PS3, true),
		RB2_WII = new RbGame(RbGameTitle.RB2, Platform.WII, true),
		
		RBN_XBOX360 = new RbGame(RbGameTitle.RBN, Platform.XBOX360, false),
		RBN_PS3 = new RbGame(RbGameTitle.RBN, Platform.PS3, false),
		RBN_WII = new RbGame(RbGameTitle.RBN, Platform.WII, false),
		
		RBNRB3_XBOX360 = new RbGame(RbGameTitle.RBNRB3, Platform.XBOX360, false),
		RBNRB3_PS3 = new RbGame(RbGameTitle.RBNRB3, Platform.PS3, false),
		RBNRB3_WII = new RbGame(RbGameTitle.RBNRB3, Platform.WII, false),
		
		TBRB_XBOX360 = new RbGame(RbGameTitle.TBRB, Platform.XBOX360, true),
		TBRB_PS3 = new RbGame(RbGameTitle.TBRB, Platform.PS3, true),
		TBRB_WII = new RbGame(RbGameTitle.TBRB, Platform.WII, true),
		
		LRB_XBOX360 = new RbGame(RbGameTitle.LRB, Platform.XBOX360, true),
		LRB_PS3 = new RbGame(RbGameTitle.LRB, Platform.PS3, true),
		LRB_WII = new RbGame(RbGameTitle.LRB, Platform.WII, false),
		
		GDRB_XBOX360 = new RbGame(RbGameTitle.GDRB, Platform.XBOX360, true),
		GDRB_PS3 = new RbGame(RbGameTitle.GDRB, Platform.PS3, true),
		GDRB_WII = new RbGame(RbGameTitle.GDRB, Platform.WII, true),
		
		RB3_XBOX360 = new RbGame(RbGameTitle.RB3, Platform.XBOX360, true),
		RB3_PS3 = new RbGame(RbGameTitle.RB3, Platform.PS3, true),
		RB3_WII = new RbGame(RbGameTitle.RB3, Platform.WII, true),
				
		RB4_XBOXONE = new RbGame(RbGameTitle.RB4, Platform.XBOXONE, true),
		RB4_PS4 = new RbGame(RbGameTitle.RB4, Platform.PS4, true)
		;
	
	public static void init() {}
	
	protected RbGame(RbGameTitle title, Platform platform,
			boolean supportsDLC) {
		super(title.scoreHeroId, title, platform, supportsDLC);
		mapTiers(Tiers.getTiers(this));
	}
	
	public void mapTiers(Tiers tiers) {
		if (null == tiers) return;
		
		// rb tiers are the same for everything, unlike some gh games
		for (Instrument.Group g : title.getSupportedInstrumentGroups()) {
			mapTiers(g, tiers);
		}
	}
	
	@Override public String getTierName(int tierLevel) {
		return getTierName(Instrument.Group.GUITAR, tierLevel);
	}
	
	@Override public int getTierLevel(final String tierName) {
		return getTierLevel(Instrument.Group.GUITAR, tierName);
	}
	
	@Override public int getTierCount() {
		return getTierCount(Instrument.Group.GUITAR);
	}

	
	@Override
	public RbSong getSongByScoreHeroId(int scoreHeroId, Difficulty diff) {
		return RbSong.getByScoreHeroId(scoreHeroId);
	}

	
	@Override
	public List<RbSong> getAllSongsByTitle(String title, Difficulty diff) {
		return RbSong.getAllByTitle(this, title);
	}
	
	@Override
	public RbSong getSongByTitle(String title, Difficulty diff) {
		return RbSong.getByTitle(this, title);
	}

	@Override
	public List<RbSong> getSongsOrderedByTitle(Group group, Difficulty diff) {
		return RbSong.getOrderedByTitles(this, group);
	}
	
	@Override
	public Score createNewScore(Song song, Group group, Difficulty diff,
			int score, int rating, float percent, int streak, String comment) {
		
		RbScore ret = RbScore.createNewScoreTemplate(this, group, diff, (RbSong) song);
		ret.setScore(score);
		ret.setRating(rating);
		ret.setPartHitPercent(1, percent);
		ret.setPartStreak(1, streak);
		ret.setComment(comment);
		
		return ret;
	}

	@Override
	public List<? extends Song> getSongs(Group group, Difficulty diff, Song.Sorting sorting) {
		return RbSong.getSongs(true, this, group, sorting);
	}

	
	@Override
	public RbScore createNewScoreTemplate(Group group, Difficulty diff, Song song) {
		return RbScore.createNewScoreTemplate(this, group, diff, (RbSong) song);
	}
	
	@Override
	public List<Score> getScores(Group group, Difficulty diff) {
		return RbScore.getScores(this, group, diff);
	}
	
	@Override
	public List<Score> getSubmittableScores(Group group, Difficulty diff) {
		return RbScore.getSubmittableScores(this, group, diff);
	}
}

/*
 * -----LICENSE START-----
 * JSHManager - A Java-based tool for managing one's ScoreHero account.
 * Copyright (C) 2008, 2009 Tim Mullin
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
package jshm.wt;

import java.util.List;

import jshm.Difficulty;
import jshm.Game;
import jshm.GameTitle;
import jshm.Instrument;
import jshm.Platform;
import jshm.Score;
import jshm.Song;
import jshm.Tiers;
import jshm.Instrument.Group;
import jshm.Song.Sorting;

public class WtGame extends Game {
	public static void init() {}
	
	public static WtGame
	GHWT_PS2 = new WtGame(16, WtGameTitle.GHWT, Platform.PS2, false),
	GHWT_XBOX360 = new WtGame(17, WtGameTitle.GHWT, Platform.XBOX360, true),
	GHWT_PS3 = new WtGame(18, WtGameTitle.GHWT, Platform.PS3, true),
	GHWT_WII = new WtGame(19, WtGameTitle.GHWT, Platform.WII, true),
	GHWT_PC = new WtGame(30, WtGameTitle.GHWT, Platform.PC, false),
	
	GHM_PS2 = new WtGame(21, WtGameTitle.GHM, Platform.PS2, true),
	GHM_XBOX360 = new WtGame(22, WtGameTitle.GHM, Platform.XBOX360, true),
	GHM_PS3 = new WtGame(23, WtGameTitle.GHM, Platform.PS3, true),
	GHM_WII = new WtGame(24, WtGameTitle.GHM, Platform.WII, true),
	
	GHSH_PS2 = new WtGame(26, WtGameTitle.GHSH, Platform.PS2, false),
	GHSH_XBOX360 = new WtGame(27, WtGameTitle.GHSH, Platform.XBOX360, false),
	GHSH_PS3 = new WtGame(28, WtGameTitle.GHSH, Platform.PS3, false),
	GHSH_WII = new WtGame(29, WtGameTitle.GHSH, Platform.WII, false),
	
	GH5_PS2 = new WtGame(31, WtGameTitle.GH5, Platform.PS2, false),
	GH5_XBOX360 = new WtGame(32, WtGameTitle.GH5, Platform.XBOX360, true),
	GH5_PS3 = new WtGame(33, WtGameTitle.GH5, Platform.PS3, true),
	GH5_WII = new WtGame(34, WtGameTitle.GH5, Platform.WII, true),
	
	GHVH_PS2 = new WtGame(35, WtGameTitle.GHVH, Platform.PS2, false),
	GHVH_XBOX360 = new WtGame(36, WtGameTitle.GHVH, Platform.XBOX360, false),
	GHVH_PS3 = new WtGame(37, WtGameTitle.GHVH, Platform.PS3, false),
	GHVH_WII = new WtGame(38, WtGameTitle.GHVH, Platform.WII, false),
	
	BH_PS2 = new WtGame(39, WtGameTitle.BH, Platform.PS2, false),
	BH_XBOX360 = new WtGame(40, WtGameTitle.BH, Platform.XBOX360, false),
	BH_PS3 = new WtGame(41, WtGameTitle.BH, Platform.PS3, false),
	BH_WII = new WtGame(42, WtGameTitle.BH, Platform.WII, false),
	
	GHWOR_XBOX360 = new WtGame(49, WtGameTitle.GHWOR, Platform.XBOX360, true),
	GHWOR_PS3 = new WtGame(50, WtGameTitle.GHWOR, Platform.PS3, true),
	GHWOR_WII = new WtGame(51, WtGameTitle.GHWOR, Platform.WII, true)
	;
	
	protected WtGame(int scoreHeroId, GameTitle title, Platform platform,
			boolean supportsDLC) {
		super(scoreHeroId, title, platform, supportsDLC);
		mapTiers(Tiers.getTiers(this));
	}

	public void mapTiers(Tiers tiers) {
		if (null == tiers) return;
		
		for (Group g : title.getSupportedInstrumentGroups()) {
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

	
	// song related overrides
	
	@Override
	public List<WtSong> getAllSongsByTitle(String title, Difficulty diff) {
		return WtSong.getAllByTitle(this, title, diff);
	}

	@Override
	public Song getSongByScoreHeroId(int scoreHeroId, Difficulty diff) {
		return WtSong.getByScoreHeroId((WtGameTitle) title, scoreHeroId, diff);
	}

	@Override
	public Song getSongByTitle(String title, Difficulty diff) {
		return WtSong.getByTitle(this, title, diff);
	}

	@Override
	public List<WtSong> getSongs(Group group, Difficulty diff,
			Sorting sorting) {
		return WtSong.getSongs(true, this, group, diff, sorting);
	}

	@Override
	public List<WtSong> getSongsOrderedByTitle(Group group,
			Difficulty diff) {
		return WtSong.getOrderedByTitles(this, group, diff);
	}

	
	// score related overrides
	
	@Override
	public Score createNewScore(Song song, Group group, Difficulty diff,
		int score, int rating, float percent, int streak, String comment) {
		
		WtScore ret = WtScore.createNewScoreTemplate(this, group, diff, (WtSong) song);
		ret.setScore(score);
		ret.setRating(rating);
		ret.setPartHitPercent(1, percent);
		ret.setPartStreak(1, streak);
		ret.setComment(comment);
		
		return ret;
	}
	

	@Override
	public WtScore createNewScoreTemplate(Group group, Difficulty diff, Song song) {
		return WtScore.createNewScoreTemplate(this, group, diff, (WtSong) song);
	}
	
	@Override
	public List<Score> getScores(Group group, Difficulty diff) {
		return WtScore.getScores(this, group, diff);
	}
	
	@Override
	public List<Score> getSubmittableScores(Group group,
		Difficulty diff) {
		
		return WtScore.getSubmittableScores(this, group, diff);
	}
}

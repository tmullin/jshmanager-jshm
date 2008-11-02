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

import java.util.Arrays;
import java.util.List;

import jshm.*;
import jshm.Instrument.Group;

public class RbGame extends Game {
	private static class RbTiers {
		public static final String[]
		    RB1_PS2 = "Warmup Songs|Apprentice Songs|Solid Songs|Moderate Songs|Skilled Songs|Challenging Songs|Blistering Songs|Nightmare Songs|Impossible Songs|European Exclusives|Track Pack Volume 1|AC/DC Live Track Pack".split("\\|"),
			RB1_NEXTGEN = "Warmup Songs|Apprentice Songs|Solid Songs|Moderate Songs|Skilled Songs|Challenging Songs|Blistering Songs|Nightmare Songs|Impossible Songs|AC/DC Live Track Pack|Downloaded Songs".split("\\|"),
			RB1_WII = Arrays.copyOf(RB1_PS2, RB1_PS2.length - 1),
			
			RB2 = "Warmup Songs|Apprentice Songs|Solid Songs|Moderate Songs|Challenging Songs|Nightmare Songs|Impossible Songs|Rock Band Imported|AC/DC Live Track Pack|Downloaded Songs".split("\\|")
			;
	}
	
	public static final RbGame
		RB1_PS2 = new RbGame(RbGameTitle.RB1, RbTiers.RB1_PS2, Platform.PS2, false),
		RB1_XBOX360 = new RbGame(RbGameTitle.RB1, RbTiers.RB1_NEXTGEN, Platform.XBOX360, true),
		RB1_PS3 = new RbGame(RbGameTitle.RB1, RbTiers.RB1_NEXTGEN, Platform.PS3, true),
		RB1_WII = new RbGame(RbGameTitle.RB1, RbTiers.RB1_WII, Platform.WII, false),
	
		RB2_XBOX360 = new RbGame(RbGameTitle.RB2, RbTiers.RB2, Platform.XBOX360, true),
		RB2_PS3 = new RbGame(RbGameTitle.RB2, RbTiers.RB2, Platform.PS3, true)
		;
	
	public static void init() {}
	
	protected RbGame(RbGameTitle title, String[] tiers, Platform platform,
			boolean supportsDLC) {
		super(title.scoreHeroId, title, platform, supportsDLC);
		
		// rb tiers are the same for everything, unlike some gh games
		Tiers t = new Tiers(tiers);
		for (Instrument.Group g : title.getSupportedInstrumentGroups()) {
			mapTiers(g, t);
		}
	}
	
	public String getTierName(int tierLevel) {
		return getTierName(Instrument.Group.GUITAR, tierLevel);
	}
	
	public int getTierLevel(final String tierName) {
		return getTierLevel(Instrument.Group.GUITAR, tierName);
	}
	
	public int getTierCount() {
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
		return RbSong.getSongsOrderedByTitles(this, group);
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
	public List<? extends Song> getSongs(Group group, Difficulty diff) {
		return RbSong.getSongs(true, this, group);
	}

	@Override
	public List<? extends Score> getScores(Group group, Difficulty diff) {
		return RbScore.getScores(this, group, diff);
	}
}

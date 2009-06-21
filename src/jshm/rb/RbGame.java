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
//			TierGrabber - 4/3/09:
//			Mem: 0.500/4.938 (used/total) mb
//			RB1_PS2 = "Warmup Songs|Apprentice Songs|Solid Songs|Moderate Songs|Skilled Songs|Challenging Songs|Blistering Songs|Nightmare Songs|Impossible Songs|European Exclusives|Track Pack Volume 1|AC/DC Live Track Pack|Track Pack Volume 2",
//			RB1_XBOX360 = "Warmup Songs|Apprentice Songs|Solid Songs|Moderate Songs|Skilled Songs|Challenging Songs|Blistering Songs|Nightmare Songs|Impossible Songs|AC/DC Live Track Pack|Downloaded Songs",
//			RB1_PS3 = "Warmup Songs|Apprentice Songs|Solid Songs|Moderate Songs|Skilled Songs|Challenging Songs|Blistering Songs|Nightmare Songs|Impossible Songs|AC/DC Live Track Pack|Downloaded Songs",
//			RB1_WII = "Warmup Songs|Apprentice Songs|Solid Songs|Moderate Songs|Skilled Songs|Challenging Songs|Blistering Songs|Nightmare Songs|Impossible Songs|European Exclusives|Track Pack Volume 1|AC/DC Live Track Pack|Track Pack Volume 2",
//			Time: 5.093 seconds
//			Mem: 9.784/11.754 (used/total) mb
		    RB1_PS2 = new Tiers("Warmup Songs|Apprentice Songs|Solid Songs|Moderate Songs|Skilled Songs|Challenging Songs|Blistering Songs|Nightmare Songs|Impossible Songs|European Exclusives|Track Pack Volume 1|AC/DC Live Track Pack|Track Pack Volume 2|Classic Rock Track Pack"),
			RB1_NEXTGEN = new Tiers("Warmup Songs|Apprentice Songs|Solid Songs|Moderate Songs|Skilled Songs|Challenging Songs|Blistering Songs|Nightmare Songs|Impossible Songs|AC/DC Live Track Pack|Downloaded Songs"),
			RB1_WII = new Tiers("Warmup Songs|Apprentice Songs|Solid Songs|Moderate Songs|Skilled Songs|Challenging Songs|Blistering Songs|Nightmare Songs|Impossible Songs|European Exclusives|Track Pack Volume 1|AC/DC Live Track Pack|Track Pack Volume 2"),
		
//			TierGrabber - 4/3/09:
//			Mem: 0.500/4.938 (used/total) mb
//			RB2_PS2 = "Warmup Songs|Apprentice Songs|Solid Songs|Moderate Songs|Challenging Songs|Nightmare Songs|Impossible Songs",
//			RB2_XBOX360 = "Warmup Songs|Apprentice Songs|Solid Songs|Moderate Songs|Challenging Songs|Nightmare Songs|Impossible Songs|Rock Band Imported|AC/DC Live Track Pack|Downloaded Songs",
//			RB2_PS3 = "Warmup Songs|Apprentice Songs|Solid Songs|Moderate Songs|Challenging Songs|Nightmare Songs|Impossible Songs|Rock Band Imported|AC/DC Live Track Pack|Downloaded Songs",
//			RB2_WII = "Warmup Songs|Apprentice Songs|Solid Songs|Moderate Songs|Challenging Songs|Nightmare Songs|Impossible Songs|Downloaded Songs",
//			Time: 5.527 seconds
//			Mem: 6.740/15.379 (used/total) mb
			RB2_PS2 = new Tiers("Warmup Songs|Apprentice Songs|Solid Songs|Moderate Songs|Challenging Songs|Nightmare Songs|Impossible Songs"),
			RB2_NEXTGEN = new Tiers("Warmup Songs|Apprentice Songs|Solid Songs|Moderate Songs|Challenging Songs|Nightmare Songs|Impossible Songs|Rock Band Imported|AC/DC Live Track Pack|Downloaded Songs"),
			RB2_WII = new Tiers("Warmup Songs|Apprentice Songs|Solid Songs|Moderate Songs|Challenging Songs|Nightmare Songs|Impossible Songs|Downloaded Songs"),
			
			RB2_DIFFS = new Tiers("<UNKNOWN>|Warmup Songs|Apprentice Songs|Solid Songs|Moderate Songs|Challenging Songs|Nightmare Songs|Impossible Songs")
			;
	}
	
	public static final RbGame
		RB1_PS2 = new RbGame(RbGameTitle.RB1, RbTiers.RB1_PS2, Platform.PS2, false),
		RB1_XBOX360 = new RbGame(RbGameTitle.RB1, RbTiers.RB1_NEXTGEN, Platform.XBOX360, true),
		RB1_PS3 = new RbGame(RbGameTitle.RB1, RbTiers.RB1_NEXTGEN, Platform.PS3, true),
		RB1_WII = new RbGame(RbGameTitle.RB1, RbTiers.RB1_WII, Platform.WII, false),
	
		RB2_PS2 = new RbGame(RbGameTitle.RB2, RbTiers.RB2_PS2, Platform.PS2, false),
		RB2_XBOX360 = new RbGame(RbGameTitle.RB2, RbTiers.RB2_NEXTGEN, Platform.XBOX360, true),
		RB2_PS3 = new RbGame(RbGameTitle.RB2, RbTiers.RB2_NEXTGEN, Platform.PS3, true),
		RB2_WII = new RbGame(RbGameTitle.RB2, RbTiers.RB2_WII, Platform.WII, true)
		;
	
	public static void init() {}
	
	protected RbGame(RbGameTitle title, Tiers tiers, Platform platform,
			boolean supportsDLC) {
		super(title.scoreHeroId, title, platform, supportsDLC);
		
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

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
package jshm.gh;

import java.util.Arrays;
import java.util.List;

import jshm.*;
import jshm.Instrument.Group;

/**
 * This class represents a Guitar Hero game as far as it
 * is represented on ScoreHero.
 * @author Tim Mullin
 *
 */
public class GhGame extends jshm.Game {
	private static class SPTiers {
		public static final Tiers
			GH1 = new Tiers("Opening Licks|Axe-Grinders|Thrash and Burn|Return of the Shred|Fret-Burners|Face-Melters|Bonus Tracks|Secret Songs"),
			GH2_DLC = new Tiers("Opening Licks|Amp-Warmers|String-Snappers|Thrash and Burn|Return of the Shred|Relentless Riffs|Furious Fretwork|Face-Melters|Bonus Tracks|Downloaded Tracks"),
			GH2 = new Tiers(Arrays.copyOf(GH2_DLC.tiers, GH2_DLC.tiers.length - 1)),
			GH80 = new Tiers("Opening Licks|Amp-Warmers|String Snappers|Return of the Shred|Relentless Riffs|Furious Fretwork"),
			GH3_DLC = new Tiers("Starting Out Small|Your First Real Gig|Making the Video|European Invasion|Bighouse Blues|The Hottest Band on Earth|Live in Japan|Battle for Your Soul|Quickplay Exclusives|Bonus Tracks|Downloaded Songs"),
			GH3 = new Tiers(Arrays.copyOf(GH3_DLC.tiers, GH3_DLC.tiers.length - 1)),
			GHOT = new Tiers("Subway|Rooftop|Parade|Greek Arena|Battleship|Bonus"),
			GHA = new Tiers("Getting The Band Together|First Taste Of Success|The Triumphant Return|International Superstars|The Great American Band|Rock 'N Roll Legends|The Vault")
		;
	}
	
	@SuppressWarnings("unused")
	private static class CoopTiers {
		
	}
	
	public static final GhGame
		GH1_PS2 = new GhGame(1, GhGameTitle.GH1, SPTiers.GH1, Platform.PS2, false),
		
		GH2_PS2 = new GhGame(2, GhGameTitle.GH2, SPTiers.GH2, Platform.PS2, false),
		GH2_XBOX360 = new GhGame(3, GhGameTitle.GH2, SPTiers.GH2_DLC, Platform.XBOX360, true),
		
		GH80_PS2 = new GhGame(4, GhGameTitle.GH80, SPTiers.GH80, Platform.PS2, false),
		
		GH3_PS2 = new GhGame(5, GhGameTitle.GH3, SPTiers.GH3, Platform.PS2, false),
		GH3_XBOX360 = new GhGame(6, GhGameTitle.GH3, SPTiers.GH3_DLC, Platform.XBOX360, true),
		GH3_PS3 = new GhGame(7, GhGameTitle.GH3, SPTiers.GH3_DLC, Platform.PS3, true),
		GH3_WII = new GhGame(8, GhGameTitle.GH3, SPTiers.GH3, Platform.WII, false),
		GH3_PC = new GhGame(9, GhGameTitle.GH3, SPTiers.GH3, Platform.PC, false),
		
		GHOT_DS = new GhGame(10, GhGameTitle.GHOT, SPTiers.GHOT, Platform.DS, false),
		
		GHA_PS2 = new GhGame(11, GhGameTitle.GHA, SPTiers.GHA, Platform.PS2, false),
		GHA_XBOX360 = new GhGame(12, GhGameTitle.GHA, SPTiers.GHA, Platform.XBOX360, false),
		GHA_PS3 = new GhGame(13, GhGameTitle.GHA, SPTiers.GHA, Platform.PS3, false),
		GHA_WII = new GhGame(14, GhGameTitle.GHA, SPTiers.GHA, Platform.WII, false),
		GHA_PC = new GhGame(15, GhGameTitle.GHA, SPTiers.GHA, Platform.PC, false)
	;
	
	public static void init() {}
	
	/**
	 * Return the correct GhGame for the given id
	 * @param id
	 * @return
	 */
	public static GhGame getById(final int id) {
		for (Game g : values()) {
			if ((g instanceof GhGame) && g.scoreHeroId == id) return (GhGame) g;
		}
		
		throw new IllegalArgumentException("invalid GhGame id: " + id);
	}
	
	protected GhGame(
		final int scoreHeroId,
		final GhGameTitle title,
		final Tiers singlePlayerTiers,
		final Platform platform,
		final boolean supportsDLC) {
		
		super(scoreHeroId, title, platform, supportsDLC);
		
		assert null != singlePlayerTiers;
		mapTiers(GhGameTitle.SINGLE_PLAYER_GROUP, singlePlayerTiers);
	}
	
	@Override public String getTierName(int tierLevel) {
		return getTierName(GhGameTitle.SINGLE_PLAYER_GROUP, tierLevel);
	}
	
	@Override public int getTierLevel(final String tierName) {
		return getTierLevel(GhGameTitle.SINGLE_PLAYER_GROUP, tierName);
	}
	
	@Override public int getTierCount() {
		return getTierCount(GhGameTitle.SINGLE_PLAYER_GROUP);
	}

	
	@Override
	public GhSong getSongByScoreHeroId(int scoreHeroId, Difficulty diff) {
		return GhSong.getByScoreHeroId(scoreHeroId);
	}

	@Override
	public List<GhSong> getAllSongsByTitle(String title, Difficulty diff) {
		return GhSong.getAllByTitle(this, title, diff);
	}
	
	@Override
	public GhSong getSongByTitle(String title, Difficulty diff) {
		return GhSong.getByTitle(this, title, diff);
	}

	@Override
	public List<GhSong> getSongsOrderedByTitle(Group group, Difficulty diff) {
		return GhSong.getSongsOrderedByTitles(this, diff);
	}
	
	@Override
	public Score createNewScore(Song song, Group group, Difficulty diff,
			int score, int rating, float percent, int streak, String comment) {
		
		GhScore ret = GhScore.createNewScoreTemplate(this, group, diff, (GhSong) song);
		ret.setScore(score);
		ret.setRating(rating);
		ret.setPartHitPercent(1, percent);
		ret.setPartStreak(1, streak);
		ret.setComment(comment);
		
		return ret;
	}

	@Override
	public List<? extends Song> getSongs(Group group, Difficulty diff, Song.Sorting sorting) {
		return GhSong.getSongs(this, diff);
	}

	@Override
	public GhScore createNewScoreTemplate(Group group, Difficulty diff, Song song) {
		return GhScore.createNewScoreTemplate(this, group, diff, (GhSong) song);
	}
	
	@Override
	public List<Score> getScores(Group group, Difficulty diff) {
		return GhScore.getScores(this, diff);
	}
	
	@Override
	public List<Score> getSubmittableScores(Group group, Difficulty diff) {
		return GhScore.getSubmittableScores(this, diff);
	}
}

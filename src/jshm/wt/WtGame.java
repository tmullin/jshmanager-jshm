package jshm.wt;

import java.util.Arrays;
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
	private static class WtTiers {
		private static final Tiers
		WT_DLC = new Tiers("USA - Phi Psi Kappa|Sweden - Wilted Orchid|Poland - Bone Church|Hong Kong - Pang Tang Bay|Los Angeles - Amoeba Records|USA - Tool|Louisiana - Swamp Shack|The Pacific - Rock Brigade|Kentucky - Strutter's Farm|Los Angeles - House of Blues|Tahiti - Ted's Tiki Hut|England - Will Heilm's Keep|Canada - Recording Studio|San Francisco - AT&T Park|Australia - Tesla's Coil|Germany - Ozzfest|New York - Times Square|Asgard - Sunna's Chariot|Downloadable Content"),
		WT_PS2 = new Tiers(Arrays.copyOf(WT_DLC.tiers, WT_DLC.tiers.length - 1)),
	
		M_ALL = new Tiers("The Forum|Tushino Air Field|Metallica at Tushino|Hammersmith Apollo|Damaged Justice Tour|The Meadowlands|Donington Park|The Ice Cave|Downloadable Content"),
		SH_ALL = new Tiers("Amazon Rain Forest|Grand Canyon|Polar Ice Cap|London Sewerage System|The Sphinx|The Great Wall Of China|The Lost City of Atlantis|Quebec City")
		;
	}
	
	public static void init() {}
	
	public static WtGame
	GHWT_PS2 = new WtGame(16, WtGameTitle.GHWT, WtTiers.WT_PS2, Platform.PS2, false),
	GHWT_XBOX360 = new WtGame(17, WtGameTitle.GHWT, WtTiers.WT_DLC, Platform.XBOX360, true),
	GHWT_PS3 = new WtGame(18, WtGameTitle.GHWT, WtTiers.WT_DLC, Platform.PS3, true),
	GHWT_WII = new WtGame(19, WtGameTitle.GHWT, WtTiers.WT_DLC, Platform.WII, true),
	
	GHM_PS2 = new WtGame(21, WtGameTitle.GHM, WtTiers.M_ALL, Platform.PS2, true),
	GHM_XBOX360 = new WtGame(22, WtGameTitle.GHM, WtTiers.M_ALL, Platform.XBOX360, true),
	GHM_PS3 = new WtGame(23, WtGameTitle.GHM, WtTiers.M_ALL, Platform.PS3, true),
	GHM_WII = new WtGame(24, WtGameTitle.GHM, WtTiers.M_ALL, Platform.WII, true),
	
	GHSH_PS2 = new WtGame(26, WtGameTitle.GHSH, WtTiers.SH_ALL, Platform.PS2, false),
	GHSH_XBOX360 = new WtGame(27, WtGameTitle.GHSH, WtTiers.SH_ALL, Platform.XBOX360, false),
	GHSH_PS3 = new WtGame(28, WtGameTitle.GHSH, WtTiers.SH_ALL, Platform.PS3, false),
	GHSH_WII = new WtGame(29, WtGameTitle.GHSH, WtTiers.SH_ALL, Platform.WII, false)
	;
	
	protected WtGame(int scoreHeroId, GameTitle title, Tiers tiers, Platform platform,
			boolean supportsDLC) {
		super(scoreHeroId, title, platform, supportsDLC);

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
		return WtSong.getAllByTitle(this, title);
	}

	@Override
	public Song getSongByScoreHeroId(int scoreHeroId, Difficulty diff) {
		return WtSong.getByScoreHeroId(scoreHeroId);
	}

	@Override
	public Song getSongByTitle(String title, Difficulty diff) {
		return WtSong.getByTitle(this, title);
	}

	@Override
	public List<WtSong> getSongs(Group group, Difficulty diff,
			Sorting sorting) {
		return WtSong.getSongs(true, this, group, sorting);
	}

	@Override
	public List<WtSong> getSongsOrderedByTitle(Group group,
			Difficulty diff) {
		return WtSong.getOrderedByTitles(this, group);
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

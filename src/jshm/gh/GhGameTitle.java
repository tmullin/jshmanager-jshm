package jshm.gh;

import jshm.*;

public class GhGameTitle extends jshm.GameTitle {
	public static final GhGameTitle
		GH1  = new GhGameTitle("GH1",  "Opening Licks|Axe-Grinders|Thrash and Burn|Return of the Shred|Fret-Burners|Face-Melters|Bonus Tracks|Secret Songs", Platform.PS2),
		GH2  = new GhGameTitle("GH2",  "Opening Licks|Amp-Warmers|String-Snappers|Thrash and Burn|Return of the Shred|Relentless Riffs|Furious Fretwork|Face-Melters|Bonus Tracks|Downloaded Tracks", Platform.PS2, Platform.XBOX360),
		GH80 = new GhGameTitle("GH80", "Opening Licks|Amp-Warmers|String Snappers|Return of the Shred|Relentless Riffs|Furious Fretwork", Platform.PS2),
		GH3  = new GhGameTitle("GH3",  "Starting Out Small|Your First Real Gig|Making the Video|European Invasion|Bighouse Blues|The Hottest Band on Earth|Live in Japan|Battle for Your Soul|Quickplay Exclusives|Bonus Tracks|Downloaded Songs", Platform.PS2, Platform.XBOX360, Platform.PS3, Platform.WII, Platform.PC),
		GHOT = new GhGameTitle("GHOT", "Subway|Rooftop|Parade|Greek Arena|Battleship|Bonus", Platform.DS),
		GHA  = new GhGameTitle("GHA",  "Getting The Band Together|First Taste Of Success|The Triumphant Return|International Superstars|The Great American Band|Rock 'N Roll Legends|The Vault", Platform.PS2, Platform.XBOX360, Platform.PS3, Platform.WII)
	;
	
	private GhGameTitle(final String title, final String tiers, final Platform ... platforms) {
		super(GameSeries.GUITAR_HERO, title, platforms);
		mapTiers(Instrument.Group.GUITAR, new Tiers(tiers));
	}
	
	public String getTierName(final int tierLevel) {
		return getTierName(Instrument.Group.GUITAR, tierLevel);
	}
	
	public int getTierCount() {
		return getTierCount(Instrument.Group.GUITAR);
	}
	
	
	// override abstract methods
	
	private static final Instrument.Group[] DEFAULT_SUPPORTED_INSTRUMENT_GROUPS = new Instrument.Group[] {
		Instrument.Group.GUITAR, Instrument.Group.GUITAR_BASS
	};
	
	private static final int
		MIN_STARS			 = 3,
		MAX_STARS			 = 5,
		MAX_CALCULABLE_STARS = 9;
	
	@Override
	public int getMaxRating() {
		return MAX_STARS;
	}

	@Override
	public int getMinRating() {
		return MIN_STARS;
	}

	@Override
	public boolean supportsCalculatedRating() {
		return true;
	}
	
	@Override
	public float getMaxCalculatedRating() {
		return MAX_CALCULABLE_STARS;
	}

	@Override
	public Difficulty.Strategy getDifficultyStrategy() {
		return Difficulty.Strategy.BY_SONG;
	}
	
	@Override
	public Instrument.Group[] getSupportedInstrumentGroups() {
		return DEFAULT_SUPPORTED_INSTRUMENT_GROUPS;
	}

	@Override
	public StreakStrategy getStreakStrategy() {
		return StreakStrategy.BY_SCORE;
	}
}

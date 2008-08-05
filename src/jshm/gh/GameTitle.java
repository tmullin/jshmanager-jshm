package jshm.gh;

public enum GameTitle {
	GH1("Opening Licks|Axe-Grinders|Thrash and Burn|Return of the Shred|Fret-Burners|Face-Melters|Bonus Tracks|Secret Songs"),
	GH2("Opening Licks|Amp-Warmers|String-Snappers|Thrash and Burn|Return of the Shred|Relentless Riffs|Furious Fretwork|Face-Melters|Bonus Tracks|Downloaded Tracks"),
	GH80("Opening Licks|Amp-Warmers|String Snappers|Return of the Shred|Relentless Riffs|Furious Fretwork"),
	GH3("Starting Out Small|Your First Real Gig|Making the Video|European Invasion|Bighouse Blues|The Hottest Band on Earth|Live in Japan|Battle for Your Soul|Quickplay Exclusives|Bonus Tracks|Downloaded Songs"),
	GHOT("Subway|Rooftop|Parade|Greek Arena|Battleship|Bonus"),
	GHA("Getting The Band Together|First Taste Of Success|The Triumphant Return|International Superstars|The Great American Band|Rock 'N Roll Legends|The Vault")
	;
	
	private String[] tiers;
	
	private GameTitle(final String tiers) {
		this.tiers = tiers.split("\\|");
	}
	
	/**
	 * Get a tier name by index. Providing 0 will return "UNKNOWN" for
	 * compatibility with empty songs.
	 * @param index The <b>1-based</b> index to retrieve.
	 * @return
	 */
	public String getTierName(final int index) {
		if (0 == index) return "UNKNOWN";
		return tiers[index - 1];
	}
	
	public int getTierCount() {
		return tiers.length;
	}
}

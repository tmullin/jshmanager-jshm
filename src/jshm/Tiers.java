package jshm;

/**
 * Represents a collection of tier headings.
 * Ideally this will be able to handle multiple
 * tiers with different orderings like in RockBand.
 * @author Tim Mullin
 *
 */
public class Tiers {
	private final String[] tiers;
	
	public Tiers(final String tiers) {
		this(tiers.split("\\|"));
	}
	
	public Tiers(final String[] tiers) {
		this.tiers = tiers;
	}

	/**
	 * 
	 * @param index The <b>1-based</b> index to retreive the name for
	 * @return The name of the tier the given index maps to
	 */
	public String getName(final int index) {
		if (0 == index) return "UNKNOWN";
		return tiers[index - 1];
	}
	
	public int getCount() {
		return tiers.length;
	}
}

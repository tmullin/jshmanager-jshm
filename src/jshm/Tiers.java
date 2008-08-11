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
	
	/**
	 * 
	 * @param name The name of the tier to find the level for
	 * @return The <b>1-based</b> level for the given tier name or 0 if not found
	 */
	public int getLevel(final String name) {
		for (int i = 0; i < tiers.length; i++) {
			if (tiers[i].equals(name)) return i + 1;
		}
		
		return 0;
	}
	
	public int getCount() {
		return tiers.length;
	}
}

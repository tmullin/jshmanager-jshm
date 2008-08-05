package jshm.sh.gh;

import jshm.sh.Difficulty;


/**
 * This class represents a Guitar Hero song as far as
 * it is represented on ScoreHero.
 * @author Tim Mullin
 *
 */
public class Song {
	/**
	 * The ScoreHero id for this song.
	 */
	public int id				= 0;
	public int order			= 0;
	
	public Game 		game 		= null;
	public Difficulty 	difficulty 	= null;
	
	public String title 		= "UNKNOWN";
	public String tierName		= "UNKNOWN";
	public int tierLevel		= 0;
	public int noteCount 		= 0;
	public int baseScore 		= 0;
	public int fourStarCutoff 	= 0;
	public int fiveStarCutoff 	= 0;
	public int sixStarCutoff 	= 0;
	public int sevenStarCutoff 	= 0;
	public int eightStarCutoff 	= 0;
	public int nineStarCutoff 	= 0;
	
	public void setScoreAndCutoffs(final Song source) {
		this.baseScore 			= source.baseScore;
		this.fourStarCutoff 	= source.fourStarCutoff;
		this.fiveStarCutoff 	= source.fiveStarCutoff;
		this.sixStarCutoff		= source.sixStarCutoff;
		this.sevenStarCutoff 	= source.sevenStarCutoff;
		this.eightStarCutoff 	= source.eightStarCutoff;
		this.nineStarCutoff 	= source.nineStarCutoff;
	}
	
	public String getTierName() {
		return game.title.getTierName(this.tierLevel);
	}
	
	public boolean equals(Object o) {
		if (!(o instanceof Song)) return false;
		
		Song s = (Song) o;
		
		return (this.id == s.id ||
				(this.title.equals(s.title) &&
				 this.game == s.game &&
				 this.difficulty == s.difficulty));
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(game);
		sb.append(',');
		sb.append(difficulty.toShortString());
		sb.append(',');
		sb.append(id);
		sb.append(',');
		sb.append(title);
		sb.append(",t=");
		sb.append(tierLevel);
		sb.append(",n=");
		sb.append(noteCount);
		sb.append(',');
		sb.append(baseScore);
		sb.append(',');
		sb.append(fourStarCutoff);
		sb.append(',');
		sb.append(fiveStarCutoff);
		
		return sb.toString();
	}
}

package jshm.sh;

import jshm.gh.Difficulty;

public class Song {
	public Game 		game 		= Game.UNKNOWN;
	public Difficulty 	difficulty 	= Difficulty.UNKNOWN;
	
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
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(title);
		sb.append(',');
		sb.append("tier");
		sb.append(tierLevel);
		sb.append(',');
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

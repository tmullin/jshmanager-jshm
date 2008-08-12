package jshm.gui.datamodels;

//import java.util.List;
//
//import jshm.gh.GhGame;
import jshm.gh.GhSong;

import org.netbeans.swing.outline.RowModel;

public class GhTiersAndSongDataRowModel implements RowModel {
//	private final GhGame game;
//	private final Difficulty difficulty;
//	private final List<GhSong> songs;
	
	public GhTiersAndSongDataRowModel() {
//		final GhGame game,
//		final Difficulty difficulty,
//		final List<GhSong> songs) {
		
//		this.game = game;
//		this.difficulty = difficulty;
//		this.songs = songs;
	}
	
	@Override
	public Class<?> getColumnClass(int arg0) {
		switch (arg0) {
			case 0: // noteCount
			case 1: // baseScore
			case 2: // 4*
			case 3: // 5*
			case 4: // 6*
			case 5: // 7*
			case 6: // 8*
				 return Integer.class;
				 
			default: assert false;
		}
		
		return null;
	}

	@Override
	public int getColumnCount() {
		return 7;
	}

	private static final String[] COLUMN_NAMES = {
		"Notes", "Base Score", "4* Cutoff", "5* Cutoff",
		"6* Cutoff", "7* Cutoff", "8* Cutoff"
	};
	
	@Override
	public String getColumnName(int arg0) {
		return COLUMN_NAMES[arg0];
	}

	@Override
	public Object getValueFor(Object arg0, int arg1) {
		if (!(arg0 instanceof GhSong)) return "";
		
		GhSong song = (GhSong) arg0;
		
		switch (arg1) {
			case 0: return song.getNoteCount(); // noteCount
			case 1: return song.getBaseScore(); // baseScore
			case 2: return song.getFourStarCutoff(); // 4*
			case 3: return song.getFiveStarCutoff();// 5*
			case 4: return song.getSixStarCutoff(); // 6*
			case 5: return song.getSevenStarCutoff(); // 7*
			case 6: return song.getEightStarCutoff(); // 8*
				
			default: assert false;
		}
		
		return "";
	}

	@Override
	public boolean isCellEditable(Object arg0, int arg1) {
		return false;
	}

	@Override
	public void setValueFor(Object arg0, int arg1, Object arg2) {

	}
}

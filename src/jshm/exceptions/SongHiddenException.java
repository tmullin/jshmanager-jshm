package jshm.exceptions;

import jshm.gui.datamodels.ScoresTreeTableModel;

/**
 * This exception gets thrown when an operation cannot continue
 * because a song is not visible. Currently used by
 * {@link ScoresTreeTableModel} when a user trys to scroll
 * to a hidden song.
 * 
 * @author Tim Mullin
 *
 */
public class SongHiddenException extends Exception {
	public SongHiddenException() {
		super();
	}
	
	public SongHiddenException(String msg) {
		super(msg);
	}
}

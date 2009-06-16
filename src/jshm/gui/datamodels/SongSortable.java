package jshm.gui.datamodels;

import jshm.Song.Sorting;

/**
 * Indicates that the implementing class supports sorting
 * of Songs in some way.
 * @author Tim Mullin
 *
 */
public interface SongSortable {
	public void setSorting(Sorting sorting);
}

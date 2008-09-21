package jshm.gui.datamodels;

import org.jdesktop.swingx.JXTreeTable;

/**
 * Interface to help data models bind to the JXTreeTable,
 * namely for adding and removing mouse listeners/etc.
 * @author Tim Mullin
 *
 */
public interface Parentable {
	public void setParent(JXTreeTable parent);
	public void removeParent(JXTreeTable parent);
}

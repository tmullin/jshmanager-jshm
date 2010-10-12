/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jshm.gui.renderers;

import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import jshm.*;

/**
 *
 * @author Tim
 */
public class GeneralListCellRenderer extends DefaultListCellRenderer {
	private boolean wtMode = false;

	public boolean isWtMode() {
		return wtMode;
	}

	public void setWtMode(boolean b) {
		wtMode = b;
	}

	@Override
	public Component getListCellRendererComponent(
		JList list,
		Object value,
		int index,
		boolean isSelected,
		boolean cellHasFocus) {

		if (value instanceof Game) {
//			value = ((Game) value)...
		} else if (value instanceof GameTitle) {
			value = ((GameTitle) value).getLongName();
		} else if (value instanceof Platform) {
			value = ((Platform) value).getShortName();
		} else if (value instanceof Difficulty) {
			value = ((Difficulty) value).getLongName();
		} else if (value instanceof Instrument) {
			value = ((Instrument) value).getLongName(wtMode);
		} else if (value instanceof Instrument.Group) {
			value = ((Instrument.Group) value).getLongName(wtMode);
		}

		return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
	}
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jshm.gui.renderers;

import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import jshm.*;

/**
 * <p>This {@link ListCellRenderer} can handle rendering of
 * {@link Game}, {@link GameTitle}, {@link Platform}, {@link Difficulty},
 * {@link Instrument}, and {@link Instrument.Group} to display their
 * "proper" name instead of their internal name.
 * <p>You can set the game for the renderer to determine whether
 * to display "Drums" or "RB Drums" for example.
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
	
	public void setGame(Game g) {
		setGameTitle(g.title);
	}
	
	public void setGameTitle(GameTitle gt) {
		setWtMode(null != gt && GameSeries.WORLD_TOUR == gt.series);
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

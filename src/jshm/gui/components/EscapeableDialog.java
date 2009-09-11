/*
 * -----LICENSE START-----
 * JSHManager - A Java-based tool for managing one's ScoreHero account.
 * Copyright (C) 2008, 2009 Tim Mullin
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * -----LICENSE END-----
*/
package jshm.gui.components;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;

/**
 * This class extends JDialog to fire a {@link WindowEvent#WINDOW_CLOSING}
 * event when the user presses the escape key.
 * @author Tim Mullin
 *
 */
public class EscapeableDialog extends JDialog {
	public EscapeableDialog() {
		this(null);
	}
	
	public EscapeableDialog(Frame parent) {
		this(parent, true);
	}
	
	public EscapeableDialog(Frame parent, boolean modal) {
		super(parent, modal);
	}
	
	protected JRootPane createRootPane() {
		JRootPane rootPane = super.createRootPane();
		KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
		Action actionListener = new AbstractAction() { 
			public void actionPerformed(ActionEvent actionEvent) { 
				EscapeableDialog.this.dispatchEvent(new WindowEvent( 
					EscapeableDialog.this, WindowEvent.WINDOW_CLOSING 
				)); 
			}
		};
		
		InputMap inputMap = rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		inputMap.put(stroke, "ESCAPE");
		rootPane.getActionMap().put("ESCAPE", actionListener);

		return rootPane;
	}
}

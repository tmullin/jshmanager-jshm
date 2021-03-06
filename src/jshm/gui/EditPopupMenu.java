/*
 * -----LICENSE START-----
 * JSHManager - A Java-based tool for managing one's ScoreHero account.
 * Copyright (C) 2008 Tim Mullin
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
package jshm.gui;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.text.JTextComponent;

/**
 * This is a JPopupMenu to handle Cut/Copy/Paste commands.
 * @author Tim Mullin
 *
 */
public class EditPopupMenu extends JPopupMenu implements ActionListener, MouseListener {
	private static EditPopupMenu instance = null;
	
	public static EditPopupMenu getInstance() {
		if (null == instance) instance = new EditPopupMenu();
		return instance;
	}
	
	public static void add(JTextComponent c) {
		getInstance().addPopupTo(c);
	}
	
	public static void remove(JTextComponent c) {
		getInstance().removePopupFrom(c);
	}
	
	
	
	private Clipboard cb;
	private JTextComponent comp = null;
	
	public EditPopupMenu() {
		cb = Toolkit.getDefaultToolkit().getSystemClipboard();		
		
		cutMenuItem = new JMenuItem("Cut", new ImageIcon(getClass().getResource("/jshm/resources/images/toolbar/cutb32.png")));
		cutMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_DOWN_MASK));
		cutMenuItem.addActionListener(this);
		copyMenuItem = new JMenuItem("Copy", new ImageIcon(getClass().getResource("/jshm/resources/images/toolbar/copy32.png")));
		copyMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK));
		copyMenuItem.addActionListener(this);
		pasteMenuItem = new JMenuItem("Paste", new ImageIcon(getClass().getResource("/jshm/resources/images/toolbar/paste32.png")));
		pasteMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_DOWN_MASK));
		pasteMenuItem.addActionListener(this);
		deleteMenuItem = new JMenuItem("Delete", new ImageIcon(getClass().getResource("/jshm/resources/images/toolbar/delete32.png")));
		deleteMenuItem.addActionListener(this);
		selectAllMenuItem = new JMenuItem("Select All");
		selectAllMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.CTRL_DOWN_MASK));
		selectAllMenuItem.addActionListener(this);
		cancelMenuItem = new JMenuItem("Cancel");
		cancelMenuItem.addActionListener(this);
		
		add(cutMenuItem);
		add(copyMenuItem);
		add(pasteMenuItem);
		add(deleteMenuItem);
		addSeparator();
		add(selectAllMenuItem);
		addSeparator();
		add(cancelMenuItem);
	}
	
	private void checkEnabled() {
		final boolean compNotNull = null != comp;
		final boolean compIsEditable = compNotNull && comp.isEditable();
		final boolean selectedNotNull = compNotNull && null != comp.getSelectedText();
		
		cutMenuItem.setEnabled(compIsEditable && selectedNotNull);
		copyMenuItem.setEnabled(selectedNotNull);
		pasteMenuItem.setEnabled(compIsEditable && cb.isDataFlavorAvailable(DataFlavor.stringFlavor));
		selectAllMenuItem.setEnabled(
			compNotNull &&
			null != comp.getText() && !comp.getText().isEmpty() &&
			(!selectedNotNull ||
			 comp.getSelectedText().length() != comp.getText().length())
		);
		deleteMenuItem.setEnabled(compIsEditable && selectedNotNull);
	}
	
	/**
	 * Attaches this EditPopupMenu to c.
	 * @param c
	 */
	public void addPopupTo(JTextComponent c) {
		c.addMouseListener(this);
	}
	
	public void removePopupFrom(JTextComponent c) {
		c.removeMouseListener(this);
	}
	
	// Implement MouseListener
	public void mousePressed(MouseEvent e) {
		maybeShowPopup(e);
	}

	public void mouseReleased(MouseEvent e) {
		maybeShowPopup(e);
	}

	private void maybeShowPopup(MouseEvent e) {
		if (e.isPopupTrigger()) {
			if (!(e.getComponent() instanceof JTextComponent)) return;
			comp = (JTextComponent) e.getComponent();
			checkEnabled();
			show(e.getComponent(), e.getX(), e.getY());
		}
	}

	public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	
	
	// Implement ActionListener
	
	public void actionPerformed(ActionEvent e) {
		if (null == comp || !comp.isVisible()) return;
		
		final Object src = e.getSource();
		
		try {
			if (src == cutMenuItem) {
				String text = comp.getSelectedText();
				comp.replaceSelection("");					
				StringSelection stringselected = new StringSelection(text);
				cb.setContents(stringselected, null);
			} else if (src == copyMenuItem) {
				String text = comp.getSelectedText();		
				StringSelection stringselected = new StringSelection(text);
				cb.setContents(stringselected, null);
			} else if (src == pasteMenuItem) {
				String pasteString = (String) cb.getData(DataFlavor.stringFlavor);
				comp.replaceSelection(pasteString);
			} else if (src == deleteMenuItem) {
				comp.replaceSelection("");
			} else if (src == selectAllMenuItem) {
				comp.selectAll();
			} else if (src == cancelMenuItem) {
				setVisible(false);
			}
		} catch (Exception ex) {
			// TODO log exception
		}
	}
	
	
	// GUI components
	
	private JMenuItem cutMenuItem;
	private JMenuItem copyMenuItem;
	private JMenuItem pasteMenuItem;
	private JMenuItem deleteMenuItem;
	private JMenuItem selectAllMenuItem;
	private JMenuItem cancelMenuItem;
}

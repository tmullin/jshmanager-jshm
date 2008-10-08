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

import java.awt.event.*;
import java.util.List;

import javax.swing.*;

import jshm.Song;
import jshm.gui.datamodels.GhMyScoresTreeTableModel;
import jshm.sh.URLs;
import jshm.sh.links.Link;
import jshm.sh.scraper.WikiSpScraper;
import jshm.util.Util;

import org.jdesktop.swingx.JXTreeTable;

/**
 * This is a JPopupMenu for use with GUI.tree.
 * @author Tim Mullin
 *
 */
public class TreePopupMenu extends JPopupMenu implements ActionListener, MouseListener {
	private GUI gui;
	private JXTreeTable comp;
	private Song selected = null;
	private SpChartsMenuMouseListener spChartsMenuMouseListener;
	
	public TreePopupMenu(GUI gui, JXTreeTable comp) {
		this.gui = gui;
		this.comp = comp;
		
		rankingsPageMenuItem = new JMenuItem("Go to song's rankings page");
		rankingsPageMenuItem.addActionListener(this);
		wikiPageMenuItem = new JMenuItem("Go to song's wiki page");
		wikiPageMenuItem.addActionListener(this);
		spPageMenuItem = new JMenuItem("Go to song's SP page");
		spPageMenuItem.addActionListener(this);
		spChartsMenu = new JMenu("View chart from wiki");
			loadingChartsMenuItem = new JMenuItem("Loading...");
			loadingChartsMenuItem.setEnabled(false);
			noChartsMenuItem = new JMenuItem("None available");
			noChartsMenuItem.setEnabled(false);
		cancelMenuItem = new JMenuItem("Cancel");
		cancelMenuItem.addActionListener(this);
		
		add(rankingsPageMenuItem);
		add(wikiPageMenuItem);
		add(spPageMenuItem);
		add(spChartsMenu);
		spChartsMenuMouseListener = new SpChartsMenuMouseListener();
			spChartsMenu.add(loadingChartsMenuItem);
		addSeparator();
		add(cancelMenuItem);
		
		addPopupTo();	
	}
	
	private void checkEnabled() {
//		final boolean compNotNull = null != comp;

	}
	
	/**
	 * Attaches this EditPopupMenu to c.
	 * @param c
	 */
	public void addPopupTo() {
		comp.addMouseListener(this);
	}
	
	public void removePopupFrom() {
		comp.removeMouseListener(this);
	}
	
	// Implement MouseListener
	public void mousePressed(MouseEvent e) {
		maybeShowPopup(e);
	}

	public void mouseReleased(MouseEvent e) {
		maybeShowPopup(e);
	}

	private void maybeShowPopup(MouseEvent e) {
		selected = null;
		
		if (e.isPopupTrigger()) {
			if (!(e.getComponent() instanceof JXTreeTable)) return;
			comp = (JXTreeTable) e.getComponent();
			
			if (!(comp.getTreeTableModel() instanceof GhMyScoresTreeTableModel)) return;
			
			Object hovered = comp.getValueAt(comp.rowAtPoint(e.getPoint()), 0);
			
//			System.out.println("Hovered: " + hovered);
			
			if (null == hovered || !(hovered instanceof GhMyScoresTreeTableModel.SongScores)) return;
			
			selected = 
				((GhMyScoresTreeTableModel.SongScores) hovered).song;
			
			checkEnabled();
			spChartsMenuMouseListener.mouseEntered(null);
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
			assert selected != null;
			
			if (src == rankingsPageMenuItem) { 
				if (null != selected)
					Util.openURL(
						selected.getRankingsUrl(
							gui.getCurGame(),
							gui.getCurGroup(),
							gui.getCurDiff()));
			} else if (src == wikiPageMenuItem) {
				if (null != selected)
					Util.openURL(URLs.wiki.getSongUrl(selected));
			} else if (src == spPageMenuItem) {
				if (null != selected)
					Util.openURL(
						URLs.wiki.getSongSpUrl(
							selected, gui.getCurGroup(), gui.getCurDiff()));
			} else if (src == cancelMenuItem) {
				setVisible(false);
			}
		} catch (Exception ex) {
			// TODO log exception
		}
	}
	
	
	// GUI components
	
	private JMenuItem
		rankingsPageMenuItem,
		wikiPageMenuItem,
		spPageMenuItem,
		loadingChartsMenuItem,
		noChartsMenuItem,
		cancelMenuItem;
	
	private JMenu
		spChartsMenu;
	
	
	private class SpChartsMenuMouseListener implements MouseListener {
		public void mouseEntered(MouseEvent e) {
			spChartsMenu.removeAll();
			spChartsMenu.add(loadingChartsMenuItem);
			spChartsMenu.validate();
			
			new SwingWorker<Void, Void>() {
				List<Link> links = null;
				
				protected Void doInBackground() {
					try {
						links = WikiSpScraper.scrape(selected, gui.getCurGroup(), gui.getCurDiff());
					} catch (Exception x) {
						x.printStackTrace();
					}
					
					return null;
				}
				
				public void done() {
					spChartsMenu.removeAll();
					
					if (null == links || links.size() == 0) {
						spChartsMenu.add(noChartsMenuItem);
					} else {
						for (final Link l : links) {
							JMenuItem item = new JMenuItem(l.name);
							item.setToolTipText(l.getUrl());
							
							item.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent e) {
									GuiUtil.openImageOrBrowser(gui, l.getUrl());
								}
							});
							
							spChartsMenu.add(item);
						}
					}
				}
			}.execute();
		}

		public void mouseExited(MouseEvent e) {}
		public void mouseClicked(MouseEvent e) {}
		public void mousePressed(MouseEvent e) {}
		public void mouseReleased(MouseEvent e) {}
	}
}

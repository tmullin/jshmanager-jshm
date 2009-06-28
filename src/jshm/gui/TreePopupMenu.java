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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingWorker;

import jshm.Score;
import jshm.Song;
import jshm.gui.datamodels.ScoresTreeTableModel;
import jshm.sh.URLs;
import jshm.sh.links.Link;
import jshm.sh.scraper.wiki.SpChartScraper;
import jshm.sh.scraper.wiki.SpPathScraper;
import jshm.sh.scraper.wiki.SpPathScraper.PathInfo;
import jshm.util.Util;

import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.error.ErrorInfo;

/**
 * This is a JPopupMenu for use with GUI.tree.
 * @author Tim Mullin
 *
 */
public class TreePopupMenu extends JPopupMenu implements ActionListener, MouseListener {
	private GUI gui;
	private JXTreeTable comp;
	private Song selectedSong = null;
	private Score selectedScore = null;
	private SpChartsMenuMouseListener spChartsMenuMouseListener;
	
	public TreePopupMenu(GUI gui, JXTreeTable comp) {
		this.gui = gui;
		this.comp = comp;
		
		insertScoreMenuItem = new JMenuItem(gui.actions.addNewScore);
		editScoreMenuItem = new JMenuItem(gui.actions.editSelectedScore);
		uploadScoreMenuItem = new JMenuItem(gui.actions.uploadSelectedScore);
		deleteScoreMenuItem = new JMenuItem(gui.actions.deleteSelectedScore);
		
		gotoMenu = new JMenu("Go to");
		gotoMenu.setIcon(gui.goToWikiPageMenuItem.getIcon());
		gotoMenu.setMnemonic('G');
			rankingsPageMenuItem = new JMenuItem("song's rankings page", 'r');
			rankingsPageMenuItem.addActionListener(this);
			wikiPageMenuItem = new JMenuItem("song's wiki page", 'w');
			wikiPageMenuItem.addActionListener(this);
			spPageMenuItem = new JMenuItem("song's SP page", 'S');
			spPageMenuItem.addActionListener(this);
		
		spChartsMenu = new JMenu("View chart from wiki");
		spChartsMenu.setIcon(gui.searchWikiMenuItem.getIcon());
		spChartsMenu.setMnemonic('V');
			loadingChartsMenuItem = new JMenuItem("Loading...");
			loadingChartsMenuItem.setEnabled(false);
			noChartsMenuItem = new JMenuItem("None available");
			noChartsMenuItem.setEnabled(false);
			
		userPathsMenuItem = new JMenuItem("View paths from wiki");
		userPathsMenuItem.setIcon(gui.searchWikiMenuItem.getIcon());
		userPathsMenuItem.addActionListener(this);
			
		cancelMenuItem = new JMenuItem("Cancel", 'C');
		cancelMenuItem.addActionListener(this);
		
		add(insertScoreMenuItem);
		add(editScoreMenuItem);
		add(uploadScoreMenuItem);
		add(deleteScoreMenuItem);
		addSeparator();
		add(gotoMenu);
			gotoMenu.add(rankingsPageMenuItem);
			gotoMenu.add(wikiPageMenuItem);
			gotoMenu.add(spPageMenuItem);
		add(spChartsMenu);
		spChartsMenuMouseListener = new SpChartsMenuMouseListener();
			spChartsMenu.add(loadingChartsMenuItem);
		add(userPathsMenuItem);
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
		selectedSong = null;
		selectedScore = null;
		
		if (e.isPopupTrigger()) {
			if (!(e.getComponent() instanceof JXTreeTable)) return;
			comp = (JXTreeTable) e.getComponent();
			
			if (!(comp.getTreeTableModel() instanceof ScoresTreeTableModel)) return;
			
			int row = comp.rowAtPoint(e.getPoint());
			comp.getSelectionModel().setSelectionInterval(row, row);
			comp.repaint();
			
//			Object hovered = comp.getValueAt(comp.rowAtPoint(e.getPoint()), 0);
			Object hovered = comp.getPathForRow(row).getLastPathComponent();
			
//			System.out.println("Hovered: " + hovered);
			
			if (null == hovered) return;
			
			editScoreMenuItem.setVisible(false);
			uploadScoreMenuItem.setVisible(false);
			deleteScoreMenuItem.setVisible(false);
			
			if (hovered instanceof ScoresTreeTableModel.SongScores) {
				selectedSong = 
					((ScoresTreeTableModel.SongScores) hovered).song;
			} else if (hovered instanceof Score) {
				selectedScore = (Score) hovered;
				selectedSong = selectedScore.getSong();
				
				editScoreMenuItem.setVisible(selectedScore.isEditable());
				uploadScoreMenuItem.setVisible(selectedScore.isSubmittable());
				deleteScoreMenuItem.setVisible(true);
			} else {
//				System.out.println("Class of hovered: " + hovered.getClass().getName());
				return;
			}
			
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
			assert selectedSong != null;
			
			if (src == rankingsPageMenuItem) { 
				if (null != selectedSong)
					Util.openURL(
						selectedSong.getRankingsUrl(
							gui.getCurGame(),
							gui.getCurGroup(),
							gui.getCurDiff()));
			} else if (src == wikiPageMenuItem) {
				if (null != selectedSong)
					Util.openURL(URLs.wiki.getSongUrl(selectedSong));
			} else if (src == spPageMenuItem) {
				if (null != selectedSong)
					Util.openURL(
						URLs.wiki.getSongSpUrl(
							selectedSong, gui.getCurGroup(), gui.getCurDiff()));
			} else if (src == userPathsMenuItem) {
				new SwingWorker<Void, Void>() {
					List<PathInfo> paths = null;
					Throwable t = null;
					
					protected Void doInBackground() throws Exception {
						try {
							paths = SpPathScraper.scrape(
								selectedSong, gui.getCurGroup(), gui.getCurDiff());
						} catch (Exception e) {
							t = e;
						}
						
						return null;
					}
					
					public void done() {
						if (null != t) {
//							LOG.log(Level.SEVERE, "Failed to load score data from database", e);
							ErrorInfo ei = new ErrorInfo("Error", "Failed to retrieve paths from wiki.", null, null, t, null, null);
							org.jdesktop.swingx.JXErrorPane.showDialog(gui, ei);
						} else if (null == paths || paths.size() == 0) {
							JOptionPane.showMessageDialog(gui,
								"The wiki didn't have any paths for that song.",
								"No paths found",
								JOptionPane.INFORMATION_MESSAGE);
						} else {
							WikiSpPathsFrame frame = new WikiSpPathsFrame();
							frame.setSong(selectedSong, gui.getCurGroup(), gui.getCurDiff());
							frame.addPaths(paths);
							frame.setLocationRelativeTo(gui);
							frame.setIconImage(gui.getIconImage());
							frame.setVisible(true);
						}
					}
				}.execute();
			} else if (src == cancelMenuItem) {
				setVisible(false);
			}
		} catch (Exception ex) {
			// TODO log exception
		}
	}
	
	
	// GUI components
	
	private JMenuItem
		insertScoreMenuItem,
		editScoreMenuItem,
		uploadScoreMenuItem,
		deleteScoreMenuItem,
		
		rankingsPageMenuItem,
		wikiPageMenuItem,
		spPageMenuItem,
		
		loadingChartsMenuItem,
		noChartsMenuItem,
		
		userPathsMenuItem,
		cancelMenuItem;
	
	private JMenu
		gotoMenu,
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
						links = SpChartScraper.scrape(selectedSong, gui.getCurGroup(), gui.getCurDiff());
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
							item.setToolTipText("<html>" + l.getUrl() + "<br>This will attempt to open in the internal image viewer.<br>Ctrl-click to open in your browser.");
							
							item.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent e) {
									int mods = e.getModifiers();
									
									// force opening in a browser regardless
									if ((mods & ActionEvent.CTRL_MASK) == ActionEvent.CTRL_MASK)
										Util.openURL(l.getUrl());
									else
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

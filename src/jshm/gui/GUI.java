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
/*
 * GUITest.java
 *
 * Created on August 10, 2008, 2:33 AM
 */

package jshm.gui;


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.tree.TreePath;

import jshm.Config;
import jshm.Difficulty;
import jshm.Game;
import jshm.GameSeries;
import jshm.GameTitle;
import jshm.Instrument;
import jshm.JSHManager;
import jshm.Score;
import jshm.Song;
import jshm.UpdateChecker;
import jshm.Instrument.Group;
import jshm.Song.Sorting;
import jshm.exceptions.SongHiddenException;
import jshm.gh.GhGame;
import jshm.gh.GhGameTitle;
import jshm.gui.components.StatusBar;
import jshm.gui.datamodels.GhSongDataTreeTableModel;
import jshm.gui.datamodels.Parentable;
import jshm.gui.datamodels.RbSongDataTreeTableModel;
import jshm.gui.datamodels.ScoresTreeTableModel;
import jshm.gui.datamodels.SongSortable;
import jshm.gui.datamodels.WtSongDataTreeTableModel;
import jshm.gui.notificationbars.SelectSongBar;
import jshm.gui.wizards.csvimport.CsvImportWizard;
import jshm.gui.wizards.scoredownload.ScoreDownloadWizard;
import jshm.gui.wizards.scoreupload.ScoreUploadWizard;
import jshm.rb.RbGame;
import jshm.rb.RbSong;
import jshm.sh.URLs;
import jshm.util.PasteBin;
import jshm.wt.WtGame;
import jshm.wt.WtGameTitle;
import jshm.wt.WtSong;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;
import org.jdesktop.swingx.treetable.TreeTableModel;
import org.netbeans.spi.wizard.Wizard;

/**
 *
 * @author Tim Mullin
 */
public class GUI extends javax.swing.JFrame {
	public static final Logger LOG = Logger.getLogger(GUI.class.getName());
	
	/**
	 * The "real" control key accelerator mask via {@link Toolkit#getMenuShortcutKeyMask()}. 
	 */
	public static final int CTRL_MASK = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
		
	private Instrument.Group curGroup = null;
	private Game curGame = null;
	private Difficulty curDiff = null;
	private Sorting curSorting = Sorting.SCOREHERO;
	
	private List<? extends Song> orderedSongs = null;
	
	private HoverHelp hh = null;
	TreePopupMenu treePopup = null;
	
	private SelectSongBar selectSongBar = null;
	
    /** Creates new form GUITest */
    public GUI() {    	
    	GuiUtil.init();
    	
    	hh = new HoverHelp();
    	
        initComponents();

//        System.out.println("Action keys:");
//        for (Object o : jXTreeTable1.getActionMap().allKeys()) {
//        	System.out.println(o);
//        }
//        
//        System.out.println("\nInput keys:");
//        for (KeyStroke o : 
//        	jXTreeTable1
//        		.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
//        			.allKeys()) {
//        	System.out.println(o + ": " + jXTreeTable1.getInputMap().get(o));
//        }
        
        
        // fix the tree table so that pressing left/right arrow will collapse/expand the current
        // row while maintaining cell selection on leaf rows
        
        tree.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "expandCurrentRow");
        tree.getActionMap().put("expandCurrentRow", new AbstractAction() {
        	Action parent = tree.getActionMap().get("selectNextColumnCell");
        	
			@Override
			public void actionPerformed(ActionEvent e) {
				int row = tree.getSelectedRow();
				if (row == -1) return;
				TreePath p = tree.getPathForRow(row);
				
				if (tree.getTreeTableModel().isLeaf(p.getLastPathComponent())) {
					parent.actionPerformed(e);
				} else {
					tree.expandRow(row);
				}
			}
        });
        
        tree.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "collapseCurrentRow");
        tree.getActionMap().put("collapseCurrentRow", new AbstractAction() {
        	Action parent = tree.getActionMap().get("selectPreviousColumnCell");
        	
			@Override
			public void actionPerformed(ActionEvent e) {
				int row = tree.getSelectedRow();
				if (row == -1) return;
				TreePath p = tree.getPathForRow(row);
				
				if (tree.getTreeTableModel().isLeaf(p.getLastPathComponent())) {
					parent.actionPerformed(e);
				} else {
					tree.collapseRow(row);
				}
			}
        });
        
        
        
        setTitle("");
        setSize(Config.getInt("window.width"), Config.getInt("window.height"));
        setLocation(Config.getInt("window.x"), Config.getInt("window.y"));
        
        if (Config.getBool("window.maximized")) {
			setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
        }
        
        hh.setStatus(statusBar1);
        
        hh.add(downloadGhSongDataMenuItem);
        hh.add(tree, new HoverHelp.Callback() {
			@Override
			public String getMessage() {
//				if (!jXTreeTable1.isEditing()) return null;
				if (!(tree.getTreeTableModel() instanceof ScoresTreeTableModel)) return null;
				
				Point p = tree.getMousePosition(true);
				
				if (null == p) return null;
				
				int row = tree.rowAtPoint(p);
				int col = tree.columnAtPoint(p);
				
				if (!(tree.isCellEditable(row, col))) return null;
				
//				int editRow = jXTreeTable1.getEditingRow();
//				int editCol = jXTreeTable1.getEditingColumn();
//				
//				if (row != editRow || col != editCol) return null;
//				
				switch (col) {
					case 0:
						return "Double-click to enter a comment for this score or leave blank";
					case 1:
						return "Double-click to enter a score that is greater than 0";
					case 2:
						return "Click to select the rating or leave blank (it will be calculated from the score if possible)";
					case 3:
						return "Double-click to enter a hit percentage between 1 and 100 or leave blank";
					case 4:
						return "Double-click to enter a note streak that is greater than 0 or leave blank";
				}
				
				return null;
			}
        });
        
        
        treePopup = new TreePopupMenu(this, tree);
        
        
        // set up songSelectBar
        selectSongBar = new SelectSongBar();
        selectSongBar.getSelectButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Song song = selectSongBar.getSelectedSong();

				if (null != song) {
					assert song.getActiveGame() == getCurGame();
					assert tree.getTreeTableModel() instanceof ScoresTreeTableModel;
					
					try {
						((ScoresTreeTableModel) tree.getTreeTableModel())
							.expandAndScrollTo(song, true);
					} catch (SongHiddenException e1) {
						JOptionPane.showMessageDialog(GUI.this,
							"That song is hidden because it doesn't have any scores\n" +
							"and you're hiding songs without scores.",
							"Notice", JOptionPane.WARNING_MESSAGE);
					}
				}
			}
		});
        
        
        // check for updates
        new SwingWorker<Void, Void>() {
        	UpdateChecker.Info info = null;
        	
			protected Void doInBackground() throws Exception {
				info = UpdateChecker.getInfo();
				return null;
			}
        	
			protected void done() {
				if (null == info || !info.isUpdateAvailable())
					return;
				
				statusBar1.setExtraLink("JSHManager update available", info.getUpdateUrl());
			}
        }.execute();


		gamePanel.gui = this;

        
        // restore last game/group/diff
        if (true) {
        	Sorting lastSorting =
        		Sorting.valueOf(Config.get("window.lastsorting"));
        	
        	if (null != lastSorting) {
        		// don't want to call setCurSorting(), just set the value itself,
        		// all will be taken care of in initSongSortingMenu()
        		curSorting = lastSorting;
        	}

    		hideEmptySongsMenuItem.setSelected(
    			Config.getBool("window.hideemptysongs"));
        	
	        Game lastGame = null;
	        Group lastGroup = null;
	        Difficulty lastDiff = null;
	        
	        try {
		        lastGame = Game.valueOf(Config.get("window.lastgame"));
		        lastGroup = Group.valueOf(Config.get("window.lastgroup"));
		        lastDiff = Difficulty.valueOf(Config.get("window.lastdiff"));
	        } catch (Exception e) {}
	        
	        if (lastGame != null && lastGroup != null && lastDiff != null) {
	            LOG.finer(String.format("Restoring game/group/diff: %s/%s/%s",
	                lastGame, lastGroup, lastDiff));

				gamePanel.setCombos(lastGame, lastGroup, lastDiff);
	        	myScoresMenuItemActionPerformed(null, lastGame, lastGroup, lastDiff);
	        } else {
	        	LOG.finer("No previous game/group/diff found");
	        }
        }
    }

    @Override
    public void setTitle(String title) {
    	super.setTitle(
    		(title.isEmpty() ? "" : title + " - ") +
    		JSHManager.Version.NAME + " " + JSHManager.Version.STRING);
    }
    
    public void setIconImage(ImageIcon icon) {
    	super.setIconImage(null != icon ? icon.getImage() : null);
    }
    
    @Override
    public void dispose() {    	
    	Dimension size = getSize();
    	Point loc = getLocation();
    	final boolean isMaximized = JFrame.MAXIMIZED_BOTH ==
    		(getExtendedState() & JFrame.MAXIMIZED_BOTH);
    	
    	LOG.finer(
    		String.format("Saving GUI's location %s and size %s, maximized=%s",
    		loc, size, isMaximized));
    	
    	// no need to save this stuff if the frame is maximized
    	if (!isMaximized) {
    		Config.set("window.maximized", false);
    		
    		Config.set("window.width", size.width);
    		Config.set("window.height", size.height);
    		Config.set("window.x", loc.x);
    		Config.set("window.y", loc.y);
    	} else {
    		Config.set("window.maximized", true);
    	}	
    	
    	size = textFileViewerDialog1.getSize();
    	LOG.finer("Saving TextFileViewer's size " + size);
		Config.set("window.textfileviewer.width", size.width);
		Config.set("window.textfileviewer.height", size.height);
    	
		
		// now save last game/group/diff
		LOG.finer(
			String.format("Saving current game/group/diff: %s/%s/%s",
				curGame, curGroup, curDiff));
		
		Config.set("window.lastgame", null == curGame ? "" : curGame);
		Config.set("window.lastgroup", null == curGroup ? "" : curGroup);
		Config.set("window.lastdiff", null == curDiff ? "" : curDiff);
		
		Config.set("window.lastsorting", null == curSorting ? "" : curSorting);
		Config.set("window.hideemptysongs", hideEmptySongsMenuItem.isSelected());
		
    	super.dispose();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
//    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        aboutDialog1 = new AboutDialog(this);
        textFileViewerDialog1 = new TextFileViewerDialog(this, true);
        checkUpdatesDialog1 = new jshm.gui.CheckUpdatesDialog();
        innerPanel3 = new javax.swing.JPanel();
        innerPanel1 = new javax.swing.JPanel();
        innerPanel2 = new javax.swing.JPanel();
        treeScrollPane = new javax.swing.JScrollPane();
        tree = new org.jdesktop.swingx.JXTreeTable();
        upperNotificationBar = new jshm.gui.notificationbars.NotificationBar();
        lowerNotificationBar = new jshm.gui.notificationbars.NotificationBar();
        statusBar1 = new jshm.gui.components.StatusBar();
        editorCollapsiblePane = new org.jdesktop.swingx.JXCollapsiblePane();
        scoreEditorPanel1 = new ScoreEditorPanel(this);
        gamePanel = new jshm.gui.GameSelectorPanel();
        toolbar = new javax.swing.JToolBar();
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        songSortingMenu = new javax.swing.JMenu();
        hideEmptySongsMenuItem = new javax.swing.JCheckBoxMenuItem();
        jSeparator3 = new javax.swing.JSeparator();
        exitMenuItem = new javax.swing.JMenuItem();
        scoresMenu = new javax.swing.JMenu();
        ghMenu = new javax.swing.JMenu();
        ghScoresMenu = new javax.swing.JMenu();
        ghSongDataMenu = new javax.swing.JMenu();
        downloadGhSongDataMenuItem = new javax.swing.JMenuItem();
        downloadGhSongMetaDataMenuItem = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JSeparator();
        ghLinksMenu = new javax.swing.JMenu();
        rbMenu = new javax.swing.JMenu();
        rbScoresMenu = new javax.swing.JMenu();
        rbSongDataMenu = new javax.swing.JMenu();
        downloadRbSongDataMenuItem = new javax.swing.JMenuItem();
        downloadRbSongMetaDataMenuItem = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JSeparator();
        rbLinksMenu = new javax.swing.JMenu();
        wikiMenu = new javax.swing.JMenu();
        searchWikiMenuItem = new javax.swing.JMenuItem();
        goToWikiPageMenuItem = new javax.swing.JMenuItem();
        jSeparator8 = new javax.swing.JSeparator();
        helpMenu = new javax.swing.JMenu();
        readmeMenuItem = new javax.swing.JMenuItem();
        changeLogMenuItem = new javax.swing.JMenuItem();
        licenseMenuItem = new javax.swing.JMenuItem();
        viewLogMenu = new javax.swing.JMenu();
        uploadLogsMenuItem = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator4 = new javax.swing.JSeparator();
        checkForUpdatesMenuItem = new javax.swing.JMenuItem();
        aboutMenuItem = new javax.swing.JMenuItem();

        textFileViewerDialog1.setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
        });

        innerPanel3.setLayout(new java.awt.BorderLayout());

        innerPanel1.setLayout(new java.awt.BorderLayout());

        innerPanel2.setLayout(new java.awt.BorderLayout());

        tree.setColumnControlVisible(true);
        tree.setPreferredScrollableViewportSize(new java.awt.Dimension(800, 600));
        tree.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tree.addTreeExpansionListener(new javax.swing.event.TreeExpansionListener() {
            public void treeCollapsed(javax.swing.event.TreeExpansionEvent evt) {
                treeTreeCollapsed(evt);
            }
            public void treeExpanded(javax.swing.event.TreeExpansionEvent evt) {
                treeTreeExpanded(evt);
            }
        });
        tree.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                treeValueChanged(evt);
            }
        });
        treeScrollPane.setViewportView(tree);

        innerPanel2.add(treeScrollPane, java.awt.BorderLayout.CENTER);
        innerPanel2.add(upperNotificationBar, java.awt.BorderLayout.NORTH);
        innerPanel2.add(lowerNotificationBar, java.awt.BorderLayout.SOUTH);

        innerPanel1.add(innerPanel2, java.awt.BorderLayout.CENTER);
        innerPanel1.add(statusBar1, java.awt.BorderLayout.SOUTH);

        editorCollapsiblePane.setCollapsed(true);
        editorCollapsiblePane.getContentPane().setLayout(new java.awt.BorderLayout());
        editorCollapsiblePane.getContentPane().add(scoreEditorPanel1, java.awt.BorderLayout.CENTER);

        innerPanel1.add(editorCollapsiblePane, java.awt.BorderLayout.NORTH);

        innerPanel3.add(innerPanel1, java.awt.BorderLayout.CENTER);
        innerPanel3.add(gamePanel, java.awt.BorderLayout.NORTH);

        getContentPane().add(innerPanel3, java.awt.BorderLayout.CENTER);

        toolbar.setRollover(true);
        initToolbar(toolbar);
        getContentPane().add(toolbar, java.awt.BorderLayout.NORTH);

        fileMenu.setMnemonic('F');
        fileMenu.setText("File");

        songSortingMenu.setMnemonic('S');
        songSortingMenu.setText("Sort Songs by");
        songSortingMenu.setEnabled(false);

        initSongSortingMenu();

        fileMenu.add(songSortingMenu);

        hideEmptySongsMenuItem.setMnemonic('H');
        hideEmptySongsMenuItem.setSelected(true);
        hideEmptySongsMenuItem.setText("Hide Empty Songs");
        hideEmptySongsMenuItem.setToolTipText("Don't show songs or tiers with no scores");
        hideEmptySongsMenuItem.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                hideEmptySongsMenuItemItemStateChanged(evt);
            }
        });
        fileMenu.add(hideEmptySongsMenuItem);
        fileMenu.add(jSeparator3);

        exitMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.ALT_MASK));
        exitMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jshm/resources/images/toolbar/close32.png"))); // NOI18N
        exitMenuItem.setMnemonic('x');
        exitMenuItem.setText("Exit");
        exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        scoresMenu.setMnemonic('S');
        scoresMenu.setText("Scores");

        initScoresMenu(scoresMenu);

        menuBar.add(scoresMenu);

        ghMenu.setMnemonic('G');
        ghMenu.setText("Guitar Hero");

        ghScoresMenu.setMnemonic('S');
        ghScoresMenu.setText("Scores");

        initGhGameMenu(ghScoresMenu);

        ghMenu.add(ghScoresMenu);

        ghSongDataMenu.setMnemonic('D');
        ghSongDataMenu.setText("Song Data");

        downloadGhSongDataMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jshm/resources/images/toolbar/down32.png"))); // NOI18N
        downloadGhSongDataMenuItem.setMnemonic('L');
        downloadGhSongDataMenuItem.setText("Download...");
        downloadGhSongDataMenuItem.setToolTipText("Sync the local song list for the current game and difficulty to ScoreHero's (e.g. when there is new DLC)");
        downloadGhSongDataMenuItem.setEnabled(false);
        downloadGhSongDataMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                downloadGhSongDataMenuItemActionPerformed(evt);
            }
        });
        ghSongDataMenu.add(downloadGhSongDataMenuItem);

        downloadGhSongMetaDataMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jshm/resources/images/toolbar/down32.png"))); // NOI18N
        downloadGhSongMetaDataMenuItem.setText("Download Meta Data...");
        downloadGhSongMetaDataMenuItem.setToolTipText("Download only song meta data (artist, genre, etc.)");
        downloadGhSongMetaDataMenuItem.setEnabled(false);
        downloadGhSongMetaDataMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                downloadGhSongMetaDataMenuItemActionPerformed(evt);
            }
        });
        ghSongDataMenu.add(downloadGhSongMetaDataMenuItem);
        ghSongDataMenu.add(jSeparator2);

        initGhGameMenu(ghSongDataMenu);

        ghMenu.add(ghSongDataMenu);

        ghLinksMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jshm/resources/images/toolbar/gowebsite32.png"))); // NOI18N
        ghLinksMenu.setMnemonic('L');
        ghLinksMenu.setText("Web Links");

        initForumsMenu(ghLinksMenu);

        ghMenu.add(ghLinksMenu);

        menuBar.add(ghMenu);

        rbMenu.setMnemonic('R');
        rbMenu.setText("Rock Band");

        rbScoresMenu.setMnemonic('S');
        rbScoresMenu.setText("Scores");

        initRbGameMenu(rbScoresMenu);

        rbMenu.add(rbScoresMenu);

        rbSongDataMenu.setMnemonic('D');
        rbSongDataMenu.setText("Song Data");

        downloadRbSongDataMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jshm/resources/images/toolbar/down32.png"))); // NOI18N
        downloadRbSongDataMenuItem.setText("Download...");
        downloadRbSongDataMenuItem.setEnabled(false);
        downloadRbSongDataMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                downloadRbSongDataMenuItemActionPerformed(evt);
            }
        });
        rbSongDataMenu.add(downloadRbSongDataMenuItem);

        downloadRbSongMetaDataMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jshm/resources/images/toolbar/down32.png"))); // NOI18N
        downloadRbSongMetaDataMenuItem.setText("Download Meta Data...");
        downloadRbSongMetaDataMenuItem.setToolTipText("Download only song meta data (artist, genre, etc.)");
        downloadRbSongMetaDataMenuItem.setEnabled(false);
        downloadRbSongMetaDataMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                downloadRbSongMetaDataMenuItemActionPerformed(evt);
            }
        });
        rbSongDataMenu.add(downloadRbSongMetaDataMenuItem);
        rbSongDataMenu.add(jSeparator5);

        initRbGameMenu(rbSongDataMenu);

        rbMenu.add(rbSongDataMenu);

        rbLinksMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jshm/resources/images/toolbar/gowebsite32.png"))); // NOI18N
        rbLinksMenu.setMnemonic('L');
        rbLinksMenu.setText("Web Links");

        initForumsMenu(rbLinksMenu);

        rbMenu.add(rbLinksMenu);

        menuBar.add(rbMenu);

        wikiMenu.setMnemonic('W');
        wikiMenu.setText("Wiki");

        searchWikiMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jshm/resources/images/toolbar/websearch32.png"))); // NOI18N
        searchWikiMenuItem.setMnemonic('S');
        searchWikiMenuItem.setText("Search...");
        searchWikiMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchWikiMenuItemActionPerformed(evt);
            }
        });
        wikiMenu.add(searchWikiMenuItem);

        goToWikiPageMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jshm/resources/images/toolbar/gowebsite32.png"))); // NOI18N
        goToWikiPageMenuItem.setMnemonic('G');
        goToWikiPageMenuItem.setText("Go to page...");
        goToWikiPageMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                goToWikiPageMenuItemActionPerformed(evt);
            }
        });
        wikiMenu.add(goToWikiPageMenuItem);
        wikiMenu.add(jSeparator8);

        initForumsMenu(wikiMenu);

        menuBar.add(wikiMenu);

        helpMenu.setMnemonic('H');
        helpMenu.setText("Help");

        readmeMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));
        readmeMenuItem.setMnemonic('R');
        readmeMenuItem.setText("View Readme");
        readmeMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                readmeMenuItemActionPerformed(evt);
            }
        });
        helpMenu.add(readmeMenuItem);

        changeLogMenuItem.setMnemonic('C');
        changeLogMenuItem.setText("View ChangeLog");
        changeLogMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changeLogMenuItemActionPerformed(evt);
            }
        });
        helpMenu.add(changeLogMenuItem);

        licenseMenuItem.setMnemonic('L');
        licenseMenuItem.setText("View License");
        licenseMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                licenseMenuItemActionPerformed(evt);
            }
        });
        helpMenu.add(licenseMenuItem);

        viewLogMenu.setMnemonic('g');
        viewLogMenu.setText("View Log");

        uploadLogsMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jshm/resources/images/toolbar/up32.png"))); // NOI18N
        uploadLogsMenuItem.setMnemonic('U');
        uploadLogsMenuItem.setText("Upload for Debugging...");
        uploadLogsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                uploadLogsMenuItemActionPerformed(evt);
            }
        });
        viewLogMenu.add(uploadLogsMenuItem);
        viewLogMenu.add(jSeparator1);

        initViewLogMenu(viewLogMenu);

        helpMenu.add(viewLogMenu);
        helpMenu.add(jSeparator4);

        checkForUpdatesMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jshm/resources/images/toolbar/websearch32.png"))); // NOI18N
        checkForUpdatesMenuItem.setText("Check for updates");
        checkForUpdatesMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkForUpdatesMenuItemActionPerformed(evt);
            }
        });
        helpMenu.add(checkForUpdatesMenuItem);

        aboutMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jshm/resources/images/toolbar/infoabout32.png"))); // NOI18N
        aboutMenuItem.setMnemonic('A');
        aboutMenuItem.setText("About");
        aboutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutMenuItemActionPerformed(evt);
            }
        });
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        setJMenuBar(menuBar);

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitMenuItemActionPerformed
	formWindowClosing(null);
}//GEN-LAST:event_exitMenuItemActionPerformed

private void aboutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutMenuItemActionPerformed
	// this.aboutDialog1
	this.aboutDialog1.setLocationRelativeTo(this);
	this.aboutDialog1.setVisible(true);
}//GEN-LAST:event_aboutMenuItemActionPerformed

private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
//	try {
//		jXTreeTable1.packAll();
//	} catch (Exception e) {}
}//GEN-LAST:event_formComponentResized

private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
//	this.setLocationRelativeTo(null);
}//GEN-LAST:event_formWindowOpened

private void downloadGhSongDataMenuItemActionPerformed(java.awt.event.ActionEvent evt) {                                                      
	new SwingWorker<Boolean, Void>() {		
		@Override
		protected Boolean doInBackground() throws Exception {
			getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			statusBar1.setTempText("Downloading song data from ScoreHero...", true);
			ProgressDialog progress = null;
			
			try {
				if (curGame instanceof GhGame) {
					jshm.dataupdaters.GhSongUpdater.update((GhGame) curGame, curDiff);
					
					progress = new ProgressDialog(GUI.this);
					jshm.dataupdaters.GhSongUpdater.updateSongInfo(progress, (GhGameTitle) curGame.title);
				} else if (curGame instanceof WtGame) {
					progress = new ProgressDialog(GUI.this);
					
					try {
						jshm.dataupdaters.WtSongUpdater.updateViaXml(progress, curGame.title);
						jshm.dataupdaters.WtSongUpdater.updateSongInfo(progress, (WtGameTitle) curGame.title);
					} catch (Exception e1) {
						LOG.log(Level.WARNING, "Failed to download song data via XML", e1);
						
						int result = JOptionPane.showConfirmDialog(GUI.this,
							"Failed to download song data via the quicker way.\nDo you want to download it the old way?", "Download Song Data", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
						
						if (JOptionPane.YES_OPTION == result) {
							jshm.dataupdaters.WtSongUpdater.updateViaScraping(progress, curGame.title);
							jshm.dataupdaters.WtSongUpdater.updateSongInfo(progress, (WtGameTitle) curGame.title);
						} else {
							return false;
						}
					}
				}
				
				return true;
			} catch (Exception e) {
				LOG.log(Level.SEVERE, "Failed to download song data ", e);
				ErrorInfo ei = new ErrorInfo("Error", "Failed to download song data", null, null, e, null, null);
				JXErrorPane.showDialog(GUI.this, ei);
			} finally {
				if (null != progress)
					progress.dispose();
				
				statusBar1.revertText();
				getContentPane().setCursor(Cursor.getDefaultCursor());
			}
			
			return false;
		}
		
		@Override
		public void done() {
			try {
				if (get()) {
					if (curGame instanceof GhGame) {
						songDataMenuItemActionPerformed(null, curGame, curDiff);
					} else if (curGame instanceof WtGame) {
						wtSongDataMenuItemActionPerformed(null, (WtGame) curGame, curGroup);
					} else {
						assert false: "unimplemented game subclass";
					}
				}
			} catch (Exception e) {
				LOG.log(Level.SEVERE, "Unknown error", e);
				ErrorInfo ei = new ErrorInfo("Error", "Unknown error", null, null, e, null, null);
				JXErrorPane.showDialog(GUI.this, ei);
			}
		}	
	}.execute();
}                                                    

private void treeTreeCollapsed(javax.swing.event.TreeExpansionEvent evt) {//GEN-FIRST:event_treeTreeCollapsed
	tree.packAll();
}//GEN-LAST:event_treeTreeCollapsed

private void treeTreeExpanded(javax.swing.event.TreeExpansionEvent evt) {//GEN-FIRST:event_treeTreeExpanded
	tree.packAll();
}//GEN-LAST:event_treeTreeExpanded

private void treeValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_treeValueChanged
//	System.out.println("now selected: " + evt.getPath());
	// evt seems out of date sometimes
//	final Object o = evt.getPath().getLastPathComponent();
	final int row = tree.getSelectedRow();
	final Object o =
		row != -1 
		? tree.getPathForRow(row).getLastPathComponent()
		: null;
	final boolean goodRowCount = tree.getSelectedRowCount() == 1;
	final boolean isScore = o instanceof Score;
	final Score score = isScore ? (Score) o : null;
	
	actions.addNewScore.setEnabled(
		goodRowCount &&
		(isScore || o instanceof ScoresTreeTableModel.SongScores));
	actions.deleteSelectedScore.setEnabled(
		goodRowCount &&
		isScore);
	actions.editSelectedScore.setEnabled(
		goodRowCount &&
		isScore && score.isEditable());
	actions.uploadSelectedScore.setEnabled(
		goodRowCount &&
		isScore &&
		score.isSubmittable());
	
	scoreEditorPanel1.setScore(score);
	
	if (o instanceof ScoresTreeTableModel.SongScores) {
		scoreEditorPanel1.setSong(
			((ScoresTreeTableModel.SongScores) o).song);
	}
}//GEN-LAST:event_treeValueChanged

private void readmeMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_readmeMenuItemActionPerformed
	showTextFileViewer("Readme.txt");
}//GEN-LAST:event_readmeMenuItemActionPerformed

private void changeLogMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changeLogMenuItemActionPerformed
	showTextFileViewer("ChangeLog.txt");
}//GEN-LAST:event_changeLogMenuItemActionPerformed

private void licenseMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_licenseMenuItemActionPerformed
	showTextFileViewer("License.txt");
}//GEN-LAST:event_licenseMenuItemActionPerformed

// TODO convert this and downloading gh song data into one method?
private void downloadRbSongDataMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_downloadRbSongDataMenuItemActionPerformed
	new SwingWorker<Boolean, Void>() {		
		@Override
		protected Boolean doInBackground() throws Exception {
			getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			statusBar1.setTempText("Downloading song data from ScoreHero...", true);
			
			boolean ret = false;
			ProgressDialog progress = null;
			
			try {
				progress = new ProgressDialog(GUI.this);
				
				try {
					jshm.dataupdaters.RbSongUpdater.updateViaXml(progress, curGame.title);
					jshm.dataupdaters.RbSongUpdater.updateSongInfo(progress);
					ret = true;
				} catch (Exception e1) {
					LOG.log(Level.WARNING, "Failed to download song data via XML", e1);
					
					int result = JOptionPane.showConfirmDialog(GUI.this,
						"Failed to download song data via the quicker way.\nDo you want to download it the old, slow way?", "Download Song Data", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
					
					if (JOptionPane.YES_OPTION == result) {
						jshm.dataupdaters.RbSongUpdater.updateViaScraping(progress, curGame.title);
						jshm.dataupdaters.RbSongUpdater.updateSongInfo(progress);
						ret = true;
					}
				}
			} catch (Exception e) {			
				ret = false;
				LOG.log(Level.SEVERE, "Failed to download song data", e);
				ErrorInfo ei = new ErrorInfo("Error", "Failed to download song data", null, null, e, null, null);
				JXErrorPane.showDialog(GUI.this, ei);
			} finally {
				if (null != progress)
					progress.dispose();
				
				statusBar1.revertText();
				getContentPane().setCursor(Cursor.getDefaultCursor());
			}
			
			return ret;
		}
		
		@Override
		public void done() {
			try {
				if (get()) {
					rbSongDataMenuItemActionPerformed(null, (RbGame) curGame, curGroup);
				}
			} catch (Exception e) {
				LOG.log(Level.SEVERE, "Unknown error", e);
				ErrorInfo ei = new ErrorInfo("Error", "Unknown error", null, null, e, null, null);
				JXErrorPane.showDialog(GUI.this, ei);
			}
		}	
	}.execute();
}//GEN-LAST:event_downloadRbSongDataMenuItemActionPerformed

private void searchWikiMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchWikiMenuItemActionPerformed
	String in = JOptionPane.showInputDialog(this, "Enter a search phrase:", "Input", JOptionPane.QUESTION_MESSAGE);
	
	try {
		in = URLs.urlCodec.encode(in);
	} catch (Exception e) {}
	
	if (null != in && !in.isEmpty()) {
		jshm.util.Util.openURL(URLs.wiki.getPageUrl("TextSearch") + "?phrase=" + in);
	}
}//GEN-LAST:event_searchWikiMenuItemActionPerformed

private void goToWikiPageMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_goToWikiPageMenuItemActionPerformed
	String in = JOptionPane.showInputDialog(this, "Enter a wiki page name:", "Input", JOptionPane.QUESTION_MESSAGE);
	
	if (null != in && !in.isEmpty()) {
		jshm.util.Util.openURL(URLs.wiki.getPageUrl(in));
	}
}//GEN-LAST:event_goToWikiPageMenuItemActionPerformed

private void checkForUpdatesMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkForUpdatesMenuItemActionPerformed
	// this.checkUpdatesDialog1
	this.checkUpdatesDialog1.setLocationRelativeTo(this);
	this.checkUpdatesDialog1.setVisible(true);
}//GEN-LAST:event_checkForUpdatesMenuItemActionPerformed

private void downloadRbSongMetaDataMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_downloadRbSongMetaDataMenuItemActionPerformed
	new SwingWorker<Boolean, Void>() {		
		@Override
		protected Boolean doInBackground() throws Exception {
			getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			statusBar1.setTempText("Downloading song meta data...", true);
			
			boolean ret = false;
			ProgressDialog progress = null;
			
			try {
				progress = new ProgressDialog(GUI.this);
				jshm.dataupdaters.RbSongUpdater.updateSongInfo(progress);
				ret = true;
			} catch (Exception e) {			
				ret = false;
				LOG.log(Level.SEVERE, "Failed to download song meta data", e);
				ErrorInfo ei = new ErrorInfo("Error", "Failed to download song meta data", null, null, e, null, null);
				JXErrorPane.showDialog(GUI.this, ei);
			} finally {
				if (null != progress)
					progress.dispose();
				
				statusBar1.revertText();
				getContentPane().setCursor(Cursor.getDefaultCursor());
			}
			
			return ret;
		}
		
		@Override
		public void done() {
			try {
				if (get()) {
					rbSongDataMenuItemActionPerformed(null, (RbGame) curGame, curGroup);
				}
			} catch (Exception e) {
				LOG.log(Level.SEVERE, "Unknown error", e);
				ErrorInfo ei = new ErrorInfo("Error", "Unknown error", null, null, e, null, null);
				JXErrorPane.showDialog(GUI.this, ei);
			}
		}	
	}.execute();
}//GEN-LAST:event_downloadRbSongMetaDataMenuItemActionPerformed

private void downloadGhSongMetaDataMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_downloadGhSongMetaDataMenuItemActionPerformed
	new SwingWorker<Boolean, Void>() {		
		@Override
		protected Boolean doInBackground() throws Exception {
			getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			statusBar1.setTempText("Downloading song meta data...", true);
			
			boolean ret = false;
			ProgressDialog progress = null;
			
			try {
				progress = new ProgressDialog(GUI.this);
				if (curGame.title instanceof GhGameTitle)
					jshm.dataupdaters.GhSongUpdater.updateSongInfo(progress, (GhGameTitle) curGame.title);
				else if (curGame.title instanceof WtGameTitle)
					jshm.dataupdaters.WtSongUpdater.updateSongInfo(progress, (WtGameTitle) curGame.title);
				else
					assert false: "unimplemented GameTitle subclass";
				
				ret = true;
			} catch (Exception e) {			
				ret = false;
				LOG.log(Level.SEVERE, "Failed to download song meta data", e);
				ErrorInfo ei = new ErrorInfo("Error", "Failed to download song meta data", null, null, e, null, null);
				JXErrorPane.showDialog(GUI.this, ei);
			} finally {
				if (null != progress)
					progress.dispose();
				
				statusBar1.revertText();
				getContentPane().setCursor(Cursor.getDefaultCursor());
			}
			
			return ret;
		}
		
		@Override
		public void done() {
			try {
				if (get()) {
					if (curGame.title instanceof GhGameTitle)
						songDataMenuItemActionPerformed(null, curGame, curDiff);
					else if (curGame.title instanceof WtGameTitle)
						wtSongDataMenuItemActionPerformed(null, (WtGame) curGame, curGroup);
					else
						assert false: "unimplemented GameTitle subclass";
				}
			} catch (Exception e) {
				LOG.log(Level.SEVERE, "Unknown error", e);
				ErrorInfo ei = new ErrorInfo("Error", "Unknown error", null, null, e, null, null);
				JXErrorPane.showDialog(GUI.this, ei);
			}
		}	
	}.execute();
}//GEN-LAST:event_downloadGhSongMetaDataMenuItemActionPerformed

private void hideEmptySongsMenuItemItemStateChanged(java.awt.event.ItemEvent evt) {                                                         
	if (tree.getTreeTableModel() instanceof ScoresTreeTableModel) {
		// I'd prefer not to have to do it this way but it seems to work
		ScoresTreeTableModel model =
			(ScoresTreeTableModel) tree.getTreeTableModel();
		model.setDisplayEmptyScores(!getHideEmptyScores());
//		model.removeParent(tree);
//		model = model.createSortedModel(curSorting);
//		model.displayEmptyScores = !getHideEmptyScores();
//		tree.setTreeTableModel(model);
//		model.setParent(GUI.this, tree);
		tree.packAll();
//		sorting = sorting;
	}
}                                                       

private void uploadLogsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {                                                     
	final ProgressDialog prog = new ProgressDialog(this);
	
	final StringBuilder sb = new StringBuilder(
		"Please copy and paste the following links when replying in the JSHManager thread.\n\n"
	);
	
	sb.append("Please also provide a detailed description of what you were doing when the error occured. ");
	sb.append("If you just paste the links, I will be less inclined to look into your issue.\n\n");
	
	new SwingWorker<Boolean, Void>() {
		public Boolean doInBackground() throws Exception {
			final File logDir = new File("data/logs");
			String[] files = logDir.list(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return name.endsWith(".txt");
				}
			});

			int i = 0;
			for (final String s : files) {
				File f = new File(logDir, s);
				prog.setProgress(
					String.format("Uploading %s (%s of %s)", s, i + 1, files.length),
					i, files.length);
				LOG.finer("Uploading " + f.getAbsolutePath() + " to PasteBin");
				
				sb.append(f.getName());
				sb.append(" - ");
				sb.append(PasteBin.post(f));
				sb.append('\n');
				
				i++;
			}
							
			return true;
		}
		
		public void done() {
			try {
				if (get()) {
					prog.setVisible(false);
					textFileViewerDialog1.setVisible("Logs Uploaded", sb.toString());
				}
			} catch (Exception e) {
				LOG.log(Level.SEVERE, "Error uploading logs to PasteBin", e);
				ErrorInfo ei = new ErrorInfo("Error", "Error uploading logs", null, null, e, null, null);
				JXErrorPane.showDialog(GUI.this, ei);
			} finally {
				prog.dispose();
			}
		}
	}.execute();
}                                                  

public void showTextFileViewer(final String file) {
	showTextFileViewer(new File(file));
}

public void showTextFileViewer(final File file) {
	try {
		textFileViewerDialog1.setVisible(file);
	} catch (Exception e) {
		LOG.log(Level.WARNING, "Unknown error displaying TextFileViewer", e);
		ErrorInfo ei = new ErrorInfo("Error", "Unknown error displaying TextFileViewer", null, null, e, null, null);
		JXErrorPane.showDialog(GUI.this, ei);
	}
}

public void showTextFileViewer(String title, String content) {
	textFileViewerDialog1.setVisible(title, content);
}

private void formWindowClosing(java.awt.event.WindowEvent evt) {                                    
	int ret = checkForNewScores();
	
	switch (ret) {
		case JOptionPane.YES_OPTION:
		case JOptionPane.CANCEL_OPTION: return;
		case JOptionPane.NO_OPTION: break;
		default: assert false;
	}
	
	JSHManager.dispose();
}                                  

/**
 * Checks if there are any un-uploaded scores and opens the 
 * score upload wizard if there are.
 * @return The result from {@link JOptionPane#showConfirmDialog()}
 * <ul>
 *   <li>{@link JOptionPane#NO_OPTION} - did not want to upload, continue action
 *   <li>{@link JOptionPane#YES_OPTION} - we did upload, may want to cancel the action
 *   <li>{@link JOptionPane#CANCEL_OPTION} - did not want to upload, cancel action
 * </ul>
 */
private int checkForNewScores() {
	int ret = JOptionPane.NO_OPTION;
	TreeTableModel model = tree.getTreeTableModel();
	
	if (model instanceof ScoresTreeTableModel) {
		int scoreCount = 
			((ScoresTreeTableModel) tree.getTreeTableModel()).getSubmittableScoreCount();
		
		if (scoreCount != 0) {
			ret = JOptionPane.showConfirmDialog(this, 
				"There are new scores that have not been uploaded.\n" +
				"Do you want to upload them?\n\n" +
				"Yes: upload the scores\n" +
				"No: continue without uploading\n" +
				"Cancel: do nothing",
				"Confirm",
				JOptionPane.YES_NO_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE);
			
			switch (ret) {
				case JOptionPane.YES_OPTION:
					// bring up upload wizard
					actions.uploadScores.actionPerformed(null);
					
				case JOptionPane.CANCEL_OPTION:
				case JOptionPane.NO_OPTION:
					break;
					
				default: assert false;
			}
		}
	}
	
	return ret;
}

private void initSongSortingMenu() {
	JMenu m = songSortingMenu;
	m.removeAll();
	
	if (null == curGame) {
		m.setEnabled(false);
	} else {
		assert 0 != curGame.title.getSupportedSortings().length;
		ButtonGroup group = new ButtonGroup();
		
		if (!curGame.title.supportsSorting(curSorting)) {
			// every game should support scorehero's sorting as a fallback
			// sicne that's where we get the data from in the first place
			
			// don't need to call setCurSorting() since this method is only
			// called before setting a new data model
			curSorting = Sorting.SCOREHERO;
		}
		
		for (final Song.Sorting s : curGame.title.getSupportedSortings()) {
			JMenuItem item = new JRadioButtonMenuItem(s.getShortName(), s == curSorting);
			item.setToolTipText(s.getShortDescription());
			group.add(item);
			item.addActionListener(new ActionListener() {
				@Override public void actionPerformed(ActionEvent e) {
					GUI.this.setCurSorting(s);
				}
			});
			m.add(item);
		}
		
		m.setEnabled(true);
	}
}

private void initScoresMenu(final JMenu menu) {
	hh.add(menu.add(actions.gotoSong));
	hh.add(menu.add(scoreEditorPanel1.actions.gotoSong));
	menu.addSeparator();

	hh.add(menu.add(actions.addNewScore));
	hh.add(menu.add(scoreEditorPanel1.actions.new_));
	hh.add(menu.add(actions.toggleEditor));
	hh.add(menu.add(scoreEditorPanel1.actions.save));
	hh.add(menu.add(actions.deleteSelectedScore));
	menu.addSeparator();

	hh.add(menu.add(actions.downloadScores));
	hh.add(menu.add(actions.uploadScores));
	hh.add(menu.add(actions.uploadSelectedScore));
	menu.addSeparator();

	hh.add(menu.add(actions.importScoresFromCsv));
}

private void initToolbar(final JToolBar toolbar) {
    hh.add(toolbar.add(actions.gotoSong));
    toolbar.addSeparator();
    hh.add(toolbar.add(actions.addNewScore));
    hh.add(toolbar.add(scoreEditorPanel1.actions.new_));
    hh.add(toolbar.add(actions.toggleEditor));
    hh.add(toolbar.add(scoreEditorPanel1.actions.save));
    hh.add(toolbar.add(actions.deleteSelectedScore));
    toolbar.addSeparator();
    hh.add(toolbar.add(actions.downloadScores));
    hh.add(toolbar.add(actions.uploadScores));
    hh.add(toolbar.add(actions.uploadSelectedScore));
    toolbar.addSeparator();
    hh.add(toolbar.add(actions.importScoresFromCsv));
}

private void initViewLogMenu(final JMenu menu) {
	try {
		final File logDir = new File("data/logs");
		String[] files = logDir.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".txt");
			}
		});
		
		for (final String s : files) {
			JMenuItem item = new JMenuItem(s);
			item.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					showTextFileViewer(logDir.getPath() + "/" + s);
				}
			});
			
			viewLogMenu.add(item);
		}
	} catch (Exception e) {
		LOG.log(Level.WARNING, "Error initializing View Log menu", e);
	}
}

/**
 * Load the menu with all avaialable GH games.
 * @param menu
 */
private void initGhGameMenu(final JMenu menu) {
	List<jshm.GameTitle> titles =
		GameTitle.getBySeries(GameSeries.GUITAR_HERO);
	
	for (GameTitle ttl : titles) {
		JMenu ttlMenu = new JMenu(ttl.getLongName());
		ttlMenu.setIcon(ttl.getIcon());
		
		List<Game> games = Game.getByTitle(ttl);
		
		for (final Game game : games) {
			// no need for an extra menu if there's only one platform
			JMenu gameMenu = null;
				
			if (games.size() > 1) {
				gameMenu = new JMenu(game.platform.getShortName());
				gameMenu.setIcon(game.platform.getIcon());
			} else {
				gameMenu = ttlMenu;
			}
			
			for (final Difficulty diff : Difficulty.values()) {
				if (diff == Difficulty.CO_OP) break;
							
				JMenuItem diffItem = new JMenuItem(diff.getLongName());
				diffItem.setIcon(diff.getIcon());
				
				ActionListener al = null;
				
				if (menu == ghSongDataMenu) {
					al = new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							songDataMenuItemActionPerformed(e, game, diff);
						}
					};
				} else if (menu == ghScoresMenu) {
					al = new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							myScoresMenuItemActionPerformed(e, game, Instrument.Group.GUITAR, diff);
						}
					};
				} else {
					assert false;
				}
				
				diffItem.addActionListener(al);
				
				gameMenu.add(diffItem);
			}
			
			if (gameMenu != ttlMenu)
				ttlMenu.add(gameMenu);
		}
		
		menu.add(ttlMenu);
	}
	
	
	titles = GameTitle.getBySeries(GameSeries.WORLD_TOUR);
	
	for (GameTitle ttl : titles) {
		WtGameTitle wtTtl = (WtGameTitle) ttl;

		JMenu ttlMenu = new JMenu(ttl.getLongName());
		ttlMenu.setIcon(ttl.getIcon());
		
		List<Game> games = Game.getByTitle(ttl);
		
		for (final Game game : games) {
			// no need for an extra menu if there's only one platform
			JMenu platformMenu = null;
				
			if (games.size() > 1) {
				platformMenu = new JMenu(game.platform.getShortName());
				platformMenu.setIcon(game.platform.getIcon());
			} else {
				platformMenu = ttlMenu;
			}
			
			final int maxGroupSize =
				menu == ghSongDataMenu
				? 4
				: menu == ghScoresMenu
				  ? 1 : 0;
			
			for (int groupSize = 1; groupSize <= maxGroupSize; groupSize++) {
				
				JMenu groupSizeMenu = menu == ghSongDataMenu
				? new JMenu(groupSize + "-part") : platformMenu; // new JMenu(groupSize + "-part");
				
				//for (final Group group : Group.getBySize(groupSize, true)) {
				for (final Group group : game.title.getSupportedInstrumentGroups()) {
					if (menu == ghScoresMenu) {
						JMenu groupMenu = new JMenu(group.getLongName(true));
						groupMenu.setIcon(group.getIcon());

						DiffLoop:
						for (final Difficulty diff : Difficulty.values()) {
							if (diff == Difficulty.CO_OP) continue;
							if (diff == Difficulty.EXPERT_PLUS) {
								if (!wtTtl.supportsExpertPlus) break;
								
								switch (group) {
									case WTDRUMS:
									case DRUMS: break;
									
									default: break DiffLoop;
								};
							}
									
							JMenuItem diffItem = new JMenuItem(diff.getLongName());
							diffItem.setIcon(diff.getIcon());
							
							diffItem.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent e) {
									myScoresMenuItemActionPerformed(e, game, group, diff);
								}
							});
							
							groupMenu.add(diffItem);
						}

						groupSizeMenu.add(groupMenu);
					} else if (menu == ghSongDataMenu) {
						JMenuItem groupMenuItem = new JMenuItem(group.getLongName(true));
						groupMenuItem.setIcon(group.getIcon());
						
						groupMenuItem.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								wtSongDataMenuItemActionPerformed(e, (WtGame) game, group);
							}
						});
						
						groupSizeMenu.add(groupMenuItem);
					} else {
						assert false;
					}
					
				}
				
				if (platformMenu != groupSizeMenu)
					platformMenu.add(groupSizeMenu);
			}
			
			ttlMenu.add(platformMenu);
		}
		
		menu.add(ttlMenu);
	}
}

private void initRbGameMenu(final JMenu menu) {
	List<GameTitle> titles =
		GameTitle.getBySeries(GameSeries.ROCKBAND);
	
	
	if (menu == rbScoresMenu) {
		for (final GameTitle ttl : titles) {
			JMenu ttlMenu = new JMenu(ttl.getLongName());
			ttlMenu.setIcon(ttl.getIcon());
			
			for (final Game g : Game.getByTitle(ttl)) {
				JMenu platformMenu = new JMenu(g.platform.getShortName());
				platformMenu.setIcon(g.platform.getIcon());
				
				for (int groupSize = 1; groupSize <= 1; groupSize++) {
					JMenu groupSizeMenu = platformMenu; // new JMenu(groupSize + "-part");
					
					//for (final Group group : Group.getBySize(groupSize)) {
					for (final Group group : g.title.getSupportedInstrumentGroups()) {
						JMenu groupMenu = new JMenu(group.getLongName());
						groupMenu.setIcon(group.getIcon());
						
						for (final Difficulty d : Difficulty.values()) {
							if (Difficulty.CO_OP == d) break;
							
							JMenuItem diffMenuItem = new JMenuItem(d.getLongName());
							diffMenuItem.setIcon(d.getIcon());
							
							diffMenuItem.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent e) {
									myScoresMenuItemActionPerformed(e, g, group, d);
								}
							});
							
							groupMenu.add(diffMenuItem);
						}
						
						groupSizeMenu.add(groupMenu);
					}
					
					if (platformMenu != groupSizeMenu)
						platformMenu.add(groupSizeMenu);
				}
				
				ttlMenu.add(platformMenu);
			}
			
			menu.add(ttlMenu);
		}
		
		return;
	}
	
	
	if (menu != rbSongDataMenu) return;
	
	
	for (final GameTitle ttl : titles) {
		JMenu ttlMenu = new JMenu(ttl.getLongName());
		ttlMenu.setIcon(ttl.getIcon());
		
		for (final Game g : Game.getByTitle(ttl)) {
			JMenu platformMenu = new JMenu(g.platform.getShortName());
			platformMenu.setIcon(g.platform.getIcon());
			
			for (int groupSize = 1; groupSize <= 4; groupSize++) {
				JMenu groupSizeMenu = new JMenu(groupSize + "-part");
				
				Iterable<Group> groups = 1 == groupSize
					? Arrays.asList(g.title.getSupportedInstrumentGroups())
					: Group.getBySize(groupSize);
					
				for (final Group group : groups) {
					JMenuItem groupMenuItem = new JMenuItem(group.getLongName());
					groupMenuItem.setIcon(group.getIcon());
					
					groupMenuItem.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							rbSongDataMenuItemActionPerformed(e, (RbGame) g, group);
						}
					});
					
					groupSizeMenu.add(groupMenuItem);
					
					
					// will need for the score menu later
//					JMenu groupMenu = new JMenu(group.toString());
//					groupMenu.setIcon(group.getIcon());
//					
//					for (Difficulty d : Difficulty.values()) {
//						if (Difficulty.CO_OP == d) break;
//						
//						JMenuItem diffMenuItem = new JMenuItem(d.toString());
//						diffMenuItem.setIcon(d.getIcon());
//						
//						groupMenu.add(diffMenuItem);
//					}
//					
//					groupSizeMenu.add(groupMenu);
				}
				
				platformMenu.add(groupSizeMenu);
			}
			
			ttlMenu.add(platformMenu);
		}
		
		menu.add(ttlMenu);
	}
}

private void initForumsMenu(final JMenu menu) {
	if (menu == ghLinksMenu) {
		initForumsMenu(menu, jshm.sh.links.Link.GH_ROOT);
	} else if (menu == rbLinksMenu) {
		initForumsMenu(menu, jshm.sh.links.Link.RB_ROOT);
	} else if (menu == wikiMenu) {
		initForumsMenu(menu, jshm.sh.links.Link.WIKI_ROOT);
	} else {
		assert false: "unimplemented forums JMenu";
	}
}

private void initForumsMenu(final JMenu menu, final jshm.sh.links.Link linkRoot) {
	for (final jshm.sh.links.Link f : linkRoot.getChildren()) {
		final Action a = 
		f.getUrl() != null
		? new AbstractAction(f.name) {
			public void actionPerformed(ActionEvent e) {
				jshm.util.Util.openURL(f.getUrl());
			}
		}
		: null;
		
		if (f.hasChildren()) {
			final JMenu subMenu = new JMenu(f.name);	
			subMenu.setIcon(f.getIcon());
			
			if (null != a) {
				// TODO figure out how to get this to work with kb navigation
				
				// has no effect
//				subMenu.addActionListener(a);
//				subMenu.setAction(a);
//				subMenu.getActionMap().put("doClick", a);
				
//				for (Object key : subMenu.getActionMap().allKeys()) {
//					System.out.println(key + "=" + subMenu.getActionMap().get(key));
//				}
//				if (true) return;
				
				// a JMenu with children simply expands the child menu
				// without considering that we might want something else
				// to happen when pressing the menu item
				subMenu.addMouseListener(new MouseAdapter() {
					public void mouseClicked(MouseEvent evt) {
						if (evt.getClickCount() != 1 || evt.getButton() != MouseEvent.BUTTON1) return;
						a.actionPerformed(null);
					}
				});
				
				subMenu.setToolTipText(f.getUrl());
				hh.add(subMenu);
			}
			
			menu.add(subMenu);
			initForumsMenu(subMenu, f);
		} else {			
			JMenuItem item = new JMenuItem(f.name);
			item.setIcon(f.getIcon());
			
			if (null != a) {
				item.addActionListener(a);
				item.setToolTipText(f.getUrl());
				hh.add(item);
			}
			
			menu.add(item);
		}
	}
}

private void songDataMenuItemActionPerformed(final ActionEvent evt, final Game game, final Difficulty difficulty) {
	if (!(game instanceof GhGame))
		throw new IllegalArgumentException("game is not a GhGame");
	
	this.setCurGame(game);
	this.setCurDiff(difficulty);
	curGroup = Instrument.Group.GUITAR;
	
	scoreEditorPanel1.setScore(null);
	editorCollapsiblePane.setCollapsed(true);
	upperNotificationBar.removeMainPanel();
	
	final GhGame ggame = (GhGame) game;
	
	new SwingWorker<Void, Void>() {
		List<jshm.gh.GhSong> songs = null;
		GhSongDataTreeTableModel model = null;
		
		@Override
		protected Void doInBackground() throws Exception {
			getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			statusBar1.setTempText("Loading song data from database...", true);
			
			try {
				songs = jshm.gh.GhSong.getSongs(ggame, difficulty);
				model = new GhSongDataTreeTableModel(ggame, curSorting, songs);
	
				SwingUtilities.invokeAndWait(new Runnable() {
					public void run() {
						if (tree.getTreeTableModel() instanceof Parentable)
							((Parentable) tree.getTreeTableModel()).removeParent(tree);
						tree.setTreeTableModel(model);
						model.setParent(GUI.this, tree);
						tree.packAll();
					}
				});
			} catch (Exception e) {
				LOG.log(Level.SEVERE, "Failed to load song data from database", e);
				ErrorInfo ei = new ErrorInfo("Error", "Failed to load song data from database", null, null, e, null, null);
				JXErrorPane.showDialog(GUI.this, ei);
			} finally {
				getContentPane().setCursor(Cursor.getDefaultCursor());
				statusBar1.revertText();
			}
			
			return null;
		}
		
		@Override
		public void done() {
			if (null == songs) return;
			
			statusBar1.setText("Viewing song data for " + game.title.getWikiAbbr() + ":" + game.platform.getShortName() + " on " + difficulty.getLongName());
			
			actions.downloadScores.setEnabled(true);
			actions.uploadScores.setEnabled(false);
			scoreEditorPanel1.actions.new_.setEnabled(false);
			actions.editSelectedScore.setEnabled(false);
			actions.toggleEditor.setEnabled(false);
			actions.importScoresFromCsv.setEnabled(false);
			actions.gotoSong.setEnabled(false);
			
			downloadGhSongDataMenuItem.setEnabled(true);
			downloadGhSongMetaDataMenuItem.setEnabled(true);
			downloadRbSongDataMenuItem.setEnabled(false);
			downloadRbSongMetaDataMenuItem.setEnabled(false);

			GUI.this.setIconImage(game.title.getIcon());
			GUI.this.setTitle(game.title.getWikiAbbr() + ":" + game.platform.getShortName() + " on " + difficulty.toShortString() + " - Song Data");
			
			if (songs.size() == 0 && null != evt) { // if evt == null we're recursing
				if (JOptionPane.YES_OPTION ==
					JOptionPane.showConfirmDialog(
						GUI.this, "No songs are present.\nDownload from ScoreHero?", "",
						JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)) {
					
					downloadGhSongDataMenuItemActionPerformed(null);
				}

				return;
			}
		}
	}.execute();
}

private void rbSongDataMenuItemActionPerformed(final ActionEvent evt, final RbGame game, final Instrument.Group group) {
	setCurGame(game);
	curGroup = group;
	setCurDiff(null);
	
	scoreEditorPanel1.setScore(null);
	editorCollapsiblePane.setCollapsed(true);
	upperNotificationBar.removeMainPanel();
	
	new SwingWorker<Void, Void>() {
		List<RbSong> songs = null;
		RbSongDataTreeTableModel model = null;
		
		@Override
		protected Void doInBackground() throws Exception {
			getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			statusBar1.setTempText("Loading song data from database...", true);
			
			try {
				songs = RbSong.getSongs(true, game, group);
				model = new RbSongDataTreeTableModel(game, curSorting, songs);
	
				SwingUtilities.invokeAndWait(new Runnable() {
					public void run() {
						if (tree.getTreeTableModel() instanceof Parentable)
							((Parentable) tree.getTreeTableModel()).removeParent(tree);
						tree.setTreeTableModel(model);
						model.setParent(GUI.this, tree);
						tree.packAll();
					}
				});
			} catch (Exception e) {
				LOG.log(Level.SEVERE, "Failed to load song data from database", e);
				ErrorInfo ei = new ErrorInfo("Error", "Failed to load song data from database", null, null, e, null, null);
				JXErrorPane.showDialog(GUI.this, ei);
			} finally {
				getContentPane().setCursor(Cursor.getDefaultCursor());
				statusBar1.revertText();
			}
			
			return null;
		}
		
		@Override
		public void done() {
			if (null == songs) return;
			
			statusBar1.setText("Viewing song data for " + game.title.getWikiAbbr() + ":" + game.platform.getShortName());
			
			actions.downloadScores.setEnabled(false);
			actions.uploadScores.setEnabled(false);
			scoreEditorPanel1.actions.new_.setEnabled(false);
			actions.editSelectedScore.setEnabled(false);
			actions.toggleEditor.setEnabled(false);
			actions.importScoresFromCsv.setEnabled(false);
			actions.gotoSong.setEnabled(false);
			
			downloadGhSongDataMenuItem.setEnabled(false);
			downloadGhSongMetaDataMenuItem.setEnabled(false);
			downloadRbSongDataMenuItem.setEnabled(true);
			downloadRbSongMetaDataMenuItem.setEnabled(true);

			GUI.this.setIconImage(game.title.getIcon());
			GUI.this.setTitle(game.title.getWikiAbbr() + ":" + game.platform.getShortName() + " " + group.getLongName() + " - Song Data");
			
			if (songs.size() == 0 && null != evt) { // if evt == null we're recursing
				if (JOptionPane.YES_OPTION ==
					JOptionPane.showConfirmDialog(
						GUI.this, "No songs are present.\nDownload from ScoreHero?\n(This may take a long time)", "",
						JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)) {
					
					downloadRbSongDataMenuItemActionPerformed(null);
				}

				return;
			}
		}
	}.execute();
}

private void wtSongDataMenuItemActionPerformed(final ActionEvent evt, final WtGame game, final Group group) {
	setCurGame(game);
	curGroup = group;
	setCurDiff(null);
	
	scoreEditorPanel1.setScore(null);
	editorCollapsiblePane.setCollapsed(true);
	upperNotificationBar.removeMainPanel();
	
	new SwingWorker<Void, Void>() {
		List<WtSong> songs = null;
		WtSongDataTreeTableModel model = null;
		
		@Override
		protected Void doInBackground() throws Exception {
			getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			statusBar1.setTempText("Loading song data from database...", true);
			
			try {
				songs = WtSong.getSongs(true, game, group);
				model = new WtSongDataTreeTableModel(game, curSorting, songs);
	
				SwingUtilities.invokeAndWait(new Runnable() {
					public void run() {
						if (tree.getTreeTableModel() instanceof Parentable)
							((Parentable) tree.getTreeTableModel()).removeParent(tree);
						tree.setTreeTableModel(model);
						model.setParent(GUI.this, tree);
						tree.packAll();
					}
				});
			} catch (Exception e) {
				LOG.log(Level.SEVERE, "Failed to load song data from database", e);
				ErrorInfo ei = new ErrorInfo("Error", "Failed to load song data from database", null, null, e, null, null);
				JXErrorPane.showDialog(GUI.this, ei);
			} finally {
				getContentPane().setCursor(Cursor.getDefaultCursor());
				statusBar1.revertText();
			}
			
			return null;
		}
		
		@Override
		public void done() {
			if (null == songs) return;
			
			statusBar1.setText("Viewing song data for " + game.title.getWikiAbbr() + ":" + game.platform.getShortName());
			
			actions.downloadScores.setEnabled(false);
			actions.uploadScores.setEnabled(false);
			scoreEditorPanel1.actions.new_.setEnabled(false);
			actions.editSelectedScore.setEnabled(false);
			actions.toggleEditor.setEnabled(false);
			actions.importScoresFromCsv.setEnabled(false);
			actions.gotoSong.setEnabled(false);
			
			downloadGhSongDataMenuItem.setEnabled(true);
			downloadGhSongMetaDataMenuItem.setEnabled(true);
			downloadRbSongDataMenuItem.setEnabled(false);
			downloadRbSongMetaDataMenuItem.setEnabled(false);

			GUI.this.setIconImage(game.title.getIcon());
			GUI.this.setTitle(game.title.getWikiAbbr() + ":" + game.platform.getShortName() + " " + group.getLongName(game instanceof WtGame) + " - Song Data");
			
			if (songs.size() == 0 && null != evt) { // if evt == null we're recursing
				if (JOptionPane.YES_OPTION ==
					JOptionPane.showConfirmDialog(
						GUI.this, "No songs are present.\nDownload from ScoreHero?\n(This may take a long time)", "",
						JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)) {
					
					downloadGhSongDataMenuItemActionPerformed(null);
				}

				return;
			}
		}
	}.execute();
}

public void myScoresMenuItemActionPerformed(final java.awt.event.ActionEvent evt, final Game game, final Instrument.Group group, final Difficulty difficulty) {
//	final List<TreePath> expandedPaths = 
//		getCurGame() == game && getCurDiff() == difficulty
//		? GuiUtil.getExpandedPaths(jXTreeTable1)
//		: null;
	
	int ret = checkForNewScores();
	
	switch (ret) {
		case JOptionPane.YES_OPTION:
		case JOptionPane.CANCEL_OPTION: return;
		case JOptionPane.NO_OPTION: break;
		default: assert false;
	}
	
	setCurGame(game);
	setCurDiff(difficulty);
	curGroup = group;
	scoreEditorPanel1.setScore(null);
	
	new SwingWorker<Void, Void>() {
		List<? extends Song> songs = null;
		List<Score> scores = null;
		ScoresTreeTableModel model = null;
		
		@Override
		protected Void doInBackground() throws Exception {			
			try {
				getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				statusBar1.setTempText("Loading score data from database...", true);
				
				songs = game.getSongs(group, difficulty);
				scores = game.getScores(group, difficulty);

				model = new ScoresTreeTableModel(game, group, difficulty, curSorting, songs, scores);
	
				orderedSongs =
					game.getSongsOrderedByTitle(group, difficulty);

				SwingUtilities.invokeAndWait(new Runnable() {
					public void run() {
						if (null != model && null != tree) {
							if (tree.getTreeTableModel() instanceof Parentable)
								((Parentable) tree.getTreeTableModel()).removeParent(tree);
							model.displayEmptyScores = !getHideEmptyScores();
							tree.setTreeTableModel(model);
							model.setParent(GUI.this, tree);
							tree.packAll();
							
							// TODO this doesn't work
//							if (null != expandedPaths) {
//								GuiUtil.restoreExpandedPaths(jXTreeTable1, expandedPaths);
//							} else {
//
//							}
							
							tree.repaint();
						}
						
						scoreEditorPanel1.setSongs(orderedSongs);
						selectSongBar.setSongs(orderedSongs);
					}
				});
			} catch (Exception e) {
				LOG.log(Level.SEVERE, "Failed to load score data from database", e);
				ErrorInfo ei = new ErrorInfo("Error", "Failed to load score data from database", null, null, e, null, null);
				org.jdesktop.swingx.JXErrorPane.showDialog(GUI.this, ei);
			} finally {
				getContentPane().setCursor(Cursor.getDefaultCursor());
				statusBar1.revertText();
			}
			
			return null;
		}
		
		@Override
		public void done() {
			if (null == scores) return;
			
			GUI.this.setIconImage(game.title.getIcon());
			GUI.this.setTitle(game.title.getWikiAbbr() + ":" + game.platform.getShortName() + " on " + difficulty.toShortString() + " " + group.getLongName(game instanceof WtGame) + " - Scores");
			
			statusBar1.setText("Viewing scores for " + game.title.getWikiAbbr() + " " + game.platform.getShortName() + " on " + difficulty.toShortString());
			GUI.this.gamePanel.setCombos(game, group, difficulty);
			
			actions.downloadScores.setEnabled(true);
			actions.uploadScores.setEnabled(true);
			scoreEditorPanel1.actions.new_.setEnabled(true);
			actions.toggleEditor.setEnabled(true);
			actions.importScoresFromCsv.setEnabled(true);
			actions.gotoSong.setEnabled(true);
			
			downloadGhSongDataMenuItem.setEnabled(game instanceof GhGame || game instanceof WtGame);
			downloadGhSongMetaDataMenuItem.setEnabled(game instanceof GhGame || game instanceof WtGame);
			downloadRbSongDataMenuItem.setEnabled(game instanceof RbGame);
			downloadRbSongMetaDataMenuItem.setEnabled(game instanceof RbGame);
			
			if (scores.size() == 0 && null != evt) { // if evt == null we're recursing
				if (JOptionPane.YES_OPTION ==
					JOptionPane.showConfirmDialog(
						GUI.this, "No scores are present.\nDownload from ScoreHero?", "",
						JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)) {
					
					actions.downloadScores.actionPerformed(null);
				}

				return;
			}
		}
	}.execute();
}

//    /**
//    * @param args the command line arguments
//    */
//    public static void main(String args[]) {
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//				try {
//					// Set the Look & Feel to match the current system
//					UIManager.setLookAndFeel(
//						UIManager.getSystemLookAndFeelClassName());
//				} catch (Exception e) {
//					System.out.println("Couldn't set system look & feel (not fatal)");
//				}
//				
//                new GUI().setVisible(true);
//            }
//        });
//    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private jshm.gui.AboutDialog aboutDialog1;
    private javax.swing.JMenuItem aboutMenuItem;
    private javax.swing.JMenuItem changeLogMenuItem;
    private javax.swing.JMenuItem checkForUpdatesMenuItem;
    private jshm.gui.CheckUpdatesDialog checkUpdatesDialog1;
    private javax.swing.JMenuItem downloadGhSongDataMenuItem;
    private javax.swing.JMenuItem downloadGhSongMetaDataMenuItem;
    private javax.swing.JMenuItem downloadRbSongDataMenuItem;
    private javax.swing.JMenuItem downloadRbSongMetaDataMenuItem;
    org.jdesktop.swingx.JXCollapsiblePane editorCollapsiblePane;
    private javax.swing.JMenuItem exitMenuItem;
    private javax.swing.JMenu fileMenu;
    private jshm.gui.GameSelectorPanel gamePanel;
    private javax.swing.JMenu ghLinksMenu;
    private javax.swing.JMenu ghMenu;
    private javax.swing.JMenu ghScoresMenu;
    private javax.swing.JMenu ghSongDataMenu;
    javax.swing.JMenuItem goToWikiPageMenuItem;
    private javax.swing.JMenu helpMenu;
    javax.swing.JCheckBoxMenuItem hideEmptySongsMenuItem;
    private javax.swing.JPanel innerPanel1;
    private javax.swing.JPanel innerPanel2;
    private javax.swing.JPanel innerPanel3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator8;
    private javax.swing.JMenuItem licenseMenuItem;
    private jshm.gui.notificationbars.NotificationBar lowerNotificationBar;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenu rbLinksMenu;
    private javax.swing.JMenu rbMenu;
    private javax.swing.JMenu rbScoresMenu;
    private javax.swing.JMenu rbSongDataMenu;
    private javax.swing.JMenuItem readmeMenuItem;
    private jshm.gui.ScoreEditorPanel scoreEditorPanel1;
    private javax.swing.JMenu scoresMenu;
    javax.swing.JMenuItem searchWikiMenuItem;
    private javax.swing.JMenu songSortingMenu;
    private jshm.gui.components.StatusBar statusBar1;
    private jshm.gui.TextFileViewerDialog textFileViewerDialog1;
    private javax.swing.JToolBar toolbar;
    org.jdesktop.swingx.JXTreeTable tree;
    private javax.swing.JScrollPane treeScrollPane;
    private javax.swing.JMenuItem uploadLogsMenuItem;
    private jshm.gui.notificationbars.NotificationBar upperNotificationBar;
    private javax.swing.JMenu viewLogMenu;
    private javax.swing.JMenu wikiMenu;
    // End of variables declaration//GEN-END:variables

    public StatusBar getStatusBar() {
    	return statusBar1;
    }
    
	public Game getCurGame() {
		return curGame;
	}

	private void setCurGame(Game curGame) {
		this.curGame = curGame;
		initSongSortingMenu();
	}

	public Difficulty getCurDiff() {
		return curDiff;
	}

	private void setCurDiff(Difficulty curDiff) {
		this.curDiff = curDiff;
	}

	public Instrument.Group getCurGroup() {
		return curGroup;
	}
	
	void setCurGroup(Instrument.Group group) {
		this.curGroup = group;
	}
	
	public Song.Sorting getCurSorting() {
		return curSorting;
	}
	
	void setCurSorting(Song.Sorting sorting) {
		if (null == curGame || !curGame.title.supportsSorting(sorting))
			throw new IllegalArgumentException("game doesn't support sorting: " + sorting);
		
		this.curSorting = sorting;
		
		if (tree.getTreeTableModel() instanceof SongSortable) {
			((SongSortable) tree.getTreeTableModel()).setSorting(sorting);
			tree.packAll();
		}
	}
	
	public boolean getHideEmptyScores() {
		return hideEmptySongsMenuItem.isSelected();
	}
	
	public JCheckBoxMenuItem getHideEmptySongsMenuItem() {
		return hideEmptySongsMenuItem;
	}
	
	public void openImageOrBrowser(String url) {
		GuiUtil.openImageOrBrowser(this, url);
	}
	
	public void showUpperNotificationBar(Component comp) {
		upperNotificationBar.add(comp, BorderLayout.CENTER);
		upperNotificationBar.validate();
		upperNotificationBar.setCollapsed(false);
	}
	
	
	final Actions actions = new Actions();
	
	class Actions {		
		final MyAction gotoSong = new MyAction(false,
			"Go to Song...", GuiUtil.getIcon("toolbar/find32.png"),
			"Find a song by name",
			KeyStroke.getKeyStroke(KeyEvent.VK_G, CTRL_MASK), 0){

			public void actionPerformed(ActionEvent e) {
				upperNotificationBar.setMainPanel(selectSongBar);
				selectSongBar.focus();
			}
		};
		
		final MyAction addNewScore = new MyAction(false,
			"Add", GuiUtil.getIcon("toolbar/add32.png"),
			"Add a new score for the selected song",
			KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, 0), 'n'){
			
			public void actionPerformed(ActionEvent e) {
				ScoresTreeTableModel model = (ScoresTreeTableModel) tree.getTreeTableModel(); 
				model.createScoreTemplate(
					getCurGame(), getCurGroup(), getCurDiff(),
					tree.getPathForRow(tree.getSelectedRow()));
			}
		};
		
		final MyAction editSelectedScore = new MyAction(false,
			"Edit", GuiUtil.getIcon("toolbar/edit32.png"),
			"Edit the selected score via the score editor",
			null, 'E'){

			public void actionPerformed(ActionEvent e) {
				editorCollapsiblePane.setCollapsed(false);
			}
		};
		
		final MyAction deleteSelectedScore = new MyAction(false,
			"Delete", GuiUtil.getIcon("toolbar/delete32.png"),
			"Delete the currently selected score from the database",
			KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), 'D'){

			public void actionPerformed(ActionEvent e) {
				ScoresTreeTableModel model = (ScoresTreeTableModel) tree.getTreeTableModel();
				TreePath path = tree.getPathForRow(tree.getSelectedRow());
				
				Score score = (Score) path.getLastPathComponent();
				
				switch (score.getStatus()) {
					case NEW:
					case TEMPLATE:
						break;
						
					default:
						if (JOptionPane.YES_OPTION !=
							JOptionPane.showConfirmDialog(GUI.this,
								"Are you sure you want to delete the\n" +
								"selected score from the local database?\n" +
								"(It will not be deleted from ScoreHero)",
								"Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE))
							return;
				}
				
				model.deleteScore(path);
				scoreEditorPanel1.setScore(null);
			}
		};
		
		final MyAction toggleEditor = new MyAction(false,
			"Toggle Editor", GuiUtil.getIcon("toolbar/edit32.png"),
			"Show or hide the score editor",
			KeyStroke.getKeyStroke(KeyEvent.VK_E, CTRL_MASK), 'g'){

			public void actionPerformed(ActionEvent e) {
				editorCollapsiblePane.setCollapsed(
					!editorCollapsiblePane.isCollapsed());
			}
		};
		
		final MyAction downloadScores = new MyAction(false,
			"Download...", GuiUtil.getIcon("toolbar/down32.png"),
			"Download your scores so they can be viewed within JSHManager",
			null, 'l'){

			public void actionPerformed(ActionEvent e) {
				Wizard wiz = ScoreDownloadWizard
					.createWizard(GUI.this, curGame, curGroup, curDiff);
				wiz.show();
			}
		};
		
		final MyAction uploadScores = new MyAction(false,
			"Upload All...", GuiUtil.getIcon("toolbar/upmulti32.png"),
			"Upload your new scores to ScoreHero",
			KeyStroke.getKeyStroke(KeyEvent.VK_U, CTRL_MASK | KeyEvent.ALT_DOWN_MASK), 'U'){

			public void actionPerformed(ActionEvent e) {
				ScoresTreeTableModel model = 
					(ScoresTreeTableModel) tree.getTreeTableModel();
				
				if (!model.hasSubmittableScores()) {
					JOptionPane.showMessageDialog(GUI.this, "There are no new scores to upload", "Error", JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				Wizard wiz = ScoreUploadWizard.createWizard(
					model.createSubmittableScoresModel());
				wiz.show();
				tree.repaint(); // takes care of de-highlighting the new scores
			}
		};
		
		final MyAction uploadSelectedScore = new MyAction(false,
			"Upload", GuiUtil.getIcon("toolbar/up32.png"),
			"Uploaded only the selected score to ScoreHero",
			KeyStroke.getKeyStroke(KeyEvent.VK_U, CTRL_MASK), 'S'){

			public void actionPerformed(ActionEvent e) {
				final Object selected =
//					// not obscure at all....
//					((JXTree) tree.getCellRenderer(0, tree.getHierarchicalColumn()))
//						.getSelectionPath().getLastPathComponent();
					tree.getPathForRow(
						tree.getSelectedRow()).getLastPathComponent();
					
				if (!(selected instanceof Score)) {
					LOG.fine("Expecting selected to be a Score but it was a " + selected.getClass().getName());
					return;
				}
				
				if (!((Score) selected).isSubmittable()) {
					JOptionPane.showMessageDialog(GUI.this,
						"That score cannot be uploaded.",
						"Error", JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				new SwingWorker<Void, Void>() {
					@Override
					protected Void doInBackground() throws Exception {
						getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
						statusBar1.setTempText("Uploading score to ScoreHero...", true);
						
						try {
							if (!jshm.sh.Client.hasAuthCookies()) {
								LoginDialog.showDialog(GUI.this);
							}
							
							Score score = (Score) selected;
							score.submit();
							
							// this will cause the fields to be disabled as needed
							scoreEditorPanel1.setScore(score);
							statusBar1.setTempText(
								"Uploaded " + score.getSong().getTitle() + " (" + score.getScore() + ")" +
								(score.getStatus() == Score.Status.SUBMITTED
								 ? " successfully"
								 : ", success uncertain")
							);
						} catch (Exception e) {
							LOG.log(Level.SEVERE, "Failed to upload score", e);
							ErrorInfo ei = new ErrorInfo("Error", "Failed to upload score", null, null, e, null, null);
							JXErrorPane.showDialog(GUI.this, ei);
							statusBar1.revertText();
						} finally {
							statusBar1.setProgressVisible(false);
							getContentPane().setCursor(Cursor.getDefaultCursor());
						}
						
						return null;
					}
					
					@Override
					public void done() {
						// let the message be seen for a couple of seconds
						new Timer().schedule(new TimerTask() {
							@Override
							public void run() {
								if (!EventQueue.isDispatchThread()) {
									EventQueue.invokeLater(this);
									return;
								}

								statusBar1.revertText();
							}
						}, 3000);
					}
					
				}.execute();
			}
		};
		
		final MyAction importScoresFromCsv = new MyAction(false,
			"Import...", GuiUtil.getIcon("toolbar/addfile32.png"),
			"Import scores from a CSV file",
			null, 'I'){

			public void actionPerformed(ActionEvent e) {
				Wizard wiz = CsvImportWizard
					.createWizard(GUI.this, curGame, curGroup, curDiff);
				wiz.show();
				myScoresMenuItemActionPerformed(null, curGame, curGroup, curDiff);
			}
		};
	}
}

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


import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FilenameFilter;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.tree.TreePath;

import jshm.Config;
import jshm.JSHManager;
import jshm.Score;
import jshm.gh.GhScore;
import jshm.gh.GhSong;
import jshm.gui.components.StatusBar;
import jshm.gui.datamodels.GhMyScoresTreeTableModel;
import jshm.gui.datamodels.GhSongDataTreeTableModel;
import jshm.gui.wizards.scoredownload.ScoreDownloadWizard;
import jshm.gui.wizards.scoreupload.ScoreUploadWizard;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.JXTree;
import org.jdesktop.swingx.error.ErrorInfo;
import org.netbeans.spi.wizard.Wizard;

/**
 *
 * @author Tim Mullin
 */
public class GUI extends javax.swing.JFrame {
	static final Logger LOG = Logger.getLogger(GUI.class.getName());
	
	private jshm.gh.GhGame curGame = null;
	private jshm.Difficulty curDiff = null;
	
	private HoverHelp hh = null;
	
    /** Creates new form GUITest */
    public GUI() {
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
        
        jXTreeTable1.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "expandCurrentRow");
        jXTreeTable1.getActionMap().put("expandCurrentRow", new AbstractAction() {
        	Action parent = jXTreeTable1.getActionMap().get("selectNextColumnCell");
        	
			@Override
			public void actionPerformed(ActionEvent e) {
				int row = jXTreeTable1.getSelectedRow();
				if (row == -1) return;
				TreePath p = jXTreeTable1.getPathForRow(row);
				
				if (jXTreeTable1.getTreeTableModel().isLeaf(p.getLastPathComponent())) {
					parent.actionPerformed(e);
				} else {
					jXTreeTable1.expandRow(row);
				}
			}
        });
        
        jXTreeTable1.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "collapseCurrentRow");
        jXTreeTable1.getActionMap().put("collapseCurrentRow", new AbstractAction() {
        	Action parent = jXTreeTable1.getActionMap().get("selectPreviousColumnCell");
        	
			@Override
			public void actionPerformed(ActionEvent e) {
				int row = jXTreeTable1.getSelectedRow();
				if (row == -1) return;
				TreePath p = jXTreeTable1.getPathForRow(row);
				
				if (jXTreeTable1.getTreeTableModel().isLeaf(p.getLastPathComponent())) {
					parent.actionPerformed(e);
				} else {
					jXTreeTable1.collapseRow(row);
				}
			}
        });
        
        
        
        setTitle("");
        setSize(Config.getInt("window.width"), Config.getInt("window.height"));
        setLocation(Config.getInt("window.x"), Config.getInt("window.y"));
        
        if (Config.getBool("window.maximized")) {
			setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
        }
        
        hh = new HoverHelp(statusBar1);
        hh.add(addNewScoreMenuItem,
        	"Insert a new score for the selected song");
        hh.add(deleteSelectedScoreMenuItem,
        	"Delete the selected score if it hasn't yet been uploaded");
        hh.add(loadMyScoresMenuItem,
        	"Sync the local score list for the current game and difficulty to ScoreHero's");
        hh.add(loadSongDataMenuItem,
        	"Sync the local song list for the current game and difficulty to ScoreHero's (e.g. when there is new DLC)");
        hh.add(jXTreeTable1, new HoverHelp.Callback() {
			@Override
			public String getMessage() {
//				if (!jXTreeTable1.isEditing()) return null;
				if (!(jXTreeTable1.getTreeTableModel() instanceof GhMyScoresTreeTableModel)) return null;
				
				Point p = jXTreeTable1.getMousePosition(true);
				
				if (null == p) return null;
				
				int row = jXTreeTable1.rowAtPoint(p);
				int col = jXTreeTable1.columnAtPoint(p);
				
				if (!(jXTreeTable1.isCellEditable(row, col))) return null;
				
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
    }

    @Override
    public void setTitle(String title) {
    	super.setTitle(
    		(title.isEmpty() ? "" : title + " - ") +
    		JSHManager.Version.NAME + " " + JSHManager.Version.STRING);
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
        statusBar1 = new jshm.gui.components.StatusBar();
        jScrollPane1 = new javax.swing.JScrollPane();
        jXTreeTable1 = new org.jdesktop.swingx.JXTreeTable();
        editorCollapsiblePane = new org.jdesktop.swingx.JXCollapsiblePane();
        scoreEditorPanel1 = new ScoreEditorPanel(this);
        jMenuBar1 = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        exitMenuItem = new javax.swing.JMenuItem();
        myScoresMenu = new javax.swing.JMenu();
        addNewScoreMenuItem = new javax.swing.JMenuItem();
        deleteSelectedScoreMenuItem = new javax.swing.JMenuItem();
        toggleEditorMenuItem = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JSeparator();
        loadMyScoresMenuItem = new javax.swing.JMenuItem();
        uploadScoresMenuItem = new javax.swing.JMenuItem();
        uploadSelectedScoreMenuItem = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        songDataMenu = new javax.swing.JMenu();
        loadSongDataMenuItem = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JSeparator();
        forumsMenu = new javax.swing.JMenu();
        helpMenu = new javax.swing.JMenu();
        readmeMenuItem = new javax.swing.JMenuItem();
        changeLogMenuItem = new javax.swing.JMenuItem();
        licenseMenuItem = new javax.swing.JMenuItem();
        viewLogMenu = new javax.swing.JMenu();
        jSeparator4 = new javax.swing.JSeparator();
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
        getContentPane().add(statusBar1, java.awt.BorderLayout.SOUTH);

        jXTreeTable1.setColumnControlVisible(true);
        jXTreeTable1.setPreferredScrollableViewportSize(new java.awt.Dimension(800, 600));
        jXTreeTable1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jXTreeTable1.addTreeExpansionListener(new javax.swing.event.TreeExpansionListener() {
            public void treeCollapsed(javax.swing.event.TreeExpansionEvent evt) {
                jXTreeTable1TreeCollapsed(evt);
            }
            public void treeExpanded(javax.swing.event.TreeExpansionEvent evt) {
                jXTreeTable1TreeExpanded(evt);
            }
        });
        jXTreeTable1.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                jXTreeTable1ValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(jXTreeTable1);

        getContentPane().add(jScrollPane1, java.awt.BorderLayout.CENTER);

        editorCollapsiblePane.setCollapsed(true);
        editorCollapsiblePane.getContentPane().setLayout(new java.awt.BorderLayout());
        editorCollapsiblePane.getContentPane().add(scoreEditorPanel1, java.awt.BorderLayout.CENTER);

        getContentPane().add(editorCollapsiblePane, java.awt.BorderLayout.NORTH);

        fileMenu.setMnemonic('F');
        fileMenu.setText("File");

        exitMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.ALT_MASK));
        exitMenuItem.setMnemonic('x');
        exitMenuItem.setText("Exit");
        exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(exitMenuItem);

        jMenuBar1.add(fileMenu);

        myScoresMenu.setMnemonic('M');
        myScoresMenu.setText("My Scores");

        addNewScoreMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_INSERT, 0));
        addNewScoreMenuItem.setMnemonic('n');
        addNewScoreMenuItem.setText("Add new score");
        addNewScoreMenuItem.setEnabled(false);
        addNewScoreMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addNewScoreMenuItemActionPerformed(evt);
            }
        });
        myScoresMenu.add(addNewScoreMenuItem);

        deleteSelectedScoreMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DELETE, 0));
        deleteSelectedScoreMenuItem.setMnemonic('D');
        deleteSelectedScoreMenuItem.setText("Delete selected score");
        deleteSelectedScoreMenuItem.setEnabled(false);
        deleteSelectedScoreMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteSelectedScoreMenuItemActionPerformed(evt);
            }
        });
        myScoresMenu.add(deleteSelectedScoreMenuItem);

        toggleEditorMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.CTRL_MASK));
        toggleEditorMenuItem.setMnemonic('E');
        toggleEditorMenuItem.setText("Toggle Editor");
        toggleEditorMenuItem.setEnabled(false);
        toggleEditorMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toggleEditorMenuItemActionPerformed(evt);
            }
        });
        myScoresMenu.add(toggleEditorMenuItem);
        myScoresMenu.add(jSeparator3);

        loadMyScoresMenuItem.setMnemonic('L');
        loadMyScoresMenuItem.setText("Download from ScoreHero...");
        loadMyScoresMenuItem.setEnabled(false);
        loadMyScoresMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadMyScoresMenuItemActionPerformed(evt);
            }
        });
        myScoresMenu.add(loadMyScoresMenuItem);

        uploadScoresMenuItem.setMnemonic('U');
        uploadScoresMenuItem.setText("Upload to ScoreHero...");
        uploadScoresMenuItem.setEnabled(false);
        uploadScoresMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                uploadScoresMenuItemActionPerformed(evt);
            }
        });
        myScoresMenu.add(uploadScoresMenuItem);

        uploadSelectedScoreMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_U, java.awt.event.InputEvent.CTRL_MASK));
        uploadSelectedScoreMenuItem.setMnemonic('S');
        uploadSelectedScoreMenuItem.setText("Upload Selected Score");
        uploadSelectedScoreMenuItem.setEnabled(false);
        uploadSelectedScoreMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                uploadSelectedScoreMenuItemActionPerformed(evt);
            }
        });
        myScoresMenu.add(uploadSelectedScoreMenuItem);
        myScoresMenu.add(jSeparator1);

        initDynamicGameMenu(myScoresMenu);

        jMenuBar1.add(myScoresMenu);

        songDataMenu.setMnemonic('D');
        songDataMenu.setText("Song Data");

        loadSongDataMenuItem.setMnemonic('L');
        loadSongDataMenuItem.setText("Download from ScoreHero...");
        loadSongDataMenuItem.setEnabled(false);
        loadSongDataMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadSongDataMenuItemActionPerformed(evt);
            }
        });
        songDataMenu.add(loadSongDataMenuItem);
        songDataMenu.add(jSeparator2);

        initDynamicGameMenu(songDataMenu);

        jMenuBar1.add(songDataMenu);

        forumsMenu.setMnemonic('r');
        forumsMenu.setText("Forums");

        initForumsMenu(forumsMenu);

        jMenuBar1.add(forumsMenu);

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

        initDynamicGameMenu(viewLogMenu);

        helpMenu.add(viewLogMenu);
        helpMenu.add(jSeparator4);

        aboutMenuItem.setMnemonic('A');
        aboutMenuItem.setText("About");
        aboutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutMenuItemActionPerformed(evt);
            }
        });
        helpMenu.add(aboutMenuItem);

        jMenuBar1.add(helpMenu);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitMenuItemActionPerformed
//	System.exit(0);
	JSHManager.dispose();
}//GEN-LAST:event_exitMenuItemActionPerformed

private void aboutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutMenuItemActionPerformed
	// this.aboutDialog1
	this.aboutDialog1.setLocationRelativeTo(null);
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

private void loadMyScoresMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadMyScoresMenuItemActionPerformed
	Wizard wiz = ScoreDownloadWizard
		.createWizard(GUI.this, getCurGame(), getCurDiff());
	wiz.show();
}//GEN-LAST:event_loadMyScoresMenuItemActionPerformed

private void loadSongDataMenuItemActionPerformed(java.awt.event.ActionEvent evt) {                                                      
	new SwingWorker<Boolean, Void>() {
		@Override
		protected Boolean doInBackground() throws Exception {
			getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			statusBar1.setTempText("Downloading song data from ScoreHero...", true);
			
			try {
				jshm.dataupdaters.GhSongUpdater.update(getCurGame(), getCurDiff());
				return true;
			} catch (Exception e) {
				LOG.log(Level.SEVERE, "Failed to download song data ", e);
				ErrorInfo ei = new ErrorInfo("Error", "Failed to download song data", null, null, e, null, null);
				JXErrorPane.showDialog(GUI.this, ei);
			} finally {
				statusBar1.revertText();
				getContentPane().setCursor(Cursor.getDefaultCursor());
			}
			
			return false;
		}
		
		@Override
		public void done() {
			try {
				if (get()) {
					songDataMenuItemActionPerformed(null,getCurGame(), getCurDiff());
				}
			} catch (Exception e) {
				LOG.log(Level.SEVERE, "Unknown error", e);
				ErrorInfo ei = new ErrorInfo("Error", "Unknown error", null, null, e, null, null);
				JXErrorPane.showDialog(GUI.this, ei);
			}
		}	
	}.execute();
}                                                    

private void jXTreeTable1TreeCollapsed(javax.swing.event.TreeExpansionEvent evt) {//GEN-FIRST:event_jXTreeTable1TreeCollapsed
	jXTreeTable1.packAll();
}//GEN-LAST:event_jXTreeTable1TreeCollapsed

private void jXTreeTable1TreeExpanded(javax.swing.event.TreeExpansionEvent evt) {//GEN-FIRST:event_jXTreeTable1TreeExpanded
	jXTreeTable1.packAll();
}//GEN-LAST:event_jXTreeTable1TreeExpanded

private void jXTreeTable1ValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_jXTreeTable1ValueChanged
//	System.out.println("now selected: " + evt.getPath());
	final Object o = evt.getPath().getLastPathComponent();
	final boolean goodRowCount = jXTreeTable1.getSelectedRowCount() == 1;
	final boolean isGhScore = o instanceof GhScore;
	final GhScore score = isGhScore ? (GhScore) o : null;
	
	addNewScoreMenuItem.setEnabled(
		goodRowCount &&
		(isGhScore || o instanceof GhMyScoresTreeTableModel.SongScores));
	deleteSelectedScoreMenuItem.setEnabled(
		goodRowCount &&
		isGhScore &&
		(score.getStatus() == Score.Status.NEW ||
		 score.getStatus() == Score.Status.TEMPLATE));
	uploadSelectedScoreMenuItem.setEnabled(
		goodRowCount &&
		isGhScore &&
		score.getStatus() == Score.Status.NEW);
	
	scoreEditorPanel1.setScore(score);
	
	if (o instanceof GhMyScoresTreeTableModel.SongScores) {
		scoreEditorPanel1.setSong(
			((GhMyScoresTreeTableModel.SongScores) o).song);
	}
}//GEN-LAST:event_jXTreeTable1ValueChanged

private void addNewScoreMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addNewScoreMenuItemActionPerformed
	GhMyScoresTreeTableModel model = (GhMyScoresTreeTableModel) jXTreeTable1.getTreeTableModel(); 
	model.createScoreTemplate(
		jXTreeTable1.getPathForRow(jXTreeTable1.getSelectedRow()));
//	jXTreeTable1.repaint();
}//GEN-LAST:event_addNewScoreMenuItemActionPerformed

private void deleteSelectedScoreMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteSelectedScoreMenuItemActionPerformed
	GhMyScoresTreeTableModel model = (GhMyScoresTreeTableModel) jXTreeTable1.getTreeTableModel();
	model.deleteScore(
		jXTreeTable1.getPathForRow(jXTreeTable1.getSelectedRow()));
	scoreEditorPanel1.setScore(null);
}//GEN-LAST:event_deleteSelectedScoreMenuItemActionPerformed

private void uploadScoresMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_uploadScoresMenuItemActionPerformed
	GhMyScoresTreeTableModel newModel = 
		((GhMyScoresTreeTableModel) jXTreeTable1.getTreeTableModel()).createNewScoresModel();
	
	if (newModel.getScoreCount() == 0) {
		JOptionPane.showMessageDialog(this, "There are no new scores to upload", "Error", JOptionPane.WARNING_MESSAGE);
		return;
	}
	
	Wizard wiz = ScoreUploadWizard.createWizard(newModel);
	wiz.show();
}//GEN-LAST:event_uploadScoresMenuItemActionPerformed

private void readmeMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_readmeMenuItemActionPerformed
	showTextFileViewer("Readme.txt");
}//GEN-LAST:event_readmeMenuItemActionPerformed

private void changeLogMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changeLogMenuItemActionPerformed
	showTextFileViewer("ChangeLog.txt");
}//GEN-LAST:event_changeLogMenuItemActionPerformed

private void licenseMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_licenseMenuItemActionPerformed
	showTextFileViewer("License.txt");
}//GEN-LAST:event_licenseMenuItemActionPerformed

private void uploadSelectedScoreMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_uploadSelectedScoreMenuItemActionPerformed
	final Object selected =
		// not obscure at all....
		((JXTree) jXTreeTable1.getCellRenderer(0, jXTreeTable1.getHierarchicalColumn()))
			.getSelectionPath().getLastPathComponent();
	
	if (!(selected instanceof GhScore)) {
		LOG.fine("Expecting selected to be a GhScore but it was a " + selected.getClass().getName());
		return;
	}
	
	if (!((GhScore) selected).isSubmittable()) {
		JOptionPane.showMessageDialog(this,
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
				
				GhScore score = (GhScore) selected;
				jshm.sh.GhApi.submitGhScore(score);
				statusBar1.setTempText("Uploaded " + score.getSong().getTitle() + " (" + score.getScore() + ") successfully");
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
}//GEN-LAST:event_uploadSelectedScoreMenuItemActionPerformed

private void toggleEditorMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toggleEditorMenuItemActionPerformed
	editorCollapsiblePane.setCollapsed(
		!editorCollapsiblePane.isCollapsed());
}//GEN-LAST:event_toggleEditorMenuItemActionPerformed

public void showTextFileViewer(final String file) {
	try {
		textFileViewerDialog1.setVisible(new File(file));
	} catch (Exception e) {
		LOG.log(Level.WARNING, "Unknown error displaying TextFileViewer", e);
		ErrorInfo ei = new ErrorInfo("Error", "Unknown error displaying TextFileViewer", null, null, e, null, null);
		JXErrorPane.showDialog(GUI.this, ei);
	}
}

private void formWindowClosing(java.awt.event.WindowEvent evt) {                                    
	jshm.JSHManager.dispose();
}                                  

/**
 * Load the menu with all avaialable GH games.
 * @param menu
 */
private void initDynamicGameMenu(final javax.swing.JMenu menu) {
	if (menu == viewLogMenu) {
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
		
		return;
	}
	
	java.util.List<jshm.GameTitle> titles =
		jshm.GameTitle.getTitlesBySeries(jshm.GameSeries.GUITAR_HERO);
	
	for (jshm.GameTitle ttl : titles) {		
//		System.out.println("Creating " + ttl);
		
		javax.swing.JMenu ttlMenu = new javax.swing.JMenu(ttl.toString());
		ttlMenu.setIcon(ttl.getIcon());
		
		java.util.List<jshm.Game> games =
			jshm.Game.getByTitle(ttl);
		
		for (final jshm.Game game : games) {
			// no need for an extra menu if there's only one platform
			javax.swing.JMenu gameMenu = null;
				
			if (games.size() > 1) {
				gameMenu = new javax.swing.JMenu(game.platform.toString());
				gameMenu.setIcon(game.platform.getIcon());
			} else {
				gameMenu = ttlMenu;
			}
			
//			System.out.println("  Creating " + game.platform);
			for (final jshm.Difficulty diff : jshm.Difficulty.values()) {
				if (diff == jshm.Difficulty.CO_OP) continue;
				
//				System.out.println("    Creating " + diff);
				
				JMenuItem diffItem = new JMenuItem(diff.toString());
				diffItem.setIcon(diff.getIcon());
				
				ActionListener al = null;
				
				if (menu == songDataMenu) {
					al = new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							songDataMenuItemActionPerformed(e, (jshm.gh.GhGame) game, diff);
						}
					};
				} else if (menu == myScoresMenu) {
					al = new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							myScoresMenuItemActionPerformed(e, (jshm.gh.GhGame) game, diff);
						}
					};
				} else {
					assert false;
				}
				
				diffItem.addActionListener(al);
				
				gameMenu.add(diffItem);
			}
			
			ttlMenu.add(gameMenu);
		}
		
		menu.add(ttlMenu);
	}
}

private void initForumsMenu(JMenu menu) {
	initForumsMenu(menu, jshm.sh.ShForum.GH_ROOT.getChildren());
}

private void initForumsMenu(JMenu menu, List<jshm.sh.ShForum> forums) {
	for (final jshm.sh.ShForum f : forums) {
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
			}
			
			menu.add(subMenu);
			initForumsMenu(subMenu, f.getChildren());
		} else {			
			JMenuItem item = new JMenuItem(f.name);
			if (null != a)
				item.addActionListener(a);
			menu.add(item);
		}
	}
}

private void songDataMenuItemActionPerformed(final java.awt.event.ActionEvent evt, final jshm.gh.GhGame game, final jshm.Difficulty difficulty) {
	this.setCurGame(game);
	this.setCurDiff(difficulty);
	scoreEditorPanel1.setScore(null);
	editorCollapsiblePane.setCollapsed(true);
	
	new SwingWorker<Void, Void>() {
		List<jshm.gh.GhSong> songs = null;
		GhSongDataTreeTableModel model = null;
		
		@Override
		protected Void doInBackground() throws Exception {
			getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			statusBar1.setTempText("Loading song data from database...", true);
			
			try {
				songs = jshm.gh.GhSong.getSongs(game, difficulty);
				model = new GhSongDataTreeTableModel(game, songs);
	
				SwingUtilities.invokeAndWait(new Runnable() {
					public void run() {
						jXTreeTable1.setTreeTableModel(model);
						model.setParent(jXTreeTable1);
						jXTreeTable1.repaint();
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
			
			statusBar1.setText("Viewing song data for " + game + " on " + difficulty);
			loadMyScoresMenuItem.setEnabled(true);
			loadSongDataMenuItem.setEnabled(true);
			uploadScoresMenuItem.setEnabled(false);
			editorCollapsiblePane.setCollapsed(true);
			toggleEditorMenuItem.setEnabled(false);
			
			GUI.this.setIconImage(game.title.getIcon().getImage());
			GUI.this.setTitle(game + " on " + difficulty + " - Song Data");
			
			if (songs.size() == 0 && null != evt) { // if evt == null we're recursing
				if (JOptionPane.YES_OPTION ==
					JOptionPane.showConfirmDialog(
						GUI.this, "No songs are present.\nDownload from ScoreHero?", "",
						JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)) {
					
					loadSongDataMenuItemActionPerformed(null);
				}

				return;
			}
		}
	}.execute();
}

public void myScoresMenuItemActionPerformed(final java.awt.event.ActionEvent evt, final jshm.gh.GhGame game, final jshm.Difficulty difficulty) {
	this.setCurGame(game);
	this.setCurDiff(difficulty);
	scoreEditorPanel1.setScore(null);
	
	new SwingWorker<Void, Void>() {
		List<GhSong> songs = null;
		List<GhScore> scores = null;
		GhMyScoresTreeTableModel model = null;
		
		@Override
		protected Void doInBackground() throws Exception {			
			try {
				songs = GhSong.getSongs(game, difficulty);
				
				getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				statusBar1.setTempText("Loading score data from database...", true);
				
				scores = GhScore.getScores(game, difficulty);
				model = new GhMyScoresTreeTableModel(game, songs, scores);
	
				final List<GhSong> orderedSongs = GhSong.getSongsOrderedByTitles(game, difficulty);
				
				SwingUtilities.invokeAndWait(new Runnable() {
					public void run() {
						if (null != model && null != jXTreeTable1) {
							jXTreeTable1.setTreeTableModel(model);
							model.setParent(jXTreeTable1);
							jXTreeTable1.repaint();
						}
						
						scoreEditorPanel1.setSongs(orderedSongs);
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
			
			GUI.this.setIconImage(game.title.getIcon().getImage());
			GUI.this.setTitle(game + " on " + difficulty + " - My Scores");
			
			statusBar1.setText("Viewing scores for " + game + " on " + difficulty);
			loadMyScoresMenuItem.setEnabled(true);
			loadSongDataMenuItem.setEnabled(true);
			uploadScoresMenuItem.setEnabled(true);
			toggleEditorMenuItem.setEnabled(true);
			
			if (scores.size() == 0 && null != evt) { // if evt == null we're recursing
				if (JOptionPane.YES_OPTION ==
					JOptionPane.showConfirmDialog(
						GUI.this, "No scores are present.\nDownload from ScoreHero?", "",
						JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)) {
					
					loadMyScoresMenuItemActionPerformed(null);
				}

				return;
			}
		}
	}.execute();
}

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
				try {
					// Set the Look & Feel to match the current system
					UIManager.setLookAndFeel(
						UIManager.getSystemLookAndFeelClassName());
				} catch (Exception e) {
					System.out.println("Couldn't set system look & feel (not fatal)");
				}
				
                new GUI().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private jshm.gui.AboutDialog aboutDialog1;
    private javax.swing.JMenuItem aboutMenuItem;
    private javax.swing.JMenuItem addNewScoreMenuItem;
    private javax.swing.JMenuItem changeLogMenuItem;
    private javax.swing.JMenuItem deleteSelectedScoreMenuItem;
    private org.jdesktop.swingx.JXCollapsiblePane editorCollapsiblePane;
    private javax.swing.JMenuItem exitMenuItem;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenu forumsMenu;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private org.jdesktop.swingx.JXTreeTable jXTreeTable1;
    private javax.swing.JMenuItem licenseMenuItem;
    private javax.swing.JMenuItem loadMyScoresMenuItem;
    private javax.swing.JMenuItem loadSongDataMenuItem;
    private javax.swing.JMenu myScoresMenu;
    private javax.swing.JMenuItem readmeMenuItem;
    private jshm.gui.ScoreEditorPanel scoreEditorPanel1;
    private javax.swing.JMenu songDataMenu;
    private jshm.gui.components.StatusBar statusBar1;
    private jshm.gui.TextFileViewerDialog textFileViewerDialog1;
    private javax.swing.JMenuItem toggleEditorMenuItem;
    private javax.swing.JMenuItem uploadScoresMenuItem;
    private javax.swing.JMenuItem uploadSelectedScoreMenuItem;
    private javax.swing.JMenu viewLogMenu;
    // End of variables declaration//GEN-END:variables

    public StatusBar getStatusBar() {
    	return statusBar1;
    }
    
	public jshm.gh.GhGame getCurGame() {
		return curGame;
	}

	private void setCurGame(jshm.gh.GhGame curGame) {
		this.curGame = curGame;
	}

	public jshm.Difficulty getCurDiff() {
		return curDiff;
	}

	private void setCurDiff(jshm.Difficulty curDiff) {
		this.curDiff = curDiff;
	}

}

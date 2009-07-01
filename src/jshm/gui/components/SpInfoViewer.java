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
package jshm.gui.components;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

import jshm.gui.EditPopupMenu;
import jshm.gui.GuiUtil;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;
import org.jdesktop.swingx.graphics.GraphicsUtilities;

/**
 *
 * @author  Tim
 */
// TODO rename since this will act as the SpInfo viewer/editor as well a standalone image viewer
public class SpInfoViewer extends javax.swing.JFrame {
	// actions
	final Action
	ZOOM_IN_ACTION = new AbstractAction("Zoom In", new ImageIcon(SpInfoViewer.class.getResource("/jshm/resources/images/toolbar/zoomin32.png"))) {
		public void actionPerformed(ActionEvent e) {
			nip.setZoom(nip.getZoom() * (1 + nip.getZoomIncrement()));
			
//			float newScale = Math.min(getScale() + 0.25f, MAX_SCALE);
//			setScale(newScale);
		}
    },
    ZOOM_OUT_ACTION = new AbstractAction("Zoom Out", new ImageIcon(SpInfoViewer.class.getResource("/jshm/resources/images/toolbar/zoomout32.png"))) {
		public void actionPerformed(ActionEvent e) {
			nip.setZoom(nip.getZoom() * (1 - nip.getZoomIncrement()));
			
//			float newScale = Math.max(getScale() - 0.25f, MIN_SCALE);
//			setScale(newScale);
		}
	},
	
	CLOSE_ACTION = new AbstractAction("Close", new ImageIcon(SpInfoViewer.class.getResource("/jshm/resources/images/toolbar/close32.png"))) {
		public void actionPerformed(ActionEvent e) {
			dispose();
		}
	},
	
	NEW_ACTION = new AbstractAction("New", new ImageIcon(SpInfoViewer.class.getResource("/jshm/resources/images/toolbar/file32.png"))) {
		public void actionPerformed(ActionEvent e) {
			
		}
	},
	SAVE_ACTION = new AbstractAction("Save", new ImageIcon(SpInfoViewer.class.getResource("/jshm/resources/images/toolbar/save32.png"))) {
		public void actionPerformed(ActionEvent e) {
			
		}
	},
	SAVE_IMAGE_AS_ACTION = new AbstractAction("Save image as...", new ImageIcon(SpInfoViewer.class.getResource("/jshm/resources/images/toolbar/saveas32.png"))) {
		public void actionPerformed(ActionEvent e) {
			if (null == nip.getImage()) return;
			
			String name = getTitle();
			int i = name.lastIndexOf('/');
			int j = name.lastIndexOf('.');
			
			if (i >= 0) {
				name = name.substring(i, j >= 0 ? j : name.length());
				name = name.trim();
			}
			
			name += ".png";
			jfc.setSelectedFile(new File(name));
			jfc.showSaveDialog(SpInfoViewer.this);
			
			final File selected = jfc.getSelectedFile();
			
			if (!selected.getName().toLowerCase().endsWith(".png")) {
				JOptionPane.showMessageDialog(SpInfoViewer.this, "The filename must end with .png", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			if (selected.exists()) {
				if (JOptionPane.YES_OPTION != 
					JOptionPane.showConfirmDialog(SpInfoViewer.this, 
						"Overwrite existing file?",
						"File already exists",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE))
					return;
			}
			
			try {
				ImageIO.write(nip.getImage(), "png", selected);
			} catch (IOException x) {
				ErrorInfo ei = new ErrorInfo("Error",
					"Failed to save image", null, null, x, null, null);
				JXErrorPane.showDialog(null, ei);
			}
		}
	},
	DELETE_ACTION = new AbstractAction("Delete", new ImageIcon(SpInfoViewer.class.getResource("/jshm/resources/images/toolbar/delete32.png"))) {
		public void actionPerformed(ActionEvent e) {
			
		}
	}
	;
	
	
    /** Creates new form ImageViewer */
    public SpInfoViewer() {
    	for (Action a : new Action[] {
    		ZOOM_IN_ACTION, ZOOM_OUT_ACTION,
    		CLOSE_ACTION,
    		NEW_ACTION, SAVE_ACTION, SAVE_IMAGE_AS_ACTION, DELETE_ACTION})
    		a.putValue(Action.SHORT_DESCRIPTION, a.getValue(Action.NAME));

        initComponents();
        
        jfc.setCurrentDirectory(new File("."));
		jfc.addChoosableFileFilter(new FileFilter() {
			public String getDescription() {
				return "PNG";
			}

			public boolean accept(File f) {
				String name = f.getName().toLowerCase();
				return f.isDirectory() ||
					name.endsWith(".png");
			}
		});
        
        EditPopupMenu.add(titleField);
        EditPopupMenu.add(referenceUrlField);
        EditPopupMenu.add(descriptionTextArea);
        
        nip.addPropertyChangeListener(NavigableImagePanel.IMAGE_CHANGED_PROPERTY, new PropertyChangeListener() {
        	public void propertyChange(PropertyChangeEvent evt) {
				SAVE_IMAGE_AS_ACTION.setEnabled(evt.getNewValue() != null);
			}
        });
                
        ActionMap aMap = getRootPane().getActionMap();
        aMap.put("imageZoomIn", ZOOM_IN_ACTION);
        aMap.put("imageZoomOut", ZOOM_OUT_ACTION);
        InputMap inMap = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, KeyEvent.CTRL_DOWN_MASK), "imageZoomIn");
        inMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, KeyEvent.CTRL_DOWN_MASK), "imageZoomOut");
        
        
        setImage((Image) null, true);
    }

	public void setText(String text) {
//		if (null == text || text.isEmpty()) {
//			Container cp = getContentPane();
//			cp.remove(jSplitPane1);
//			cp.add(imageScrollPane, BorderLayout.CENTER);
//			cp.validate();
//			textPane.setText("");
//		} else {
//			if (textPane.getText().isEmpty()) {
//				jSplitPane1.setBottomComponent(imageScrollPane);
//				Container cp = getContentPane();
//				cp.remove(imageScrollPane);
//				cp.add(jSplitPane1, BorderLayout.CENTER);
//				cp.validate();	
//			}
			
			descriptionTextArea.setText(text);
//		}
	}
	
	public void setImage(String url) {
		setImage(url, false);
	}
	
	public void setImage(String url, boolean hideEditor) {
		try {
			setImage(new URL(url), hideEditor);
		} catch (MalformedURLException e) {
			setImage((Image) null, hideEditor);
			e.printStackTrace();
		}
	}
	
	public void setImage(final URL url) {
		setImage(url, false);
	}
	
	public void setImage(final URL url, final boolean hideEditor) {
		if (null == url) {
			setImage((Image) null, hideEditor);
			return;
		}
		
		new Thread(new Runnable() {
			public void run() {
				try {
					final BufferedImage im = ImageIO.read(url);
					
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							setImage(im, hideEditor);
						}
					});
				} catch (Exception e) {
					setImage((Image) null, hideEditor);
				}
			}
		}).start();
	}
	
	public void setImage(ImageIcon icon) {
		setImage(icon.getImage());
	}
	
	public void setImage(Image image) {
		setImage(image, false);
	}
	
	public void setImage(Image image, boolean hideEditor) {
		editorCollapsiblePane.setVisible(!hideEditor);
		jSplitPane1.setDividerSize(hideEditor ? 0 : 5);
		newButton.setVisible(!hideEditor);
		saveButton.setVisible(!hideEditor);
		deleteButton.setVisible(!hideEditor);
//		editActionsToolbarSeparator.setVisible(!hideEditor);
		
		BufferedImage newImage = null;

		if (null == image) {
		} else if (image instanceof BufferedImage) {
			newImage = (BufferedImage) image;
		} else {
			newImage = GraphicsUtilities.createCompatibleImage(
				image.getWidth(null), image.getHeight(null)); 
			newImage.getGraphics().drawImage(image, 
				0, 0, newImage.getWidth(), newImage.getHeight(), 
				0, 0, newImage.getWidth(), newImage.getHeight(), null);
		}
		
		if (null != newImage) {
			newImage = GraphicsUtilities.toCompatibleImage(newImage);
			newImage.setAccelerationPriority(1f);
		}

		nip.setImage(newImage);
	}
	
	@Override public void dispose() {
		super.dispose();
		setImage((Image) null);
		System.gc(); // meh
	}
	
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
//    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jfc = new javax.swing.JFileChooser();
        jSplitPane1 = new javax.swing.JSplitPane();
        editorCollapsiblePane = new org.jdesktop.swingx.JXCollapsiblePane();
        editorPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        titleLabel = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        fromFileButton = new javax.swing.JButton();
        fromUrlButton = new javax.swing.JButton();
        referenceUrlField = new javax.swing.JTextField();
        titleField = new javax.swing.JTextField();
        openReferenceUrlButton = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        jPanel2 = new javax.swing.JPanel();
        titleLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        songTitleField = new javax.swing.JTextField();
        songDifficultyField = new javax.swing.JTextField();
        songInstrumentField = new javax.swing.JTextField();
        jScrollPane3 = new javax.swing.JScrollPane();
        descriptionTextArea = new javax.swing.JTextArea();
        mainPanel = new javax.swing.JPanel();
        controlsPanel = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        newButton = new javax.swing.JButton();
        saveButton = new javax.swing.JButton();
        saveImageAsButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        editActionsToolbarSeparator = new javax.swing.JToolBar.Separator();
        zoomOutButton = new javax.swing.JButton();
        zoomInButton = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        closeButton = new javax.swing.JButton();
        nip = new jshm.gui.components.NavigableImagePanel();

        jfc.setAcceptAllFileFilterUsed(false);
        jfc.setDialogType(javax.swing.JFileChooser.SAVE_DIALOG);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("SP Path Viewer");

        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        editorCollapsiblePane.getContentPane().setLayout(new java.awt.BorderLayout());

        jPanel1.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createEmptyBorder(5, 0, 0, 5), javax.swing.BorderFactory.createTitledBorder("Path Details")));

        titleLabel.setText("Title");

        jLabel1.setText("Reference URL");

        jLabel2.setText("Set Image");

        fromFileButton.setText("From File");
        fromFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fromFileButtonActionPerformed(evt);
            }
        });

        fromUrlButton.setText("From URL");
        fromUrlButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fromUrlButtonActionPerformed(evt);
            }
        });

        openReferenceUrlButton.setText("Open");
        openReferenceUrlButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openReferenceUrlButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(titleLabel)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addGap(9, 9, 9)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(fromFileButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(fromUrlButton))
                            .addComponent(referenceUrlField, javax.swing.GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(openReferenceUrlButton))
                    .addComponent(titleField, javax.swing.GroupLayout.DEFAULT_SIZE, 320, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(titleLabel)
                    .addComponent(titleField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(openReferenceUrlButton)
                    .addComponent(referenceUrlField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(fromFileButton)
                    .addComponent(fromUrlButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jScrollPane2.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createEmptyBorder(5, 0, 0, 0), javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createTitledBorder("Saved Paths"), javax.swing.BorderFactory.createEmptyBorder(0, 5, 5, 5))));

        jList1.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Path 1", "Path 2", "..." };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane2.setViewportView(jList1);

        jPanel2.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 0, 0), javax.swing.BorderFactory.createTitledBorder("Song")));

        titleLabel1.setText("Title");

        jLabel3.setText("Difficulty");

        jLabel4.setText("Instrument");

        songTitleField.setEditable(false);

        songDifficultyField.setEditable(false);

        songInstrumentField.setEditable(false);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jLabel3)
                    .addComponent(titleLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(songDifficultyField, javax.swing.GroupLayout.DEFAULT_SIZE, 186, Short.MAX_VALUE)
                    .addComponent(songInstrumentField, javax.swing.GroupLayout.DEFAULT_SIZE, 186, Short.MAX_VALUE)
                    .addComponent(songTitleField, javax.swing.GroupLayout.DEFAULT_SIZE, 186, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(titleLabel1)
                    .addComponent(songTitleField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(songDifficultyField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(songInstrumentField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(22, Short.MAX_VALUE))
        );

        jScrollPane3.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createEmptyBorder(0, 5, 5, 5), javax.swing.BorderFactory.createTitledBorder("Description")));

        descriptionTextArea.setColumns(20);
        descriptionTextArea.setRows(5);
        jScrollPane3.setViewportView(descriptionTextArea);

        javax.swing.GroupLayout editorPanelLayout = new javax.swing.GroupLayout(editorPanel);
        editorPanel.setLayout(editorPanelLayout);
        editorPanelLayout.setHorizontalGroup(
            editorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(editorPanelLayout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 994, Short.MAX_VALUE)
        );
        editorPanelLayout.setVerticalGroup(
            editorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(editorPanelLayout.createSequentialGroup()
                .addGroup(editorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, 0, 0, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 75, Short.MAX_VALUE))
        );

        editorCollapsiblePane.getContentPane().add(editorPanel, java.awt.BorderLayout.CENTER);

        jSplitPane1.setTopComponent(editorCollapsiblePane);

        mainPanel.setLayout(new java.awt.BorderLayout());

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        newButton.setAction(NEW_ACTION);
        newButton.setFocusable(false);
        newButton.setHideActionText(true);
        newButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        newButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(newButton);

        saveButton.setAction(SAVE_ACTION);
        saveButton.setFocusable(false);
        saveButton.setHideActionText(true);
        saveButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        saveButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(saveButton);

        saveImageAsButton.setAction(SAVE_IMAGE_AS_ACTION);
        saveImageAsButton.setFocusable(false);
        saveImageAsButton.setHideActionText(true);
        saveImageAsButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        saveImageAsButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(saveImageAsButton);

        deleteButton.setAction(DELETE_ACTION);
        deleteButton.setFocusable(false);
        deleteButton.setHideActionText(true);
        deleteButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        deleteButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(deleteButton);
        jToolBar1.add(editActionsToolbarSeparator);

        zoomOutButton.setAction(ZOOM_OUT_ACTION);
        zoomOutButton.setFocusable(false);
        zoomOutButton.setHideActionText(true);
        zoomOutButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        zoomOutButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(zoomOutButton);

        zoomInButton.setAction(ZOOM_IN_ACTION);
        zoomInButton.setFocusable(false);
        zoomInButton.setHideActionText(true);
        zoomInButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        zoomInButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(zoomInButton);
        jToolBar1.add(jSeparator2);

        closeButton.setAction(CLOSE_ACTION);
        closeButton.setFocusable(false);
        closeButton.setHideActionText(true);
        closeButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        closeButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(closeButton);

        controlsPanel.add(jToolBar1);

        mainPanel.add(controlsPanel, java.awt.BorderLayout.NORTH);

        nip.setHighQualityRenderingEnabled(false);
        nip.setNavigationImageEnabled(false);

        javax.swing.GroupLayout nipLayout = new javax.swing.GroupLayout(nip);
        nip.setLayout(nipLayout);
        nipLayout.setHorizontalGroup(
            nipLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 994, Short.MAX_VALUE)
        );
        nipLayout.setVerticalGroup(
            nipLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 342, Short.MAX_VALUE)
        );

        mainPanel.add(nip, java.awt.BorderLayout.CENTER);

        jSplitPane1.setBottomComponent(mainPanel);

        getContentPane().add(jSplitPane1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
	this.dispose();
}//GEN-LAST:event_closeButtonActionPerformed

private void fromFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fromFileButtonActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_fromFileButtonActionPerformed

private void openReferenceUrlButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openReferenceUrlButtonActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_openReferenceUrlButtonActionPerformed

private void fromUrlButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fromUrlButtonActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_fromUrlButtonActionPerformed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
            	GuiUtil.init();
				
            	SpInfoViewer iv = new SpInfoViewer();
            	iv.setLocationRelativeTo(null);
            	iv.setVisible(true);
            	iv.setText("Here's some text to test with.\n\nNew line maybe?");
            	iv.setImage("http://www.bradleyzoo.com/GuitarHero/gh3-ps2/expert/cherubrock.no-squeeze.best.png", true);
//            	iv.setImage("http://i24.photobucket.com/albums/c45/bbloot/CherubRock.gif", true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton closeButton;
    private javax.swing.JPanel controlsPanel;
    private javax.swing.JButton deleteButton;
    private javax.swing.JTextArea descriptionTextArea;
    private javax.swing.JToolBar.Separator editActionsToolbarSeparator;
    private org.jdesktop.swingx.JXCollapsiblePane editorCollapsiblePane;
    private javax.swing.JPanel editorPanel;
    private javax.swing.JButton fromFileButton;
    private javax.swing.JButton fromUrlButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JList jList1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JFileChooser jfc;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JButton newButton;
    private jshm.gui.components.NavigableImagePanel nip;
    private javax.swing.JButton openReferenceUrlButton;
    private javax.swing.JTextField referenceUrlField;
    private javax.swing.JButton saveButton;
    private javax.swing.JButton saveImageAsButton;
    private javax.swing.JTextField songDifficultyField;
    private javax.swing.JTextField songInstrumentField;
    private javax.swing.JTextField songTitleField;
    private javax.swing.JTextField titleField;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JLabel titleLabel1;
    private javax.swing.JButton zoomInButton;
    private javax.swing.JButton zoomOutButton;
    // End of variables declaration//GEN-END:variables

}

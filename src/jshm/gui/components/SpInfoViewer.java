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

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.MouseInputAdapter;

import javax.swing.filechooser.FileFilter;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;

import jshm.gui.EditPopupMenu;
import jshm.gui.GuiUtil;

/**
 *
 * @author  Tim
 */
// TODO rename since this will act as the SpInfo viewer/editor as well a standalone image viewer
public class SpInfoViewer extends javax.swing.JFrame {
	static final float
		FIT_WIDTH = -1f,
		FIT_ALL   = -2f,
		MIN_SCALE = 0.1f,
		MAX_SCALE = 10f;
	
	static final String
		SELECT_ZOOM_STR = "Select Zoom...",
		FIT_WIDTH_STR = "Fit Width",
		FIT_ALL_STR   = "Fit All",
		CUSTOM_STR    = "Custom...";
	
	
	ImagePainter imagePainter = new ImagePainter();
	BufferedImage image = null;
	float scale = 1f; // requested scale, could be fit width/all
	float actualScale = 1f; // the actual scale accounting for fit width/all
	Object lastSelectedItem = null;
	
	
	// actions
	final Action
	ZOOM_IN_ACTION = new AbstractAction("Zoom In", new ImageIcon(SpInfoViewer.class.getResource("/jshm/resources/images/toolbar/zoomin32.png"))) {
		public void actionPerformed(ActionEvent e) {
			float newScale = Math.min(getScale() + 0.25f, MAX_SCALE);
			setScale(newScale);
			zoomCombo.setSelectedItem(SELECT_ZOOM_STR);
		}
    },
    ZOOM_OUT_ACTION = new AbstractAction("Zoom Out", new ImageIcon(SpInfoViewer.class.getResource("/jshm/resources/images/toolbar/zoomout32.png"))) {
		public void actionPerformed(ActionEvent e) {
			float newScale = Math.max(getScale() - 0.25f, MIN_SCALE);
			setScale(newScale);
			zoomCombo.setSelectedItem(SELECT_ZOOM_STR);
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
				ImageIO.write(image, "png", selected);
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
		
        setImage((Image) null, true);
        
        EditPopupMenu.add(titleField);
        EditPopupMenu.add(referenceUrlField);
        EditPopupMenu.add(descriptionTextArea);
        
        imagePainter.addMouseWheelListener(new MouseWheelListener() {
			public void mouseWheelMoved(MouseWheelEvent e) {
				if (e.isControlDown()) {
					if (e.getWheelRotation() < 0) {
						ZOOM_IN_ACTION.actionPerformed(null);
					} else {
						ZOOM_OUT_ACTION.actionPerformed(null);
					}
					
					e.consume();
				} else {
					imageScrollPane.dispatchEvent(e);
				}
			}
        });
        
        ActionMap aMap = getRootPane().getActionMap();
        aMap.put("imageZoomIn", ZOOM_IN_ACTION);
        aMap.put("imageZoomOut", ZOOM_OUT_ACTION);
        InputMap inMap = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, KeyEvent.CTRL_DOWN_MASK), "imageZoomIn");
        inMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, KeyEvent.CTRL_DOWN_MASK), "imageZoomOut");
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
		
		if (null != this.image)
			this.image.setAccelerationPriority(0f);
		
		if (null == image) {
			this.image = null;
		} else if (image instanceof BufferedImage) {
			this.image = (BufferedImage) image;
			this.image.setAccelerationPriority(1f);
		} else {
//		    GraphicsEnvironment ge = GraphicsEnvironment
//					.getLocalGraphicsEnvironment();
//			GraphicsDevice defaultScreen = ge.getDefaultScreenDevice();
//			GraphicsConfiguration gc = defaultScreen
//					.getConfigurations()[0];
//
//			this.image = gc.createCompatibleImage(image.getWidth(null), image.getHeight(null), Transparency.OPAQUE);
			
			this.image = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB);
			this.image.setAccelerationPriority(1f);
			this.image.getGraphics().drawImage(image, 
				0, 0, this.image.getWidth(), this.image.getHeight(), 
				0, 0, this.image.getWidth(), this.image.getHeight(), null);
		}
		
		scale = 1f;
		imageScrollPane.getViewport().revalidate();
		imageScrollPane.revalidate();
		imageScrollPane.repaint();
	}
	
	/**
	 * 
	 * @return The actual current scale value, accounting for fit width/all
	 */
	private float getScale() {
		float scale = SpInfoViewer.this.scale;
		SpInfoViewer.this.actualScale = scale;
		
		if (FIT_WIDTH == scale || FIT_ALL == scale) {
			int imw = image.getWidth();
			int imh = image.getHeight();
			int vpw = imageScrollPane.getViewport().getWidth();
			int vph = imageScrollPane.getViewport().getHeight();
			
			float wScale = (float) vpw / (float) imw;
			float hScale = (float) vph / (float) imh;
			
			if (FIT_WIDTH == scale)
				scale = wScale;
			else { // FIT_ALL
				scale = Math.min(wScale, hScale);
			}
			
			scale = Math.min(MAX_SCALE,
				Math.max(MIN_SCALE, scale)
			);
			
			SpInfoViewer.this.actualScale = scale;
		}
		
		return scale;
	}
	
	private void setScale(float scale) {
		if (image != null && this.scale != scale) {
			if (!(FIT_WIDTH == scale || FIT_ALL == scale || (MIN_SCALE <= scale && scale <= MAX_SCALE)))
				throw new IllegalArgumentException("Invalid scale: " + scale);
			
			this.scale = scale;
			getScale();
			imageScrollPane.getViewport().revalidate();
			imageScrollPane.revalidate();
			imageScrollPane.repaint();
			
			ZOOM_IN_ACTION.setEnabled(MAX_SCALE != scale);
			ZOOM_OUT_ACTION.setEnabled(MIN_SCALE != scale);
		}
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
        zoomCombo = new javax.swing.JComboBox();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        closeButton = new javax.swing.JButton();
        imageScrollPane = new JScrollPane(imagePainter);

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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

        zoomCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Select Zoom...", "50%", "75%", "100%", "125%", "150%", "200%", "250%", "300%", "Fit Width", "Fit All", "Custom..." }));
        zoomCombo.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                zoomComboItemStateChanged(evt);
            }
        });
        jToolBar1.add(zoomCombo);
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

        imageScrollPane.setPreferredSize(new java.awt.Dimension(700, 500));
        imageScrollPane.getVerticalScrollBar().setUnitIncrement(50);
        mainPanel.add(imageScrollPane, java.awt.BorderLayout.CENTER);

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

private void zoomComboItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_zoomComboItemStateChanged
	if (evt.getStateChange() == ItemEvent.DESELECTED) {
		lastSelectedItem = evt.getItem();
		return;
	}
	
	String s = (String) zoomCombo.getSelectedItem();
	float newScale = 1f;
	
	try {
		s = s.replaceAll("%", "");
//		System.out.println("s=" + s);
		int i = Integer.parseInt(s);
		newScale = ((float) i) / 100f;
	} catch (NumberFormatException e) {
		if (FIT_ALL_STR.equals(s))
			newScale = FIT_ALL;
		else if (FIT_WIDTH_STR.equals(s))
			newScale = FIT_WIDTH;
		else if (CUSTOM_STR.equals(s)) {
			s = JOptionPane.showInputDialog(this, "Please enter a zoom level:", "Input", JOptionPane.QUESTION_MESSAGE);
			
			if (null == s) {
				if (null != lastSelectedItem)
					zoomCombo.setSelectedItem(lastSelectedItem);
				return;
			}
			
			try {
				int i = Integer.parseInt(s);
				newScale = ((float) i) / 100f;
				
				if (newScale < MIN_SCALE || MAX_SCALE < newScale)
					throw new NumberFormatException();
			} catch (NumberFormatException nfe) {
				JOptionPane.showMessageDialog(this, 
					String.format(
						"You must enter a number between %s and %s.",
						(int) (MIN_SCALE * 100), (int) (MAX_SCALE * 100)),
					"Error", JOptionPane.WARNING_MESSAGE);
				if (null != lastSelectedItem)
					zoomCombo.setSelectedItem(lastSelectedItem);
				return;
			}
		}
	}
	
	setScale(newScale);
}//GEN-LAST:event_zoomComboItemStateChanged

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
            	iv.setImage("http://i24.photobucket.com/albums/c45/bbloot/CherubRock.gif", true);
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
    private javax.swing.JScrollPane imageScrollPane;
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
    private javax.swing.JComboBox zoomCombo;
    private javax.swing.JButton zoomInButton;
    private javax.swing.JButton zoomOutButton;
    // End of variables declaration//GEN-END:variables

    
    class ImagePainter extends JComponent {
    	public ImagePainter() {
    		setOpaque(true);
    		setBackground(Color.BLACK);
    		
    		// make this draggable
    		MouseInputAdapter mia = new MouseInputAdapter() {
    			int xDiff, yDiff;

//    			boolean isDragging;

    			Container c;

    			public void mouseDragged(MouseEvent e) {
    				c = getParent();
    				if (c instanceof JViewport) {
    					JViewport jv = (JViewport) c;
    					Point p = jv.getViewPosition();
    					int newX = p.x - (e.getX() - xDiff);
    					int newY = p.y - (e.getY() - yDiff);

    					int maxX = getWidth() - jv.getWidth();
    					int maxY = getHeight() - jv.getHeight();
    					if (newX < 0) newX = 0;
    					if (newX > maxX) newX = maxX;
    					if (newY < 0) newY = 0;
    					if (newY > maxY) newY = maxY;

    					jv.setViewPosition(new Point(newX, newY));
    				}
    			}

    			public void mousePressed(MouseEvent e) {
    				setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
    				xDiff = e.getX();
    				yDiff = e.getY();
    			}

    			public void mouseReleased(MouseEvent e) {
    				setCursor(null);
    			}
    		};

    		addMouseMotionListener(mia);
    		addMouseListener(mia);
    	}
    	
    	final Dimension MIN_SIZE = new Dimension(400, 300);
    	public Dimension getMinimumSize() {
    		return MIN_SIZE;
    	}
    	
    	public Dimension getPreferredSize() {
    		float scale = getScale();
			
    		return
    			null != image
    			? new Dimension((int) (scale * image.getWidth()), (int) (scale * image.getHeight()))
    			: MIN_SIZE;
    	}
    	
		public void paintComponent(Graphics g) {
			if (null == image) return;
			
			Graphics2D g2 = (Graphics2D) g;

			float scale = getScale();
			
			if (scale < 1f) {
				g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			}
			
			float iscale = 1f / scale;
			Rectangle clip = g2.getClipBounds();
			Rectangle scaled = new Rectangle(
				(int) (iscale * clip.x),
				(int) (iscale * clip.y),
				(int) (iscale * clip.width),
				(int) (iscale * clip.height)
			);
			
			g2.drawImage(image,
				clip.x, clip.y, clip.x + clip.width, clip.y + clip.height,
				scaled.x, scaled.y, scaled.x + scaled.width, scaled.y + scaled.height,
				getBackground(), null);
		}
    }
}

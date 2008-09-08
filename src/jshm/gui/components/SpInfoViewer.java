package jshm.gui.components;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.MouseInputAdapter;

import jshm.gui.EditPopupMenu;
import jshm.gui.GuiUtil;

/**
 *
 * @author  Tim
 */
public class SpInfoViewer extends javax.swing.JFrame {    
	ImagePainter imagePainter = new ImagePainter();
	BufferedImage image = null;
	float scale = 1f;
	
	
	// actions
	final Action
	ZOOM_IN_ACTION = new AbstractAction("Zoom In", new ImageIcon(SpInfoViewer.class.getResource("/jshm/resources/images/toolbar/zoomin32.png"))) {
		public void actionPerformed(ActionEvent e) {
			int newScale = Math.min(zoomSlider.getValue() + 25, zoomSlider.getMaximum());
			zoomSlider.setValue(newScale);
		}
    },
    ZOOM_OUT_ACTION = new AbstractAction("Zoom Out", new ImageIcon(SpInfoViewer.class.getResource("/jshm/resources/images/toolbar/zoomout32.png"))) {
		public void actionPerformed(ActionEvent e) {
			int newScale = Math.max(zoomSlider.getValue() - 25, zoomSlider.getMinimum());
			zoomSlider.setValue(newScale);
		}
	},
	RESET_ZOOM_ACTION = new AbstractAction("Reset Zoom", new ImageIcon(SpInfoViewer.class.getResource("/jshm/resources/images/toolbar/refresh32.png"))) {
		public void actionPerformed(ActionEvent e) {
			zoomSlider.setValue(100);
		}
	},
	FIT_WIDTH_ACTION = new AbstractAction("Fit Width", new ImageIcon(SpInfoViewer.class.getResource("/jshm/resources/images/toolbar/window32.png"))) {
		public void actionPerformed(ActionEvent e) {
			int imw  = image.getWidth(); 
			int vpw = imageScrollPane.getViewport().getWidth();
			float newScale = (float) vpw / (float) imw;
			newScale = Math.min((float) zoomSlider.getMaximum() / 100f,
				Math.max((float) zoomSlider.getMinimum() / 100f, newScale)
			);
			zoomSlider.setValue((int) (newScale * 100));
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
	SAVE_ACTION = new AbstractAction("Save", new ImageIcon(SpInfoViewer.class.getResource("/jshm/resources/images/toolbar/saveas32.png"))) {
		public void actionPerformed(ActionEvent e) {
			
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
    		ZOOM_IN_ACTION, ZOOM_OUT_ACTION, RESET_ZOOM_ACTION, FIT_WIDTH_ACTION,
    		CLOSE_ACTION,
    		NEW_ACTION, SAVE_ACTION, DELETE_ACTION})
    		a.putValue(Action.SHORT_DESCRIPTION, a.getValue(Action.NAME));

        initComponents();
        
        EditPopupMenu.add(titleField);
        EditPopupMenu.add(referenceUrlField);
        EditPopupMenu.add(textPane);
        
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
			
			textPane.setText(text);
//		}
	}
	
	public void setImage(String url) {
		try {
			setImage(new URL(url));
		} catch (MalformedURLException e) {
			setImage((Image) null);
			e.printStackTrace();
		}
	}
	
	public void setImage(final URL url) {
		if (null == url) {
			setImage((Image) null);
			return;
		}
		
		new Thread(new Runnable() {
			public void run() {
				try {
					final BufferedImage im = ImageIO.read(url);
					
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							setImage(im);
						}
					});
				} catch (Exception e) {
					setImage((Image) null);
				}
			}
		}).start();
	}
	
	public void setImage(ImageIcon icon) {
		setImage(icon.getImage());
	}
	
	public void setImage(Image image) {
		if (null == image) {
			this.image = null;
		} else if (image instanceof BufferedImage) {
			this.image = (BufferedImage) image;
		} else {
			this.image = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB);
			this.image.getGraphics().drawImage(image, 
				0, 0, this.image.getWidth(), this.image.getHeight(), 
				0, 0, this.image.getWidth(), this.image.getHeight(), null);
		}
		
		scale = 1f;
		imageScrollPane.getViewport().revalidate();
		imageScrollPane.revalidate();
		imageScrollPane.repaint();
	}
	
	private void resizeImage(float scale) {
		if (image != null && this.scale != scale) {
			this.scale = scale;
			imageScrollPane.getViewport().revalidate();
			imageScrollPane.revalidate();
			imageScrollPane.repaint();
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

        jSplitPane1 = new javax.swing.JSplitPane();
        editorCollapsiblePane = new org.jdesktop.swingx.JXCollapsiblePane();
        editorPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        textPane = new javax.swing.JTextPane();
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
        mainPanel = new javax.swing.JPanel();
        controlsPanel = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        newButton = new javax.swing.JButton();
        saveButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        zoomOutButton = new javax.swing.JButton();
        zoomInButton = new javax.swing.JButton();
        resetButton = new javax.swing.JButton();
        fitWidthButton = new javax.swing.JButton();
        zoomSlider = new javax.swing.JSlider();
        zoomLabel = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        closeButton = new javax.swing.JButton();
        imageScrollPane = new JScrollPane(imagePainter);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("SP Path Viewer");

        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        editorCollapsiblePane.getContentPane().setLayout(new java.awt.BorderLayout());

        jScrollPane1.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createEmptyBorder(0, 5, 5, 5), javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createTitledBorder("Description"), javax.swing.BorderFactory.createEmptyBorder(0, 5, 5, 5))));

        textPane.setEditable(false);
        jScrollPane1.setViewportView(textPane);

        jPanel1.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createEmptyBorder(5, 0, 0, 5), javax.swing.BorderFactory.createTitledBorder("Details")));

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

        referenceUrlField.setText("jTextField1");

        titleField.setText("jTextField1");

        openReferenceUrlButton.setText("Open...");

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
                            .addComponent(referenceUrlField, javax.swing.GroupLayout.DEFAULT_SIZE, 524, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(openReferenceUrlButton))
                    .addComponent(titleField, javax.swing.GroupLayout.DEFAULT_SIZE, 618, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
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

        jScrollPane2.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 0, 0), javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createTitledBorder("Saved Paths"), javax.swing.BorderFactory.createEmptyBorder(0, 5, 5, 5))));

        jList1.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Path 1", "Path 2", "..." };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane2.setViewportView(jList1);

        javax.swing.GroupLayout editorPanelLayout = new javax.swing.GroupLayout(editorPanel);
        editorPanel.setLayout(editorPanelLayout);
        editorPanelLayout.setHorizontalGroup(
            editorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(editorPanelLayout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 961, Short.MAX_VALUE)
        );
        editorPanelLayout.setVerticalGroup(
            editorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(editorPanelLayout.createSequentialGroup()
                .addGroup(editorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane2, 0, 0, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 96, Short.MAX_VALUE))
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

        deleteButton.setAction(DELETE_ACTION);
        deleteButton.setFocusable(false);
        deleteButton.setHideActionText(true);
        deleteButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        deleteButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(deleteButton);
        jToolBar1.add(jSeparator1);

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

        resetButton.setAction(RESET_ZOOM_ACTION);
        resetButton.setFocusable(false);
        resetButton.setHideActionText(true);
        resetButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        resetButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(resetButton);

        fitWidthButton.setAction(FIT_WIDTH_ACTION);
        fitWidthButton.setFocusable(false);
        fitWidthButton.setHideActionText(true);
        fitWidthButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        fitWidthButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(fitWidthButton);

        zoomSlider.setMajorTickSpacing(150);
        zoomSlider.setMaximum(500);
        zoomSlider.setMinimum(50);
        zoomSlider.setMinorTickSpacing(50);
        zoomSlider.setPaintTicks(true);
        zoomSlider.setValue(100);
        zoomSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                zoomSliderStateChanged(evt);
            }
        });
        jToolBar1.add(zoomSlider);

        zoomLabel.setText("100%");
        jToolBar1.add(zoomLabel);
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

private void zoomSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_zoomSliderStateChanged
	zoomLabel.setText(zoomSlider.getValue() + "%");
	if (!zoomSlider.getValueIsAdjusting()) {
		int value = zoomSlider.getValue();
		resizeImage(value / 100f);
		ZOOM_IN_ACTION.setEnabled(value != zoomSlider.getMaximum());
		ZOOM_OUT_ACTION.setEnabled(value != zoomSlider.getMinimum());
	}
}//GEN-LAST:event_zoomSliderStateChanged

private void fromFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fromFileButtonActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_fromFileButtonActionPerformed

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
            	iv.setImage("http://i24.photobucket.com/albums/c45/bbloot/CherubRock.gif");
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton closeButton;
    private javax.swing.JPanel controlsPanel;
    private javax.swing.JButton deleteButton;
    private org.jdesktop.swingx.JXCollapsiblePane editorCollapsiblePane;
    private javax.swing.JPanel editorPanel;
    private javax.swing.JButton fitWidthButton;
    private javax.swing.JButton fromFileButton;
    private javax.swing.JButton fromUrlButton;
    private javax.swing.JScrollPane imageScrollPane;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JList jList1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JButton newButton;
    private javax.swing.JButton openReferenceUrlButton;
    private javax.swing.JTextField referenceUrlField;
    private javax.swing.JButton resetButton;
    private javax.swing.JButton saveButton;
    private javax.swing.JTextPane textPane;
    private javax.swing.JTextField titleField;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JButton zoomInButton;
    private javax.swing.JLabel zoomLabel;
    private javax.swing.JButton zoomOutButton;
    private javax.swing.JSlider zoomSlider;
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
    		return
    			null != image
    			? new Dimension((int) (scale * image.getWidth()), (int) (scale * image.getHeight()))
    			: MIN_SIZE;
    	}
    	
		public void paintComponent(Graphics g) {
			if (null == image) return;
			
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			
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

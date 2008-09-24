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
 * ScoreEditorPanel.java
 *
 * Created on August 25, 2008, 1:16 AM
 */

package jshm.gui;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

import jshm.Score;
import jshm.Song;
import jshm.StreakStrategy;
import jshm.gh.GhScore;
import jshm.gui.datamodels.GhMyScoresTreeTableModel;
import jshm.gui.editors.GhMyScoresRatingEditor;
import jshm.gui.renderers.ScoreEditorSongComboRenderer;
import jshm.hibernate.HibernateUtil;
import jshm.rb.RbScore;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.jdesktop.swingx.JXCollapsiblePane;
import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import org.jdesktop.swingx.autocomplete.ObjectToStringConverter;
import org.jdesktop.swingx.error.ErrorInfo;

/**
 *
 * @author  Tim
 */
public class ScoreEditorPanel extends javax.swing.JPanel implements PropertyChangeListener {
	static final Logger LOG = Logger.getLogger(ScoreEditorPanel.class.getName());
	
	private GUI gui;
	
	/**
	 * Indicates if our current score is new from clicking
	 * our new button or not.
	 */
	private boolean isNewScore = false;
	private boolean hasScoreChanged = false;
	
	public ScoreEditorPanel() {
		this(null);
	}
	
    /** Creates new form ScoreEditorPanel */
    public ScoreEditorPanel(GUI gui) {
    	this.gui = gui;
        initComponents();
        
        MySongChangeListener mscl = new MySongChangeListener();
        
        for (JTextComponent c : new JTextComponent[] {
        	scoreField, percentField, streakField, commentField, imageUrlField, videoUrlField}) {
        	EditPopupMenu.add(c);
        	c.getDocument().addDocumentListener(mscl);
        }
        
        songCombo.addItemListener(mscl);
        ratingCombo.addItemListener(mscl);
    }

    private class MySongChangeListener implements DocumentListener, ItemListener {
    	private void _() {
    		hasScoreChanged = true;
    	}
    	
		public void changedUpdate(DocumentEvent e) { _(); }
		public void insertUpdate(DocumentEvent e) {	_(); }
		public void removeUpdate(DocumentEvent e) {	_(); }
		public void itemStateChanged(ItemEvent e) {	_(); }
    }
    
	@Override
	public void setEnabled(boolean b) {
		super.setEnabled(b);
		scoreField.setEditable(b);
		ratingCombo.setEnabled(b);
		percentField.setEditable(b);
		streakField.setEditable(b);
		commentField.setEditable(b);
		imageUrlField.setEditable(b);
		videoUrlField.setEditable(b);
		saveButton.setEnabled(b);
	}
	
	private final ListCellRenderer SONG_COMBO_RENDERER = new ScoreEditorSongComboRenderer();
	private static final String SELECT_A_SONG = "Type a song name...";
	private static final ObjectToStringConverter SONG_COMBO_CONVERTER = new ObjectToStringConverter() {
		@Override
		public String getPreferredStringForItem(Object item) {
			if (null == item) return null;
			if (item instanceof Song)
				return ((Song) item).getTitle();
			return item.toString();
		}
	};
	
	public void setSongs(List<? extends Song> songs) {
		songCombo.setRenderer(SONG_COMBO_RENDERER);
		DefaultComboBoxModel model = (DefaultComboBoxModel) songCombo.getModel();
		model.removeAllElements();
		
		model.addElement(SELECT_A_SONG);
		for (Song s : songs)
			model.addElement(s);
		
		AutoCompleteDecorator.decorate(songCombo, SONG_COMBO_CONVERTER);
		
		newButton.setEnabled(true);
	}
	
	private Score score = null;
	
	public void setSong(Song song) {
		songCombo.setSelectedItem(song);
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		final Object src = evt.getSource();		
		final Score score = (Score) src;
		final String name = evt.getPropertyName();
		final Object newValue = evt.getNewValue();
		
		JTextField field = null;
		
		if (name.equals("score")) {
			field = scoreField;
		} else if (name.equals("part1Streak")) {
			field = streakField;
		} else if (name.equals("comment")) {
			field = commentField;
		} else if (name.equals("imageUrl")) {
			field = imageUrlField;
		} else if (name.equals("videoUrl")) {
			field = videoUrlField;
		} else if (name.equals("rating")) {
			ratingCombo.setSelectedItem(score.getRatingIcon(true));
		} else if (name.equals("part1HitPercent")) {
			float f = (Float) newValue;
			percentField.setText(0.0f != f ? String.valueOf((int) (f * 100)) : "");
		}
		
		if (null != field) {
			field.setText(!newValue.equals(0) ? newValue.toString() : "");
		}
		
		// it has changed via the quick editor, which has already
		// saved the change to the db
		hasScoreChanged = false;
	}
	
	public void setScore(final Score score) {		
		if (null != this.score) {
			if (this.score.isEditable() && hasScoreChanged) {
				final int ret = JOptionPane.showConfirmDialog(gui, 
					"The current score has been modified.\n" +
					"Do you want to save it?\n\n" +
					
					"Yes: save and continue current action\n" +
					"No: lose changes and continue current action\n" +
					"Cancel: continue editing the current score",
					"Confirm", JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE);
				
				if (JOptionPane.CANCEL_OPTION == ret)
					return;
				
				if (JOptionPane.YES_OPTION == ret)
					saveButtonActionPerformed(null);
			}
			
			this.score.removePropertyChangeListener(this);
		}
		
		if (null != score &&
			(null == this.score || this.score.getClass() != score.getClass())) {
			JComboBox newRatingCombo = GhMyScoresRatingEditor.createRatingComboBox(gui.getCurGame());
			ratingCombo.setModel(newRatingCombo.getModel());
			ratingCombo.setKeySelectionManager(newRatingCombo.getKeySelectionManager());
			ratingCombo.validate();
		}
		
		this.score = score;
		boolean scoreNotNull = false;
		
		setEnabled(score);
		
		if (null != score) {
			scoreNotNull = true;
			score.addPropertyChangeListener(this);
		}
		
		songCombo.setSelectedItem(scoreNotNull && null != score.getSong() ? score.getSong() : SELECT_A_SONG);
		scoreField.setText(scoreNotNull && score.getScore() > 0 ? String.valueOf(score.getScore()) : "");
		ratingCombo.setSelectedItem(scoreNotNull ? score.getRatingIcon(true) : ratingCombo.getItemAt(0));
		percentField.setText(scoreNotNull && score.getHitPercent() != 0f ? String.valueOf((int) (score.getHitPercent() * 100)) : "");
		streakField.setText(scoreNotNull && score.getPartStreak(1) > 0 ? String.valueOf(score.getPartStreak(1)) : "");
		commentField.setText(scoreNotNull ? score.getComment() : "");
		imageUrlField.setText(scoreNotNull ? score.getImageUrl() : "");
		videoUrlField.setText(scoreNotNull ? score.getVideoUrl() : "");
		
		songCombo.setEnabled(false);
		
		isNewScore = false;
		hasScoreChanged = false;
	}
	
	private void setEnabled(Score score) {
		if (null != score) {
			switch (score.getStatus()) {
				case NEW:
				case TEMPLATE:
					setEnabled(true);
					saveButton.setEnabled(true);
					break;
					
				default:
					setEnabled(false);
					saveButton.setEnabled(false);
					break;
			}

			songCombo.setEnabled(isNewScore);
			imageUrlOpenButton.setEnabled(true);
			videoUrlOpenButton.setEnabled(true);
		} else {
			setEnabled(false);
			songCombo.setEnabled(false);
			imageUrlOpenButton.setEnabled(false);
			videoUrlOpenButton.setEnabled(false);
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

        scoreField = new javax.swing.JTextField();
        ratingCombo = new javax.swing.JComboBox();
        percentField = new javax.swing.JTextField();
        streakField = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        commentField = new javax.swing.JTextField();
        imageUrlField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        videoUrlField = new javax.swing.JTextField();
        songCombo = new javax.swing.JComboBox();
        imageUrlOpenButton = new javax.swing.JButton();
        videoUrlOpenButton = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        saveButton = new javax.swing.JButton();
        newButton = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        hideButton = new javax.swing.JButton();

        scoreField.setEditable(false);

        ratingCombo.setEnabled(false);

        percentField.setEditable(false);

        streakField.setEditable(false);

        jLabel1.setLabelFor(commentField);
        jLabel1.setText("Comment:");

        jLabel2.setLabelFor(imageUrlField);
        jLabel2.setText("Image URL:");

        commentField.setEditable(false);

        imageUrlField.setEditable(false);

        jLabel3.setLabelFor(videoUrlField);
        jLabel3.setText("Video URL:");

        videoUrlField.setEditable(false);

        songCombo.setEnabled(false);

        imageUrlOpenButton.setText("Open");
        imageUrlOpenButton.setToolTipText("Opens the image viewer or launches external browser if not an image");
        imageUrlOpenButton.setEnabled(false);
        imageUrlOpenButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                imageUrlOpenButtonActionPerformed(evt);
            }
        });

        videoUrlOpenButton.setText("Open");
        videoUrlOpenButton.setToolTipText("Open the Video URL in an external browser window");
        videoUrlOpenButton.setEnabled(false);
        videoUrlOpenButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                videoUrlOpenButtonActionPerformed(evt);
            }
        });

        jLabel4.setLabelFor(songCombo);
        jLabel4.setText("Song:");

        saveButton.setText("Save");
        saveButton.setEnabled(false);
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });

        newButton.setText("New");
        newButton.setEnabled(false);
        newButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newButtonActionPerformed(evt);
            }
        });

        jLabel5.setLabelFor(scoreField);
        jLabel5.setText("Score:");

        jLabel6.setLabelFor(percentField);
        jLabel6.setText("%:");

        jLabel7.setLabelFor(streakField);
        jLabel7.setText("Streak:");

        jLabel8.setLabelFor(ratingCombo);
        jLabel8.setText("Rating:");

        hideButton.setText("Hide");
        hideButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hideButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel3)
                    .addComponent(jLabel2)
                    .addComponent(jLabel1)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(imageUrlField, javax.swing.GroupLayout.DEFAULT_SIZE, 593, Short.MAX_VALUE)
                            .addComponent(videoUrlField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 593, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(imageUrlOpenButton)
                            .addComponent(videoUrlOpenButton)))
                    .addComponent(commentField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 673, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(newButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(saveButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(hideButton))
                            .addComponent(songCombo, 0, 316, Short.MAX_VALUE))
                        .addGap(16, 16, 16)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(scoreField, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(ratingCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(percentField, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7)
                            .addComponent(streakField, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(newButton)
                            .addComponent(saveButton)
                            .addComponent(hideButton))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(songCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(jLabel8)
                            .addComponent(jLabel6)
                            .addComponent(jLabel7))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(streakField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(percentField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ratingCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(scoreField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(commentField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(imageUrlOpenButton)
                    .addComponent(imageUrlField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(videoUrlOpenButton)
                    .addComponent(videoUrlField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

private void imageUrlOpenButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_imageUrlOpenButtonActionPerformed
	if (imageUrlField.getText().isEmpty()) return;
	gui.openImageOrBrowser(imageUrlField.getText());
}//GEN-LAST:event_imageUrlOpenButtonActionPerformed

private void videoUrlOpenButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_videoUrlOpenButtonActionPerformed
	if (!videoUrlField.getText().isEmpty()) {
		jshm.util.Util.openURL(videoUrlField.getText());
	}
}//GEN-LAST:event_videoUrlOpenButtonActionPerformed

private void newButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newButtonActionPerformed
//	boolean wasNullScore = null == score;
	Song lastSong =
		songCombo.getSelectedItem() instanceof Song
		? (Song) songCombo.getSelectedItem() : null;
		
	setScore(Score.createNewScoreTemplate(
		gui.getCurGame(), gui.getCurGroup(), gui.getCurDiff(), lastSong));
	
	isNewScore = true;
	
	if (/*wasNullScore &&*/ null != lastSong) {
		setSong(lastSong);
	}
	
	songCombo.setEnabled(true);
	songCombo.getEditor().selectAll();
	songCombo.requestFocusInWindow();
}//GEN-LAST:event_newButtonActionPerformed

private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
	try {
		switch (score.getStatus()) {
			case NEW:
			case TEMPLATE:
				score.setStatus(Score.Status.NEW);
				
				if (!(songCombo.getSelectedItem() instanceof Song))
					throw new IllegalArgumentException("You must select a song");
				
				score.setSong((Song) songCombo.getSelectedItem());
				score.setComment(commentField.getText().trim());
				score.setImageUrl(imageUrlField.getText().trim());
				score.setVideoUrl(videoUrlField.getText().trim());
	
				try {
					String s = scoreField.getText();
					score.setScore(
						s.isEmpty() ? 0 :
						Integer.parseInt(s));
				} catch (Exception e) {
					throw new NumberFormatException("Invalid score: " + scoreField.getText());
				}
	
				score.setRating(0);
				
				if (score instanceof GhScore) {
					for (int i : new Integer[] {3, 4, 5})
						if (GhScore.getRatingIcon(i) == ratingCombo.getSelectedItem()) {
							score.setRating(i);
							break;
						}
				} else if (score instanceof RbScore) {
					for (int i : new Integer[] {1, 2, 3, 4, 5, 6})
						if (RbScore.getRatingIcon(i) == ratingCombo.getSelectedItem()) {
							score.setRating(i);
							break;
						}
				}
	
				try {
					String s = percentField.getText();
					score.setPartHitPercent(1,
						s.isEmpty() ? 0f :
						Integer.parseInt(s) / 100.0f);
				} catch (Exception e) {
					throw new NumberFormatException("Invalid percent: " + percentField.getText());
				}
	
				try {
					String s = streakField.getText();
					int streak = s.isEmpty() ? 0 : Integer.parseInt(s);
					
					if (score.getGameTitle().getStreakStrategy() == StreakStrategy.BY_SCORE) {
						score.setStreak(streak);
					}
					
					score.setPartStreak(1, streak);
				} catch (Exception e) {
					throw new NumberFormatException("Invalid streak: " + streakField.getText());
				}
				
				Session sess = null;
				Transaction tx = null;
				
				try {
					sess = HibernateUtil.getCurrentSession();
					tx = sess.beginTransaction();
					sess.saveOrUpdate(score);
					sess.getTransaction().commit();
				} catch (Exception e) {
					LOG.log(Level.WARNING, "Error saving to database", e);
					if (null != tx) tx.rollback();
					throw e;
				} finally {
					if (sess.isOpen()) sess.close();
				}
				
				((GhMyScoresTreeTableModel) gui.tree.getTreeTableModel())
					.insertScore(score);
				gui.tree.repaint();
				
				// update the enabled state of the fields
				setEnabled(isNewScore ? null : score);
				hasScoreChanged = false;
		}
	} catch (Throwable t) {
		LOG.log(Level.WARNING, "Failed to save score", t);
		ErrorInfo ei = new ErrorInfo("Error", "Failed to save score", null, null, t, null, null);
		JXErrorPane.showDialog(null, ei);
	}
}//GEN-LAST:event_saveButtonActionPerformed

private void hideButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hideButtonActionPerformed
	try {
		java.awt.Container parent = getParent().getParent().getParent();
		if (parent instanceof JXCollapsiblePane) {
			((JXCollapsiblePane) parent).setCollapsed(true);
		}
	} catch (NullPointerException e) {}
}//GEN-LAST:event_hideButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField commentField;
    private javax.swing.JButton hideButton;
    private javax.swing.JTextField imageUrlField;
    private javax.swing.JButton imageUrlOpenButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    javax.swing.JButton newButton;
    private javax.swing.JTextField percentField;
    private javax.swing.JComboBox ratingCombo;
    private javax.swing.JButton saveButton;
    private javax.swing.JTextField scoreField;
    private javax.swing.JComboBox songCombo;
    private javax.swing.JTextField streakField;
    private javax.swing.JTextField videoUrlField;
    private javax.swing.JButton videoUrlOpenButton;
    // End of variables declaration//GEN-END:variables
}

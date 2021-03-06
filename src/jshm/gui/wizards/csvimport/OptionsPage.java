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
 * OptionsPage.java
 *
 * Created on September 21, 2008, 9:58 PM
 */

package jshm.gui.wizards.csvimport;

import java.awt.Component;
import java.io.File;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import jshm.Difficulty;
import jshm.Game;
import jshm.Instrument;
import jshm.gh.GhGame;

import org.netbeans.spi.wizard.WizardController;
import org.netbeans.spi.wizard.WizardPage;

/**
 *
 * @author  Tim
 */
public class OptionsPage extends WizardPage {
	Game game;
	Instrument.Group group;
	Difficulty diff;
	
	public OptionsPage() {
		this(GhGame.GH3_XBOX360, null, null);
	}
	
    /** Creates new form OptionsPage */
	public OptionsPage(Game game, Instrument.Group group, Difficulty diff) {
		super("options", "Select options");
		
		this.game = game;
		this.group = group;
		this.diff = diff;
		
        initComponents();
        
        gameField.setText(game.toString());
        setDiffComboModel();
        setInstrumentComboModel();
        
        // force validation so the user knows to select a file
        putWizardData("file", "");
    }
	
	protected String validateContents(final Component component, Object event) {
		if (fileField.getText().isEmpty() || !new File(fileField.getText()).exists())
			return "You must select a valid file.";
		
		setForwardNavigationMode(WizardController.MODE_CAN_CONTINUE_OR_FINISH);
		return null;
	}
	
	private void setDiffComboModel() {
		DefaultComboBoxModel model = new DefaultComboBoxModel();
		
		for (Difficulty d : Difficulty.values()) {
			if (Difficulty.CO_OP == d) break;
			model.addElement(d);
		}
		
		diffCombo.setModel(model);
		diffCombo.setSelectedItem(diff);
	}

	private void setInstrumentComboModel() {
		DefaultComboBoxModel model = new DefaultComboBoxModel();

		for (Instrument.Group g : game.title.getSupportedInstrumentGroups()) {
			model.addElement(g);
		}
		
		instrumentCombo.setModel(model);
		
		if (null != group) {
			instrumentCombo.setSelectedItem(group);
		}
		
		if (model.getSize() == 1) {
			instrumentCombo.setSelectedIndex(0);
			instrumentCombo.setEnabled(false);
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
        jLabel1 = new javax.swing.JLabel();
        fileField = new javax.swing.JTextField();
        selectFileButton = new javax.swing.JButton();
        gameField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        diffCombo = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        instrumentCombo = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        importDuplicatesCheckBox = new javax.swing.JCheckBox();

        jfc.setCurrentDirectory(new File("."));
        jfc.setFileFilter(new FileFilter() {
            public boolean accept(File f) {
                String name = f.getName().toLowerCase();
                return f.isDirectory() || name.endsWith(".csv") || name.endsWith(".txt");
            }

            public String getDescription() {
                return "CSV Files (*.csv, *.txt)";
            }
        });
        jfc.setName(""); // NOI18N

        setPreferredSize(new java.awt.Dimension(644, 296));

        jLabel1.setText("File:");

        fileField.setEditable(false);
        fileField.setName("file"); // NOI18N

        selectFileButton.setText("...");
        selectFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectFileButtonActionPerformed(evt);
            }
        });

        gameField.setEditable(false);

        jLabel2.setText("Game:");

        jLabel3.setText("Default Difficulty:");

        diffCombo.setName("difficulty"); // NOI18N

        jLabel4.setText("Default Instrument:");

        instrumentCombo.setName("instrument"); // NOI18N

        jLabel5.setText("<html>* The file must be a CSV (comma-separated values) file. Most spreadsheet programs can export or \"Save As\" this format.");

        importDuplicatesCheckBox.setText("Import duplicate scores");
        importDuplicatesCheckBox.setName("duplicates"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 614, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(fileField, javax.swing.GroupLayout.DEFAULT_SIZE, 505, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(selectFileButton))
                            .addComponent(gameField, javax.swing.GroupLayout.DEFAULT_SIZE, 561, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(diffCombo, 0, 469, Short.MAX_VALUE)
                            .addComponent(instrumentCombo, 0, 469, Short.MAX_VALUE)))
                    .addComponent(importDuplicatesCheckBox))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(gameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(selectFileButton)
                    .addComponent(fileField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 52, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(diffCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(instrumentCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(importDuplicatesCheckBox)
                .addGap(51, 51, 51))
        );
    }// </editor-fold>//GEN-END:initComponents

private void selectFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectFileButtonActionPerformed
	jfc.showOpenDialog(this);
	
	if (null == jfc.getSelectedFile() || !jfc.getSelectedFile().exists()) {
		JOptionPane.showMessageDialog(this, "That file is invalid.\nPlease select another.", "Error", JOptionPane.WARNING_MESSAGE);
	} else {
		fileField.setText(jfc.getSelectedFile().getAbsolutePath());
	}
}//GEN-LAST:event_selectFileButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox diffCombo;
    private javax.swing.JTextField fileField;
    private javax.swing.JTextField gameField;
    private javax.swing.JCheckBox importDuplicatesCheckBox;
    private javax.swing.JComboBox instrumentCombo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JFileChooser jfc;
    private javax.swing.JButton selectFileButton;
    // End of variables declaration//GEN-END:variables

}

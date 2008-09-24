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
package jshm.gui.wizards.scoreupload;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import jshm.gui.datamodels.GhMyScoresTreeTableModel;

import org.jdesktop.swingx.JXTreeTable;
import org.netbeans.spi.wizard.WizardPage;

public class VerifyScoresPage extends WizardPage {
	JCheckBox cb;
	JXTreeTable tt;
	
	public VerifyScoresPage(GhMyScoresTreeTableModel model) {
		super("verifyScores", "Verify scores", true);
		
		JPanel northPanel = new JPanel(new BorderLayout());
		cb = new JCheckBox("Upload scores with an unknown status", false);
		cb.setName("uploadUnknown");
		northPanel.add(cb, BorderLayout.SOUTH);
		northPanel.add(
			new JLabel("<html>Ensure the following scores are correct.<br>" +
						"If they aren't, cancel this wizard, make the necessary corrections, and restart the wizard.<br>" +
						"If a new score is not shown below, you may have forgotten to enter the number of points for the score."),
			BorderLayout.CENTER);
		
		tt = new JXTreeTable(model);
		model.setParent(tt);
		tt.setEditable(false);
		tt.expandAll();
//		tt.setHorizontalScrollEnabled(true);
		tt.getColumnExt(5).setVisible(false); // hide submit date
		tt.packAll();
//		tt.setName("treeTableData");
		
		setPreferredSize(new Dimension(700, 600));
		setLayout(new BorderLayout(0, 10));
		add(northPanel, BorderLayout.NORTH);
		add(new JScrollPane(tt), BorderLayout.CENTER);
		
		putWizardData("treeTableData", model);
	}
}

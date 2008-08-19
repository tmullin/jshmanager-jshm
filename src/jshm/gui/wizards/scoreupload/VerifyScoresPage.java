package jshm.gui.wizards.scoreupload;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JScrollPane;

import jshm.gui.datamodels.GhMyScoresTreeTableModel;

import org.jdesktop.swingx.JXTreeTable;
import org.netbeans.spi.wizard.WizardPage;

public class VerifyScoresPage extends WizardPage {
	JXTreeTable tt;
	
	public VerifyScoresPage(GhMyScoresTreeTableModel model) {
		super("verifyScores", "Verify scores", false);
		tt = new JXTreeTable(model);
		model.setParent(tt);
		tt.setEditable(false);
		tt.expandAll();
//		tt.setHorizontalScrollEnabled(true);
		tt.getColumnExt(5).setVisible(false); // hide submit date
		tt.packAll();
		tt.setName("treeTableData");
		
		setLayout(new BorderLayout(0, 10));
		add(new JLabel("<html>Ensure the following scores are correct.<br>" +
						"If they aren't, cancel this wizard, make the necessary corrections, and restart the wizard.<br>" +
						"If a new score is not shown below, you may have forgotten to enter the number of points for the score."),
			BorderLayout.NORTH);
		add(new JScrollPane(tt), BorderLayout.CENTER);
		
		putWizardData("treeTableData", model);
	}
}

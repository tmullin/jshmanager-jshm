package jshm.gui.wizards.scoreupload;

import javax.swing.JLabel;

import jshm.gui.datamodels.GhMyScoresTreeTableModel;

import org.netbeans.spi.wizard.WizardPage;

public class UploadScoresPage extends WizardPage {
	JLabel label;
	
	public UploadScoresPage() {
		super("uploadScores", "Upload scores");
		label = new JLabel();
		add(label);
	}
	
	@Override
	protected void renderingPage() {
		GhMyScoresTreeTableModel model = 
			(GhMyScoresTreeTableModel) getWizardData("treeTableData");
		label.setText("<html>Ready to upload.<br>Click Finish to upload " + model.getScoreCount() + " score(s).");
	}
}

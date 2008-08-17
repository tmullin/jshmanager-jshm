package jshm.gui.wizards.scoreupload;

import java.awt.BorderLayout;

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
		tt.expandAll();
//		tt.setHorizontalScrollEnabled(true);
		tt.getColumnExt(5).setVisible(false); // hide submit date
		tt.packAll();
		tt.setName("treeTableData");
		
		setLayout(new BorderLayout());
		add(new JScrollPane(tt), BorderLayout.CENTER);
		
		putWizardData("treeTableData", model);
	}
}

package jshm.gui.wizards.scoreupload;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//import javax.swing.JOptionPane;

import jshm.gh.GhScore;
import jshm.gui.datamodels.GhMyScoresTreeTableModel;

import org.netbeans.spi.wizard.ResultProgressHandle;
import org.netbeans.spi.wizard.Summary;
import org.netbeans.spi.wizard.Wizard;
import org.netbeans.spi.wizard.DeferredWizardResult;
import org.netbeans.spi.wizard.WizardException;
import org.netbeans.spi.wizard.WizardPage;
import org.netbeans.spi.wizard.WizardPage.WizardResultProducer;

@SuppressWarnings("unchecked")
public class ScoreUploadWizard {
	public static Wizard createWizard(GhMyScoresTreeTableModel model) {
		final ScoreUploadWizard me = new ScoreUploadWizard();
		
		return WizardPage.createWizard("Upload scores to ScoreHero",
			new WizardPage[] {
				new VerifyScoresPage(model),
				new UploadScoresPage()
			},
			me.resultProducer);
	}

	private ScoreUploadWizard() {}
	
	private WizardResultProducer resultProducer = new WizardResultProducer() {
		@Override
		public boolean cancel(Map settings) {
			return true;
//			return JOptionPane.YES_OPTION ==
//				JOptionPane.showConfirmDialog(null,
//					"Are you sure you want to cancel?", "Question",
//					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		}
	
		@Override
		public Object finish(Map wizardData) throws WizardException {
//			System.out.println("Keys: ");
//			
//			for (Object key : wizardData.keySet()) {
//				System.out.println("  " + key + ": " + wizardData.get(key).getClass().getName());
//			}
			
			return deferredResult;
		}
	};
	
	private DeferredWizardResult deferredResult = new DeferredWizardResult() {
		@Override
		public void start(Map settings, ResultProgressHandle progress) {
			try {
				GhMyScoresTreeTableModel model =
					(GhMyScoresTreeTableModel) settings.get("treeTableData");
				
				List<String> resultStrings = new ArrayList<String>();
				
				int scoreCount = model.getScoreCount();
				int curIndex = -1;
				for (GhScore s : model.getScores()) {
					curIndex++;
					progress.setProgress(
						String.format("Uploading score %s of %s", curIndex + 1, scoreCount),
						curIndex, scoreCount);
					Thread.sleep(1500); // TODO actually upload
					resultStrings.add("Uploaded: " + s.getSong().getTitle() + " - " + s.getScore());
				}
				
				progress.finished(
					Summary.create(
//						"Uploaded " + scoreCount + " score(s) successfuly.", null));
						resultStrings.toArray(new String[0]), null));
			} catch (Exception e) {
				progress.failed(e.getMessage(), false);
			}
		}
	};
}

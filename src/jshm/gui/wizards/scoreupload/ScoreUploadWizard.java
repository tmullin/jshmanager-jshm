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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

//import javax.swing.JOptionPane;

import jshm.gh.GhScore;
import jshm.gui.ShLoginDialog;
import jshm.gui.datamodels.GhMyScoresTreeTableModel;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.JXLoginDialog;
import org.jdesktop.swingx.error.ErrorInfo;
import org.netbeans.spi.wizard.ResultProgressHandle;
import org.netbeans.spi.wizard.Summary;
import org.netbeans.spi.wizard.Wizard;
import org.netbeans.spi.wizard.DeferredWizardResult;
import org.netbeans.spi.wizard.WizardException;
import org.netbeans.spi.wizard.WizardPage;
import org.netbeans.spi.wizard.WizardPage.WizardResultProducer;

@SuppressWarnings("unchecked")
public class ScoreUploadWizard {
	static final Logger LOG = Logger.getLogger(ScoreUploadWizard.class.getName());
	
	public static Wizard createWizard(GhMyScoresTreeTableModel model) {
		final ScoreUploadWizard me = new ScoreUploadWizard();
		
		return WizardPage.createWizard("Upload scores to ScoreHero",
			new WizardPage[] {
				new VerifyScoresPage(model)
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
				if (!jshm.sh.Client.hasAuthCookies()) {
					JXLoginDialog login = new ShLoginDialog(null);					
					login.setLocationRelativeTo(null);
					login.setVisible(true);
				}
				
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
					
					jshm.sh.GhApi.submitGhScore(s);
					
					resultStrings.add("Uploaded: " + s.getSong().getTitle() + " - " + s.getScore());
				}
				
				progress.finished(
					Summary.create(
//						"Uploaded " + scoreCount + " score(s) successfuly.", null));
						resultStrings.toArray(new String[0]), null));
			} catch (Throwable e) {
				LOG.log(Level.WARNING, "Failed to upload new score", e);
				progress.failed("Failed to upload score: " + e.getMessage(), false);
				ErrorInfo ei = new ErrorInfo("Error", "Failed to upload new score", null, null, e, null, null);
				JXErrorPane.showDialog(null, ei);
				return;
			}
		}
	};
}

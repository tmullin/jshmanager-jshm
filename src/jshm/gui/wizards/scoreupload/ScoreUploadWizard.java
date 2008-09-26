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

import jshm.Score;
import jshm.gui.LoginDialog;
import jshm.gui.datamodels.GhMyScoresTreeTableModel;

import org.jdesktop.swingx.JXErrorPane;
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
		
		return WizardPage.createWizard("Upload Scores to ScoreHero",
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
//				System.out.println("  " + key + ": " + String.valueOf(wizardData.get(key)));
//			}
//			
//			return null;
			
			return deferredResult;
		}
	};
	
	private DeferredWizardResult deferredResult = new DeferredWizardResult() {
		@Override
		public void start(Map settings, ResultProgressHandle progress) {
			try {
				if (!jshm.sh.Client.hasAuthCookies()) {
					LoginDialog.showDialog();
				}
				
				boolean uploadUnknown = false;
				
				try {
					uploadUnknown = (Boolean) settings.get("uploadUnknown");
				} catch (Exception e) {}
				
				GhMyScoresTreeTableModel model =
					(GhMyScoresTreeTableModel) settings.get("treeTableData");
				
				int uploaded = 0, possiblyUploaded = 0, notUploaded = 0;
				List<String> resultStrings = new ArrayList<String>();
				
				if (uploadUnknown)
					resultStrings.add("Going to upload scores with unknown status");
				
				int scoreCount = model.getScoreCount();
				int curIndex = -1;
				for (Score s : model.getScores()) {
					String scoreStr = s.getSong().getTitle() + " - " + s.getScore();
					
					curIndex++;
					
					try {
						Thread.sleep(250);
						
						// try not to hammer SH too badly
						if (curIndex % 5 == 4) {
							LOG.fine("Sleeping so we don't spam SH");
							Thread.sleep(2000);
						}
					} catch (InterruptedException e) {}
					
					progress.setProgress(
						String.format("Uploading score %s of %s", curIndex + 1, scoreCount),
						curIndex, scoreCount);
					
					if (s.getStatus() == Score.Status.UNKNOWN && !uploadUnknown)
						continue;
					
					try {
						s.submit();
					} catch (Exception e) {
						notUploaded++;
						LOG.log(Level.WARNING, "Error uploading score " + s, e);
						resultStrings.add("Submit failed: " + scoreStr + ": " + e);
						continue;
					}
					
					if (s.getStatus() == Score.Status.UNKNOWN)
						possiblyUploaded++;
					else
						uploaded++;
					
					resultStrings.add(
						(s.getStatus() == Score.Status.UNKNOWN
						 ? "Possibly submitted" : "Submitted") +
						": " + scoreStr);
				}
				
				resultStrings.add(
					String.format("Submitted: %s, uncertain about: %s, failed: %s", uploaded, possiblyUploaded, notUploaded));
				
				progress.finished(
					Summary.create(
						resultStrings.toArray(new String[0]), null));
			} catch (Throwable e) {
				LOG.log(Level.WARNING, "Failed to upload score", e);
				progress.failed("Failed to upload score: " + e.getMessage(), false);
				ErrorInfo ei = new ErrorInfo("Error", "Failed to upload score", null, null, e, null, null);
				JXErrorPane.showDialog(null, ei);
				return;
			}
		}
	};
}

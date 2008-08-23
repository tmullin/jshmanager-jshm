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
package jshm.gui.wizards.scoredownload;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import jshm.Difficulty;
import jshm.dataupdaters.GhScoreUpdater;
import jshm.gh.GhGame;
import jshm.gui.GUI;
import jshm.gui.ShLoginDialog;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.JXLoginDialog;
import org.jdesktop.swingx.error.ErrorInfo;
import org.netbeans.spi.wizard.DeferredWizardResult;
import org.netbeans.spi.wizard.ResultProgressHandle;
import org.netbeans.spi.wizard.Wizard;
import org.netbeans.spi.wizard.WizardException;
import org.netbeans.spi.wizard.WizardPage;
import org.netbeans.spi.wizard.WizardPage.WizardResultProducer;

@SuppressWarnings("unchecked")
public class ScoreDownloadWizard {
	static final Logger LOG = Logger.getLogger(ScoreDownloadWizard.class.getName());
	
	public static Wizard createWizard(final GUI gui, final GhGame game, final Difficulty difficulty) {
		final ScoreDownloadWizard me = new ScoreDownloadWizard(gui, game, difficulty);
		
		return WizardPage.createWizard("Download scores from ScoreHero",
			new WizardPage[] {
				new DownloadModePage()
			},
			me.resultProducer);
	}

	final GUI gui;
	final GhGame game;
	final Difficulty difficulty;
	
	private ScoreDownloadWizard(final GUI gui, final GhGame game, final Difficulty difficulty) {
		this.gui = gui;
		this.game = game;
		this.difficulty = difficulty;
	}
	
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
					JXLoginDialog login = new ShLoginDialog(null);					
					login.setLocationRelativeTo(null);
					login.setVisible(true);
				}

				boolean scrapeAll = false;
				
				try {
					scrapeAll = (Boolean) settings.get("all");
				} catch (Exception e) {}
				
				GhScoreUpdater.update(progress, scrapeAll, game, difficulty);
				gui.myScoresMenuItemActionPerformed(null, game, difficulty);
				progress.finished(null);
			} catch (Throwable e) {
				LOG.log(Level.WARNING, "Failed to download scores", e);
				progress.failed("Failed to download scores: " + e.getMessage(), false);
				ErrorInfo ei = new ErrorInfo("Error", "Failed to download scores", null, null, e, null, null);
				JXErrorPane.showDialog(null, ei);
				return;
			}
		}
	};
}

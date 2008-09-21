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

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import jshm.*;
import jshm.dataupdaters.GhScoreUpdater;
import jshm.dataupdaters.RbScoreUpdater;
import jshm.gh.GhGame;
import jshm.gui.GUI;
import jshm.gui.LoginDialog;
//import jshm.gui.ProgressDialog;
import jshm.rb.RbGame;
import jshm.rb.RbSong;

import org.jdesktop.swingx.JXErrorPane;
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
	
	public static Wizard createWizard(final GUI gui, final Game game, final Instrument.Group group, final Difficulty difficulty) {
		final ScoreDownloadWizard me = new ScoreDownloadWizard(gui, game, group, difficulty);
		
		return WizardPage.createWizard("Download Scores from ScoreHero",
			new WizardPage[] {
				new DownloadModePage(game)
			},
			me.resultProducer);
	}

	final GUI gui;
	final Game game;
	final Instrument.Group group;
	final Difficulty difficulty;
	
	private ScoreDownloadWizard(final GUI gui, final Game game, final Instrument.Group group, final Difficulty difficulty) {
		this.gui = gui;
		this.game = game;
		this.group = group;
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
					LOG.finer("Showing login dialog to get auth cookies");
					LoginDialog.showDialog();
					LOG.finest("Returned from login dialog");
				}

				boolean scrapeAll = false;
				
				try {
					scrapeAll = (Boolean) settings.get("all");
				} catch (Exception e) {}
				
				if (game instanceof GhGame) {
					GhGame ggame = (GhGame) game;
					// TODO change to a select count(*) for efficiency
					List<?> songs = jshm.gh.GhSong.getSongs(ggame, difficulty);
					
					if (songs.size() == 0) {
						// need to load song data as well
						LOG.fine("Downloading song data first");
						progress.setBusy("Downloading song data");
						jshm.dataupdaters.GhSongUpdater.update(ggame, difficulty);
					}
					
					GhScoreUpdater.update(progress, scrapeAll, ggame, difficulty);
					gui.myScoresMenuItemActionPerformed(null, ggame, Instrument.Group.GUITAR, difficulty);
				} else if (game instanceof RbGame) {
					RbGame rgame = (RbGame) game;
					// TODO change to a select count(*) for efficiency
					List<?> songs = RbSong.getSongs(rgame, group);
					
					if (songs.size() == 0) {
						// need to load song data as well
						LOG.fine("Downloading song data first");
						progress.setBusy("Downloading song data");
//						ProgressDialog progDialog = new ProgressDialog();
						jshm.dataupdaters.RbSongUpdater.update(progress, rgame.title);
//						progDialog.dispose();
					}
					
					RbScoreUpdater.update(progress, scrapeAll, rgame, group, difficulty);
					gui.myScoresMenuItemActionPerformed(null, rgame, group, difficulty);
				} else {
					assert false: "game is not a GhGame or RbGame";
				}
				
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

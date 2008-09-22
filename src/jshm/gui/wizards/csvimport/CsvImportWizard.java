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
package jshm.gui.wizards.csvimport;

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
public class CsvImportWizard {
	static final Logger LOG = Logger.getLogger(CsvImportWizard.class.getName());
	
	public static Wizard createWizard(final GUI gui, final Game game, final Instrument.Group group, final Difficulty difficulty) {
		final CsvImportWizard me = new CsvImportWizard(gui, game, group, difficulty);
		
		return WizardPage.createWizard("Import Scores from CSV File",
			new WizardPage[] {
				new OptionsPage(game, group, difficulty)
			},
			me.resultProducer);
	}

	final GUI gui;
	final Game game;
	final Instrument.Group group;
	final Difficulty difficulty;
	
	private CsvImportWizard(final GUI gui, final Game game, final Instrument.Group group, final Difficulty difficulty) {
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
			System.out.println("Keys: ");
			
			for (Object key : wizardData.keySet()) {
				System.out.println("  " + key + ": " + wizardData.get(key).getClass().getName());
			}
			
			return null;
//			return deferredResult;
		}
	};
	
	private DeferredWizardResult deferredResult = new DeferredWizardResult() {
		@Override
		public void start(Map settings, ResultProgressHandle progress) {
			try {

				boolean scrapeAll = false;
				
				try {
					scrapeAll = (Boolean) settings.get("all");
				} catch (Exception e) {}

				Object platform   = settings.get("platforms");
				Object diff       = settings.get("difficulties");
				Object instrument = settings.get("instruments");
				
				Object[] platforms   = null;
				Object[] diffs       = null;
				Object[] instruments = null;
				
				if (platform instanceof Object[]) {
					platforms = (Object[]) platform;
				} else {
					platforms = new Object[] {platform};
				}
				
				if (diff instanceof Object[]) {
					diffs = (Object[]) diff;
				} else {
					diffs = new Object[] {diff};
				}
				
				if (instrument instanceof Object[]) {
					instruments = (Object[]) instrument;
				} else {
					instruments = new Object[] {instrument};
				}
				
				
				if (game instanceof GhGame) {
					for (Object platformObj : platforms) {
						Platform p = (Platform) platformObj;
						GhGame ggame = (GhGame) Game.getByTitleAndPlatform(game.title, p);
						
						for (Object diffObj : diffs) {
							Difficulty d = (Difficulty) diffObj;
							
							// TODO change to a select count(*) for efficiency
							List<?> songs = jshm.gh.GhSong.getSongs(ggame, d);
							
							if (songs.size() == 0) {
								// need to load song data as well
								LOG.fine("Downloading song data first");
								progress.setBusy("Downloading song data");
								jshm.dataupdaters.GhSongUpdater.update(ggame, d);
							}
							
							GhScoreUpdater.update(progress, scrapeAll, ggame, d);
						}
					}
						
					gui.myScoresMenuItemActionPerformed(null, (GhGame) game, Instrument.Group.GUITAR, difficulty);
				} else if (game instanceof RbGame) {
					for (Object platformObj : platforms) {
						Platform p = (Platform) platformObj;
						RbGame rgame = (RbGame) Game.getByTitleAndPlatform(game.title, p);
						
						for (Object diffObj : diffs) {
							Difficulty d = (Difficulty) diffObj;
					
							for (Object instrumentObj : instruments) {
								Instrument.Group g = (Instrument.Group) instrumentObj;
							
								// TODO change to a select count(*) for efficiency
								List<?> songs = RbSong.getSongs(rgame, g);
								
								if (songs.size() == 0) {
									// need to load song data as well
									LOG.fine("Downloading song data first");
									progress.setBusy("Downloading song data");
			//						ProgressDialog progDialog = new ProgressDialog();
									jshm.dataupdaters.RbSongUpdater.update(progress, rgame.title);
			//						progDialog.dispose();
								}
							
								RbScoreUpdater.update(progress, scrapeAll, rgame, g, d);
							}
						}
					}
					
					gui.myScoresMenuItemActionPerformed(null, (RbGame) game, group, difficulty);
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

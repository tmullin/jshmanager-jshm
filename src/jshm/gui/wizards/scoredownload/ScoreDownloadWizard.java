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

import javax.swing.JOptionPane;

import jshm.*;
import jshm.dataupdaters.GhScoreUpdater;
import jshm.dataupdaters.RbScoreUpdater;
import jshm.dataupdaters.WtScoreUpdater;
import jshm.gh.GhGame;
import jshm.gui.GUI;
import jshm.gui.LoginDialog;
//import jshm.gui.ProgressDialog;
import jshm.rb.RbGame;
import jshm.rb.RbSong;
import jshm.wt.WtGame;
import jshm.wt.WtSong;

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
				new WhatToDownloadPage(game, group, difficulty),
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
						
					gui.myScoresMenuItemActionPerformed(null, game, Instrument.Group.GUITAR, difficulty);
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
									
									try {
										jshm.dataupdaters.RbSongUpdater.updateViaXml(progress, rgame.title);
										jshm.dataupdaters.RbSongUpdater.updateSongInfo(progress);
									} catch (Exception e1) {
										LOG.log(Level.WARNING, "Failed to download song data via XML", e1);
										
										int result = JOptionPane.showConfirmDialog(null,
											"Failed to download song data via the quicker way.\nDo you want to download it the old, slow way?", "Download Song Data", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
										
										if (JOptionPane.YES_OPTION == result) {
											jshm.dataupdaters.RbSongUpdater.updateViaScraping(progress, rgame.title);
											jshm.dataupdaters.RbSongUpdater.updateSongInfo(progress);
										}
									}
									
			//						progDialog.dispose();
								}
							
								RbScoreUpdater.update(progress, scrapeAll, rgame, g, d);
							}
						}
					}
					
					gui.myScoresMenuItemActionPerformed(null, game, group, difficulty);
				} else if (game instanceof WtGame) {
					for (Object platformObj : platforms) {
						Platform p = (Platform) platformObj;
						WtGame wgame = (WtGame) Game.getByTitleAndPlatform(game.title, p);
						
						for (Object diffObj : diffs) {
							Difficulty d = (Difficulty) diffObj;
					
							for (Object instrumentObj : instruments) {
								Instrument.Group g = (Instrument.Group) instrumentObj;
							
								// TODO change to a select count(*) for efficiency
								List<?> songs = WtSong.getSongs(wgame, g, d);
								
								if (songs.size() == 0) {
									// need to load song data as well
									LOG.fine("Downloading song data first");
									progress.setBusy("Downloading song data");
			//						ProgressDialog progDialog = new ProgressDialog();
									
//									try {
//										jshm.dataupdaters.WtSongUpdater.updateViaXml(progress, rgame.title);
//										jshm.dataupdaters.WtSongUpdater.updateSongInfo(progress);
//									} catch (Exception e1) {
//										LOG.log(Level.WARNING, "Failed to download song data via XML", e1);
//										
//										int result = JOptionPane.showConfirmDialog(null,
//											"Failed to download song data via the quicker way.\nDo you want to download it the old, slow way?", "Download Song Data", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
//										
//										if (JOptionPane.YES_OPTION == result) {
											jshm.dataupdaters.WtSongUpdater.updateViaScraping(progress, wgame.title);
//											jshm.dataupdaters.WtSongUpdater.updateSongInfo(progress);
//										}
//									}
									
			//						progDialog.dispose();
								}
							
								WtScoreUpdater.update(progress, scrapeAll, wgame, g, d);
							}
						}
					}
					
					gui.myScoresMenuItemActionPerformed(null, game, group, difficulty);
				} else {
					assert false: "unimplemented game subclass";
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

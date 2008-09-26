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

import static jshm.hibernate.HibernateUtil.getCurrentSession;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import jshm.*;
import jshm.csv.*;
import jshm.gui.GUI;
//import jshm.gui.ProgressDialog;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Example;
import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;
import org.netbeans.spi.wizard.DeferredWizardResult;
import org.netbeans.spi.wizard.ResultProgressHandle;
import org.netbeans.spi.wizard.Summary;
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
				new OptionsPage(game, group, difficulty),
				new ColumnsPage(game)
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
//			System.out.println("Keys: ");
//			
//			for (Object key : wizardData.keySet()) {
//				System.out.println("  " + key + ": " + wizardData.get(key));
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
				progress.setBusy("Parsing CSV file...");
				
				File f = new File((String) settings.get("file"));
				boolean inferColumns = true;
				
				try {
					inferColumns = (Boolean) settings.get("inferColumns");
				} catch (Exception e) {}
				
				
				boolean importDuplicates = false;
				
				try {
					importDuplicates = (Boolean) settings.get("duplicates");
				} catch (Exception e) {}
				
					
				CsvColumn[] columns = null;
				
				if (!inferColumns) {
					Object[] columnObjs = (Object[]) settings.get("columns");
					
					columns = new CsvColumn[columnObjs.length];
					
					for (int i = 0; i < columnObjs.length; i++)
						columns[i] = (CsvColumn) columnObjs[i];
				}
				
				List<String> summary = new ArrayList<String>();
				List<Score> scores = CsvParser.parse(summary, f, columns, game, group, difficulty);
				
				summary.add("Parsed " + scores.size() + " scores");
				
				
				progress.setBusy("Inserting into DB...");
				int existingScores = 0, insertedScores = 0;
				final int totalScores = scores.size();
				
				Session session = null;
				Transaction tx = null;
				
				try {
					session = getCurrentSession();
				    tx = session.beginTransaction();
				    
					for (int i = 0; i < totalScores; i++) {
						Score s = scores.get(i);
						
					    Example ex = Example.create(s)
					    	.excludeProperty("comment")
					    	.excludeProperty("rating")
					    	.excludeProperty("calculatedRating")
					    	.excludeProperty("creationDate")
					    	.excludeProperty("submissionDate")
					    	.excludeProperty("imageUrl")
					    	.excludeProperty("videoUrl");
					    
					    List result =
						    session.createCriteria(s.getClass())
						    	.add(ex).list();
					    
					    String scoreStr =
					    	String.format("%s %s \"%s\" - %s", s.getDifficulty().toShortString(), s.getGroup(), s.getSong().getTitle(), s.getScore());
					    
					    if (result.size() == 0 || importDuplicates) {
				    		LOG.info("Inserting score: " + s);
				    		summary.add("Added score: " + scoreStr);
				    		session.save(s);
				    		insertedScores++;
					    } else {
				    		LOG.fine("Skipping existing score: " + s);
				    		existingScores++;
					    }
					}
					
					tx.commit();
				} catch (Exception e) {
					if (null != tx) tx.rollback();
					LOG.throwing("CsvImportWizard", "start", e);
					throw e;
				} finally {
					if (null != session && session.isOpen())
						session.close();
				}
				
				summary.add(
					String.format("Done. Added %s scores, skipped %s duplicate scores", insertedScores, existingScores));
				
				progress.finished(
					Summary.create(
						summary.toArray(new String[0]), null));
			} catch (Throwable e) {
				LOG.log(Level.WARNING, "Failed to parse CSV file", e);
				progress.failed("Failed to parse CSV file: " + e.getMessage(), false);
				ErrorInfo ei = new ErrorInfo("Error", "Failed to parse CSV file", null, null, e, null, null);
				JXErrorPane.showDialog(null, ei);
				return;
			}
		}
	};
}

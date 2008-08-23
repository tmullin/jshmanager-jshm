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
package jshm.dataupdaters;

import java.util.*;
import java.util.logging.*;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Example;
import org.netbeans.spi.wizard.ResultProgressHandle;

import jshm.*;
import static jshm.hibernate.HibernateUtil.getCurrentSession;
import jshm.gh.*;

/**
 * This class scrapes song data and then inserts or updates
 * the database as needed.
 * @author Tim Mullin
 *
 */
public class GhScoreUpdater {
	static final Logger LOG = Logger.getLogger(GhScoreUpdater.class.getName());
	
	public static void update(final boolean scrapeAll, final GhGame game, final Difficulty difficulty) throws Exception {
		update(null, scrapeAll, game, difficulty);
	}
	
	public static void update(final ResultProgressHandle progress, final boolean scrapeAll, final GhGame game, final Difficulty difficulty) throws Exception {
		List<GhScore> scrapedScores =
			scrapeAll
			? jshm.sh.scraper.GhScoreScraper.scrapeAll(progress, game, difficulty)
			: jshm.sh.scraper.GhScoreScraper.scrapeLatest(progress, game, difficulty, null);
		
		for (GhScore score : scrapedScores) {
			Session session = null;
			Transaction tx = null;
			
			try {
				session = getCurrentSession();
			    tx = session.beginTransaction();
			    
			    Example ex = Example.create(score)
			    	.excludeProperty("comment")
			    	.excludeProperty("calculatedRating")
			    	.excludeProperty("rating")
			    	.excludeProperty("creationDate")
			    	.excludeProperty("submissionDate");
			    
			    GhScore result =
			    	(GhScore)
			    	session.createCriteria(GhScore.class).add(ex)
			    		.uniqueResult();
			    
		    	session = getCurrentSession();
			    session.beginTransaction();
			    
			    if (null == result) {
			    	// new insert
			    	LOG.info("Inserting score: " + score);
				    session.save(score);
			    } else {
			    	LOG.fine("Score already exists: " + result);
			    }

			    tx.commit();
			} catch (Exception e) {
				if (null != tx) tx.rollback();
				LOG.throwing("GhScoreUpdater", "update", e);
				throw e;
			} finally {
				if (session.isOpen())
					session.close();
			}
		}
	}
}

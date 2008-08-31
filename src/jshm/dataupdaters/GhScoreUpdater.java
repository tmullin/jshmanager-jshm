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
	
	@SuppressWarnings("unchecked")
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
			    
			    // TODO fix submission date comparison to prevent
			    // getting more than 1 result back
			    Example ex = Example.create(score)
			    	.excludeProperty("comment")
			    	.excludeProperty("calculatedRating")
			    	.excludeProperty("rating")
			    	.excludeProperty("creationDate")
			    	.excludeProperty("submissionDate");
			    
			    List<GhScore> result =
			    	(List<GhScore>)
			    	session.createCriteria(GhScore.class).add(ex)
			    		.list();
			    
		    	session = getCurrentSession();
			    session.beginTransaction();
			    
			    switch (result.size()) {
			    	case 0:
				    	// new insert
				    	LOG.info("Inserting score: " + score);
					    session.save(score);
					    break;
			    		
			    	default:
			    		LOG.warning("Found more than 1 existing score");
			    		LOG.warning("  Scraped: " + score);
			    		for (GhScore s : result)
			    			LOG.warning("    In DB: " + s);
//			    		throw new IllegalStateException("found more than 1 existing score, found " + result.size());
			    		
			    	case 1:
			    		LOG.fine("Score already exists: " + result.get(0));
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

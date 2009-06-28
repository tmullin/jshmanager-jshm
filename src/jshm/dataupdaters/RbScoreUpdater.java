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
import org.hibernate.criterion.Expression;
//import org.hibernate.criterion.Example;
import org.netbeans.spi.wizard.ResultProgressHandle;

import static jshm.hibernate.HibernateUtil.getCurrentSession;
import jshm.*;
import jshm.rb.*;

/**
 * This class scrapes song data and then inserts or updates
 * the database as needed.
 * @author Tim Mullin
 *
 */
public class RbScoreUpdater {
	static final Logger LOG = Logger.getLogger(RbScoreUpdater.class.getName());
	
	public static void update(final boolean scrapeAll, final RbGame game, final Instrument.Group group, final Difficulty difficulty) throws Exception {
		update(null, scrapeAll, game, group, difficulty);
	}
	
	@SuppressWarnings("unchecked")
	public static void update(final ResultProgressHandle progress, final boolean scrapeAll, final RbGame game, final Instrument.Group group, final Difficulty difficulty) throws Exception {
		List<RbScore> scrapedScores =
			scrapeAll
			? jshm.sh.scraper.RbScoreScraper.scrapeAll(progress, game, group, difficulty)
			: jshm.sh.scraper.RbScoreScraper.scrapeLatest(progress, game, group, difficulty, null);
		
		Session session = null;
		Transaction tx = null;
		
		try {
			session = getCurrentSession();
		    tx = session.beginTransaction();
			
			for (RbScore score : scrapedScores) {			    
			    // TODO fix submission date comparison to prevent
			    // getting more than 1 result back
//			    Example ex = Example.create(score)
//			    	.excludeProperty("comment")
//			    	.excludeProperty("rating")
//			    	.excludeProperty("creationDate")
//			    	.excludeProperty("submissionDate")
//			    	.excludeProperty("imageUrl")
//			    	.excludeProperty("videoUrl");
			    
			    List<RbScore> result =
			    	(List<RbScore>)
			    	session.createCriteria(RbScore.class)//.add(ex)
			    	.add(Expression.eq("score", score.getScore()))
			    	.add(Expression.eq("song", score.getSong()))
			    		.list();
			    
			    switch (result.size()) {
			    	case 0:
				    	// new insert
				    	LOG.info("Inserting score: " + score);
					    session.save(score);
					    break;
			    		
					// FIXME 2 scores always seem to get returned
			    	default:
			    		LOG.warning("Found more than 1 existing score");
			    		LOG.warning("  Scraped: " + score);
			    		for (RbScore s : result)
			    			LOG.warning("    In DB: " + s);
	//			    		throw new IllegalStateException("found more than 1 existing score, found " + result.size());
			    		
			    	case 1:
			    		LOG.fine("Score already exists: " + result.get(0));
			    }
			}
			
		    tx.commit();
		} catch (Exception e) {
			if (null != tx) tx.rollback();
			LOG.throwing("RbScoreUpdater", "update", e);
			throw e;
		} finally {
			if (null != session && session.isOpen())
				session.close();
		}
	}
}

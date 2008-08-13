package jshm.dataupdaters;

import java.util.*;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Example;

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
	public static void update(final GhGame game, final Difficulty difficulty) throws Exception {
		List<GhScore> scrapedScores =
			jshm.sh.scraper.GhScoreScraper.scrapeLatest(game, difficulty);
		
		for (GhScore score : scrapedScores) {
			Session session = null;
			Transaction tx = null;
			
			try {
				session = getCurrentSession();
			    tx = session.beginTransaction();
			    
			    Example ex = Example.create(score);
			    GhScore result =
			    	(GhScore)
			    	session.createCriteria(GhScore.class).add(ex)
			    		.uniqueResult();
			    
		    	session = getCurrentSession();
			    session.beginTransaction();
			    
			    if (null == result) {
			    	// new insert
			    	System.out.println("Inserting: " + score);
				    session.save(score);
			    } else {
			    	System.out.println("Score already exists: " + result);
			    }

			    tx.commit();
			} catch (Exception e) {
				if (null != tx) tx.rollback();
				throw e;
			} finally {
				if (session.isOpen())
					session.close();
			}
		}
	}
}

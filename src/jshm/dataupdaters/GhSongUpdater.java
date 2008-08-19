package jshm.dataupdaters;

import java.util.*;
import java.util.logging.Logger;

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
public class GhSongUpdater {
	static final Logger LOG = Logger.getLogger(GhSongUpdater.class.getName());
	
	public static void update(final GhGame game, final Difficulty difficulty) throws Exception {
		List<GhSong> scrapedSongs =
			jshm.sh.scraper.GhSongScraper.scrape(game, difficulty);
		
		for (GhSong song : scrapedSongs) {
			Session session = null;
			Transaction tx = null;
			
			try {
				session = getCurrentSession();
			    tx = session.beginTransaction();
			    
			    Example ex = Example.create(song);
			    GhSong result =
			    	(GhSong)
			    	session.createCriteria(GhSong.class).add(ex)
			    		.uniqueResult();
			    
		    	session = getCurrentSession();
			    session.beginTransaction();
			    
			    if (null == result) {
			    	// new insert
			    	LOG.info("Inserting song: " + song);
				    session.save(song);
			    } else {
			    	// update existing
			    	if (result.update(song)) {
				    	LOG.info("Updating song: " + result);
				    	session.update(result);
			    	} else {
			    		LOG.finest("No changes to song: " + result);
			    	}
			    }

			    tx.commit();
			} catch (Exception e) {
				if (null != tx) tx.rollback();
				LOG.throwing("GhSongUpdater", "update", e);
				throw e;
			} finally {
				if (session.isOpen())
					session.close();
			}
		}
	}
}

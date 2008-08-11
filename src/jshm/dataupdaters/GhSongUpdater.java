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
public class GhSongUpdater {
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
			    	System.out.println("Inserting: " + song);
				    session.save(song);
			    } else {
			    	// update existing
			    	if (result.update(song)) {
				    	System.out.println("Updating: " + result);
				    	session.update(result);
			    	} else {
			    		System.out.println("No changes: " + result);
			    	}
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

package jshm.dataupdaters;

import static jshm.hibernate.HibernateUtil.getCurrentSession;

import java.util.List;
import java.util.logging.Logger;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Example;

import jshm.*;
import jshm.rb.*;
import jshm.sh.scraper.RbSongScraper;

public class RbSongUpdater {
	static final Logger LOG = Logger.getLogger(RbSongUpdater.class.getName());
	
	public static void update(final GameTitle game) throws Exception {
		if (!(game instanceof RbGameTitle))
			throw new IllegalArgumentException("game must be an RbGameTitle");
		
		// first get the songs themselves, need to do for each platform
		for (Game g : Game.getByTitle(game)) {
			List<RbSong> songs = RbSongScraper.scrape((RbGame) g);
			
			LOG.finer("scraped " + songs.size() + " songs for " + g);
			
			for (RbSong song : songs) {
				Session session = null;
				Transaction tx = null;
				
				try {
					session = getCurrentSession();
				    tx = session.beginTransaction();
				    
				    Example ex = Example.create(song)
				    	.excludeProperty("gameStrs");
				    RbSong result =
				    	(RbSong)
				    	session.createCriteria(RbSong.class).add(ex)
				    		.uniqueResult();
//				    tx.commit();
				    
//			    	session = getCurrentSession();
				    tx = session.beginTransaction();
				    
				    if (null == result) {
				    	// new insert
				    	LOG.info("Inserting song: " + song);
					    session.save(song);
				    } else {
				    	LOG.finest("found song: " + result);
				    	// update existing
				    	if (result.update(song)) {
					    	LOG.info("Updating song to: " + result);
					    	session.update(result);
				    	} else {
				    		LOG.finest("No changes to song: " + result);
				    	}
				    }

				    session.flush();
				    tx.commit();
				} catch (Exception e) {
					if (null != tx) tx.rollback();
					LOG.throwing("RbSongUpdater", "update", e);
					throw e;
				} finally {
					if (null != session && session.isOpen())
						session.close();
				}
			}
		}
		
		// for now we kind of have to do each platform/group combo.... 20+ requests ugh
		// at least it seems to be working
		for (Game g : Game.getByTitle(game)) {
			List<SongOrder> orders = RbSongScraper.scrapeOrders((RbGame) g);
			
			LOG.finer("scraped " + orders.size() + " song orderings for " + g);
			
			for (SongOrder order : orders) {
//				System.out.println(order);
//				if (true) continue;
				
				Session session = null;
				Transaction tx = null;
				
				try {
					session = getCurrentSession();
				    tx = session.beginTransaction();
				    
				    Example ex = Example.create(order)
				    	.excludeProperty("tier")
				    	.excludeProperty("order");
				    SongOrder result =
				    	(SongOrder)
				    	session.createCriteria(SongOrder.class)
				    	.add(ex)
				    		.createCriteria("song")
				    			.add(Example.create(order.getSong()))
				    	.uniqueResult();
//				    tx.commit();
				    
//			    	session = getCurrentSession();
				    tx = session.beginTransaction();
				    
				    if (null == result) {
				    	// new insert
				    	LOG.info("Inserting song order: " + order);
					    session.save(order);
				    } else {
				    	LOG.finest("Found song order: " + result);
				    	// update existing
				    	if (result.update(order)) {
					    	LOG.info("Updating song order to: " + result);
					    	session.update(result);
				    	} else {
				    		LOG.finest("No changes to song order: " + result);
				    	}
				    }

				    session.flush();
				    tx.commit();
				} catch (Exception e) {
					if (null != tx) tx.rollback();
					LOG.throwing("RbSongUpdater", "update", e);
					throw e;
				} finally {
					if (null != session && session.isOpen())
						session.close();
				}
			}
			
//			break;
		}
	}
}

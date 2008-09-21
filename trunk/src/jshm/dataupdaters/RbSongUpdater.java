package jshm.dataupdaters;

import static jshm.hibernate.HibernateUtil.openSession;

import java.util.List;
import java.util.logging.Logger;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Example;
import org.netbeans.spi.wizard.ResultProgressHandle;

import jshm.*;
import jshm.rb.*;
import jshm.sh.scraper.RbSongScraper;

public class RbSongUpdater {
	static final Logger LOG = Logger.getLogger(RbSongUpdater.class.getName());
	
	public static void update(final GameTitle game) throws Exception {
		update(null, game);
	}
	
	public static void update(final ResultProgressHandle progress, final GameTitle game) throws Exception {
		if (!(game instanceof RbGameTitle))
			throw new IllegalArgumentException("game must be an RbGameTitle");
		
		Session session = null;
		Transaction tx = null;
		
		try {
			session = openSession();
		    tx = session.beginTransaction();
		
		    // first get the songs themselves, need to do for each platform
			for (Game g : Game.getByTitle(game)) {
				if (null != progress)
					progress.setBusy("Downloading song list for " + g);
				
				List<RbSong> songs = RbSongScraper.scrape((RbGame) g);
				
				LOG.finer("scraped " + songs.size() + " songs for " + g);
				
				int i = 0, total = songs.size(); 
				for (RbSong song : songs) {
					if (null != progress)
						progress.setProgress(
							String.format("Processing song %s of %s", i + 1, total), i, total);
							
				    Example ex = Example.create(song)
				    	.excludeProperty("gameStrs");
				    RbSong result =
				    	(RbSong)
				    	session.createCriteria(RbSong.class).add(ex)
				    		.uniqueResult();
				    
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
				    
				    i++;
				}
			}
			
		    
			// for now we kind of have to do each platform/group combo.... 20+ requests ugh
			// at least it seems to be working
			for (Game g : Game.getByTitle(game)) {
				if (null != progress)
					progress.setBusy("Downloading song order lists for " + g);
				
				List<SongOrder> orders = RbSongScraper.scrapeOrders(progress, (RbGame) g);
				
				LOG.finer("scraped " + orders.size() + " song orderings for " + g);
				
				int i = 0, total = orders.size();
				for (SongOrder order : orders) {
					if (null != progress)
						progress.setProgress(
							"Processing song order lists...", i, total);
					
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
				    
				    i++;
				}
			}
			
		    tx.commit();
		} catch (HibernateException e) {
			if (null != tx && tx.isActive()) tx.rollback();
			LOG.throwing("RbSongUpdater", "update", e);
			throw e;
		} finally {
			if (null != session && session.isOpen())
				session.close();
		}
	}
}

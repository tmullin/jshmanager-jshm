package jshm.dataupdaters;

import static jshm.hibernate.HibernateUtil.openSession;

import java.util.List;
import java.util.logging.Logger;

import jshm.Game;
import jshm.GameTitle;
import jshm.SongOrder;
import jshm.wt.*;
import jshm.sh.scraper.WtSongScraper;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.netbeans.spi.wizard.ResultProgressHandle;

public class WtSongUpdater {
	static final Logger LOG = Logger.getLogger(WtSongUpdater.class.getName());
	
	public static void updateViaScraping(final GameTitle game) throws Exception {
		updateViaScraping(null, game);
	}
	
	public static void updateViaScraping(final ResultProgressHandle progress, final GameTitle game) throws Exception {
		if (!(game instanceof WtGameTitle))
			throw new IllegalArgumentException("game must be a WtGameTitle");
		
		Session session = null;
		Transaction tx = null;
		
		try {
			session = openSession();
		    tx = session.beginTransaction();
		
		    // first get the songs themselves, need to do for each platform
			for (Game g : Game.getByTitle(game)) {
				if (null != progress)
					progress.setBusy("Downloading song list for " + g);
				
				List<WtSong> songs = WtSongScraper.scrape((WtGame) g);
				
				LOG.finer("scraped " + songs.size() + " songs for " + g);
				
				int i = 0, total = songs.size(); 
				for (WtSong song : songs) {
					if (null != progress)
						progress.setProgress(
							String.format("Processing song %s of %s", i + 1, total), i, total);
				    
					// only care about the sh id and gameTitle
					WtSong result =
						(WtSong) session.createQuery(
							"FROM WtSong WHERE scoreHeroId=:shid AND gameTitle=:gameTtl")
							.setInteger("shid", song.getScoreHeroId())
							.setString("gameTtl", song.getGameTitle().title)
							.uniqueResult();
					
				    if (null == result) {
				    	// new insert
				    	LOG.info("Inserting song: " + song);
					    session.save(song);
				    } else {
				    	LOG.finest("found song: " + result);
				    	// update existing
				    	if (result.update(song, false)) {
					    	LOG.info("Updating song to: " + result);
					    	session.update(result);
				    	} else {
				    		LOG.finest("No changes to song: " + result);
				    	}
				    }
				    
				    i++;
				}
			}
			
		    
			// faster to delete them all and insert instead of checking
			// each one separately
			LOG.info("Deleting old song orders");
			int deletedOrderCount = session.createQuery(
				"delete SongOrder where gameTitle=:gameTitle")
				.setString("gameTitle", game.toString())
				.executeUpdate();
			
			LOG.finer("deleted " + deletedOrderCount + " old song orders");
			
			
			// for now we kind of have to do each platform/group combo.... 20+ requests ugh
			// at least it seems to be working
			for (Game g : Game.getByTitle(game)) {
				if (null != progress)
					progress.setBusy("Downloading song order lists for " + g);
				
				List<SongOrder> orders = WtSongScraper.scrapeOrders(progress, (WtGame) g);
				
				LOG.finer("scraped " + orders.size() + " song orderings for " + g);
				
				int i = 0, total = orders.size();
				for (SongOrder order : orders) {
					if (null != progress && i % 64 == 0)
						progress.setProgress(
							"Processing song order lists...", i, total);
					
			    	// always a new insert
			    	LOG.info("Inserting song order: " + order);
				    session.save(order);
				    
				    i++;
				}
			}
			
		    tx.commit();
		} catch (HibernateException e) {
			if (null != tx && tx.isActive()) tx.rollback();
			LOG.throwing("WtSongUpdater", "update", e);
			throw e;
		} finally {
			if (null != session && session.isOpen())
				session.close();
		}
	}
}

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
import jshm.xml.RbSongDataFetcher;

public class RbSongUpdater {
	static final Logger LOG = Logger.getLogger(RbSongUpdater.class.getName());
	
	public static void updateViaXml(final GameTitle game) throws Exception {
		updateViaXml(null, game);
	}
	
	public static void updateViaXml(final ResultProgressHandle progress, final GameTitle game) throws Exception {
		if (!(game instanceof RbGameTitle))
			throw new IllegalArgumentException("game must be an RbGameTitle");
		
		Session session = null;
		Transaction tx = null;
		
		try {
			session = openSession();
		    tx = session.beginTransaction();
		
		    // first get the songs themselves

			if (null != progress)
				progress.setBusy("Downloading song data for " + game);
			
			RbSongDataFetcher fetcher = new RbSongDataFetcher();
			fetcher.fetch((RbGameTitle) game);
			
			LOG.finer("xml updated at " + fetcher.updated);
			
			List<RbSong> songs = fetcher.songs;
			
			LOG.finer("xml had " + songs.size() + " songs for " + game);
			
			int i = 0, total = songs.size(); 
			for (RbSong song : songs) {
				if (null != progress)
					progress.setProgress(
						String.format("Processing song %s of %s", i + 1, total), i, total);
						
			    Example ex = Example.create(song)
			    	.excludeProperty("gameStrs")
			    	.excludeProperty("title");
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
			
		    
			// now get song orders
			List<SongOrder> orders = fetcher.orders;
			
			LOG.finer("xml had " + orders.size() + " song orderings for " + game);
			
			// faster to delete them all and insert instead of checking
			// each one separately
			LOG.info("Deleting old song orders");
			int deletedOrderCount = session.createQuery(
				"delete SongOrder where gameTitle=:gameTitle")
				.setString("gameTitle", game.toString())
				.executeUpdate();
			
			LOG.finer("deleted " + deletedOrderCount + " old song orders");
			
			i = 0; total = orders.size();
			for (SongOrder order : orders) {
				if (null != progress && i % 64 == 0)
					progress.setProgress(
						"Processing song order lists...", i, total);
				
//			    Example ex = Example.create(order)
//			    	.excludeProperty("tier")
//			    	.excludeProperty("order");
//			    SongOrder result =
//			    	(SongOrder)
//			    	session.createCriteria(SongOrder.class)
//			    	.add(ex)
//			    		.createCriteria("song")
//			    			.add(Example.create(order.getSong()))
//			    	.uniqueResult();
			    
//			    SongOrder result = (SongOrder) session.createQuery(String.format(
//			    	"from SongOrder as so where so.group='%s' and so.gameTitle='%s' and so.platform='%s' and so.song.scoreHeroId=%s",
//			    	order.getGroup(), order.getGameTitle(), order.getPlatform(), order.getSong().getScoreHeroId()
//			    )).uniqueResult();
			    
//			    if (null == result) {
					// we need to get an instance of the RbSong that's in the db,
					// not the detached one we used before from the xml
					order.setSong(
						RbSong.getByScoreHeroId(
							order.getSong().getScoreHeroId()));
					
			    	// new insert
			    	LOG.info("Inserting song order: " + order);
				    session.save(order);
//			    } else {
//			    	LOG.finest("Found song order: " + result);
//			    	// update existing
//			    	if (result.update(order)) {
//				    	LOG.info("Updating song order to: " + result);
//				    	session.update(result);
//			    	} else {
//			    		LOG.finest("No changes to song order: " + result);
//			    	}
//			    }
			    
			    i++;
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
	
	public static void updateViaScraping(final GameTitle game) throws Exception {
		updateViaScraping(null, game);
	}
	
	public static void updateViaScraping(final ResultProgressHandle progress, final GameTitle game) throws Exception {
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
				    	.excludeProperty("gameStrs")
				    	.excludeProperty("title");
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
				
				List<SongOrder> orders = RbSongScraper.scrapeOrders(progress, (RbGame) g);
				
				LOG.finer("scraped " + orders.size() + " song orderings for " + g);
				
				int i = 0, total = orders.size();
				for (SongOrder order : orders) {
					if (null != progress && i % 64 == 0)
						progress.setProgress(
							"Processing song order lists...", i, total);
					
//				    Example ex = Example.create(order)
//				    	.excludeProperty("tier")
//				    	.excludeProperty("order");
//				    SongOrder result =
//				    	(SongOrder)
//				    	session.createCriteria(SongOrder.class)
//				    	.add(ex)
//				    		.createCriteria("song")
//				    			.add(Example.create(order.getSong()))
//				    	.uniqueResult();
//				    
//				    if (null == result) {
				    	// new insert
				    	LOG.info("Inserting song order: " + order);
					    session.save(order);
//				    } else {
//				    	LOG.finest("Found song order: " + result);
//				    	// update existing
//				    	if (result.update(order)) {
//					    	LOG.info("Updating song order to: " + result);
//					    	session.update(result);
//				    	} else {
//				    		LOG.finest("No changes to song order: " + result);
//				    	}
//				    }
				    
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
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
import java.util.logging.Logger;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.netbeans.spi.wizard.ResultProgressHandle;
//import org.hibernate.criterion.Example;

import jshm.*;
import static jshm.hibernate.HibernateUtil.openSession;
import jshm.gh.*;
import jshm.xml.GhSongInfoFetcher;

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
		
		Session session = null;
		Transaction tx = null;
		
		try {
			session = openSession();
		    tx = session.beginTransaction();
		    
			for (GhSong song : scrapedSongs) {
				// gh songs have a unique scoreHeroId across all
				// platforms and difficulties, so we can
				// use a simpler query
				GhSong result =
					(GhSong)
					session.createQuery(
						"FROM GhSong WHERE scoreHeroId=:shid")
						.setInteger("shid", song.getScoreHeroId())
						.uniqueResult();
				
			    if (null == result) {
			    	// new insert
			    	LOG.info("Inserting song: " + song);
				    session.save(song);
			    } else {
			    	// update existing
			    	if (result.update(song)) {
				    	LOG.info("Updating song to: " + result);
				    	session.update(result);
			    	} else {
			    		LOG.finest("No changes to song: " + result);
			    	}
			    }
			}
		
	    tx.commit();
		} catch (Exception e) {
			if (null != tx) tx.rollback();
			LOG.throwing("GhSongUpdater", "update", e);
			throw e;
		} finally {
			if (null != session && session.isOpen())
				session.close();
		}
	}
	
	public static void updateSongInfo(GhGameTitle ttl) throws Exception {
		updateSongInfo(null, ttl);
	}
	
	@SuppressWarnings("unchecked")
	public static void updateSongInfo(final ResultProgressHandle progress, GhGameTitle ttl) throws Exception {
		if (null != progress)
			progress.setBusy("Downloading song meta data...");
		GhSongInfoFetcher fetcher = new GhSongInfoFetcher();
		fetcher.fetch(ttl);
		
		Session session = null;
		Transaction tx = null;
		
		try {
			session = openSession();
		    tx = session.beginTransaction();
		    
			int i = 0, total = fetcher.songMap.size(); 
						
			for (String key : fetcher.songMap.keySet()) {
				if (null != progress)
					progress.setProgress(
						String.format("Processing song %s of %s", i + 1, total), i, total);
				
				String upperTtl = key.toUpperCase();
				
				List<GhSong> result =
					(List<GhSong>) session.createQuery(
						"FROM GhSong WHERE game LIKE :gameTtl AND UPPER(title) LIKE :ttl")
					.setString("gameTtl", ttl.title + "_%")
					.setString("ttl", upperTtl)
					.list();
				
				LOG.finer("result.size() == " + result.size());
				
				for (GhSong s : result) {
					if (s.update(fetcher.songMap.get(key))) {
						LOG.info("Updating song to: " + s);
						session.update(s);
					} else {
						LOG.finer("No changes to song: " + s);
					}
				}
				
				if (i % 64 == 0) {
					session.flush();
					session.clear();
				}
				
				i++;
			}
			
		    tx.commit();
		} catch (HibernateException e) {
			if (null != tx && tx.isActive()) tx.rollback();
			LOG.throwing("GhSongUpdater", "updateSongInfo", e);
			throw e;
		} finally {
			if (null != session && session.isOpen())
				session.close();
		}
		
		ttl.initDynamicTiers();
	}
}

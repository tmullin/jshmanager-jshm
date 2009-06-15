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

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Example;

import jshm.*;
import static jshm.hibernate.HibernateUtil.openSession;
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
		
		Session session = null;
		Transaction tx = null;
		
		try {
			session = openSession();
		    tx = session.beginTransaction();
		    
			for (GhSong song : scrapedSongs) {
//			    Example ex = Example.create(song)
//			    	.excludeProperty("noteCount")
//			    	.excludeProperty("baseScore")
//			    	.excludeProperty("fourStarCutoff")
//			    	.excludeProperty("fiveStarCutoff")
//			    	.excludeProperty("sixStarCutoff")
//			    	.excludeProperty("sevenStarCutoff")
//			    	.excludeProperty("eightStarCutoff")
//			    	.excludeProperty("nineStarCutoff")
//			    	;
//			    GhSong result =
//			    	(GhSong)
//			    	session.createCriteria(GhSong.class).add(ex)
//			    		.uniqueResult();
			    
				// gh songs have a unique scoreHeroId across all
				// platforms and difficulties, unlike rb, so we can
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
}

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
package jshm.concepts;

import java.util.List;

import org.hibernate.Session;

import jshm.*;
import jshm.dataupdaters.*;
import jshm.gh.*;
import jshm.rb.*;
import jshm.hibernate.HibernateUtil;
import jshm.logging.Log;
import jshm.sh.scraper.*;

@SuppressWarnings({"unused"})
public class GhSongDbTest {
	public static void main(String[] args) throws Exception {
		Log.configTestLogging();
//		HibernateUtil.getCurrentSession();
//		System.exit(0);
		
		final RbGame game = RbGame.RB1_XBOX360;
//		final Difficulty difficulty = Difficulty.EXPERT;
		
		jshm.util.TestTimer.start();
//		GhSongUpdater.update(GhGame.GH3_XBOX360, Difficulty.EXPERT);
		RbSongUpdater.update(game.title);
		jshm.util.TestTimer.stop();
		
//		printSongs();
		
//		jshm.util.TestTimer.stop();
		
//		jshm.sh.Client.getAuthCookies("someuser", "somepass");
//		jshm.dataupdaters.GhScoreUpdater.update(false, game, difficulty);
//		
//		jshm.util.TestTimer.stop();
		
//		printScores();
		
//		jshm.util.TestTimer.stop();
		
//		HibernateUtil.shutdown();
	}
	
	static void printSongs() {
//	    Session session = HibernateUtil.getCurrentSession();
//	    session.beginTransaction();
//	    List<Song> result = (List<Song>) session.createQuery("from RbSong").list();
//	    session.getTransaction().commit();
//	    
//	    for (Song s : result)
//	    	System.out.println(s);
	    
		List<SongOrder> orders = RbSong.getSongs(RbGame.RB1_WII, Instrument.Group.GUITAR);
		
		for (SongOrder o : orders)
			System.out.println(o);
	}
	
	static void printScores() {
//	    Session session = HibernateUtil.getCurrentSession();
//	    session.beginTransaction();
//	    List<GhScore> result = (List<GhScore>) session.createQuery("from GhScore").list();
//	    session.getTransaction().commit();
//	    
//	    for (GhScore s : result)
//	    	System.out.println(s);
	}
}

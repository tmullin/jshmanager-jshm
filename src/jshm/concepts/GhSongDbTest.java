package jshm.concepts;

import java.util.List;

import org.hibernate.Session;

import jshm.Difficulty;
import jshm.Instrument;
import jshm.Part;
import jshm.Song;
import jshm.gh.GhGame;
import jshm.gh.GhScore;
import jshm.gh.GhSong;
import jshm.hibernate.HibernateUtil;
import jshm.sh.scraper.GhSongScraper;

@SuppressWarnings({"unused", "unchecked"})
public class GhSongDbTest {
	public static void main(String[] args) throws Exception {
		final GhGame game = GhGame.GH3_XBOX360;
		final Difficulty difficulty = Difficulty.EXPERT;
		
		jshm.util.TestTimer.start();
		
//		jshm.dataupdaters.GhSongUpdater.update(game, difficulty);
//		jshm.util.TestTimer.stop();
//		
//		printSongs();
//		
//		jshm.util.TestTimer.stop();
		
		jshm.sh.Client.getAuthCookies("someuser", "somepass");
		jshm.dataupdaters.GhScoreUpdater.update(game, difficulty);
		
		jshm.util.TestTimer.stop();
		
		printScores();
		
		jshm.util.TestTimer.stop();
	}
	
	static void printSongs() {
	    Session session = HibernateUtil.getCurrentSession();
	    session.beginTransaction();
	    List<GhSong> result = (List<GhSong>) session.createQuery("from GhSong").list();
	    session.getTransaction().commit();
	    
	    for (GhSong s : result)
	    	System.out.println(s);
	}
	
	static void printScores() {
	    Session session = HibernateUtil.getCurrentSession();
	    session.beginTransaction();
	    List<GhScore> result = (List<GhScore>) session.createQuery("from GhScore").list();
	    session.getTransaction().commit();
	    
	    for (GhScore s : result)
	    	System.out.println(s);
	}
}

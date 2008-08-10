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

public class GhSongDbTest {
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws Exception {
		List<GhSong> songs = 
			GhSongScraper.scrape(
				GhGame.GH3_XBOX360, Difficulty.EXPERT);
		
		storeSongs(songs);
		
//		printSongs();
		
		Part part = new Part();
		part.setInstrument(Instrument.GUITAR);
		part.setDifficulty(Difficulty.EXPERT);
		part.setHitPercent(1.0f);
		part.setStreak(551);
		
		GhScore score = new GhScore();
		score.setSong(songs.get(0));
		score.addPart(part);
		score.setCalculatedRating(7.2f);
		score.setDifficulty(Difficulty.EXPERT);
		score.setRating(5);
		score.setScore(210514);
		score.setStreak(551);
		
		System.out.println("new score: " + score);
		
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        session.save(score);
        session.getTransaction().commit();
        
        session = HibernateUtil.getSessionFactory().getCurrentSession();
	    session.beginTransaction();
	    List<GhScore> result = (List<GhScore>) session.createQuery("from GhScore").list();
	    session.getTransaction().commit();
	    
	    System.out.println("in db:");
	    for (GhScore s : result)
	    	System.out.println(s);
	    
//	    session = HibernateUtil.getSessionFactory().getCurrentSession();
//        session.beginTransaction();
//        session.delete(score);
//        session.getTransaction().commit();
	}
		
	static void storeSongs(final List<GhSong> songs) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();

        for (Song song : songs)
        	session.save(song);
        
        session.getTransaction().commit();
	}
	
	@SuppressWarnings("unchecked")
	static void printSongs() {
	    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
	    session.beginTransaction();
	    List<GhSong> result = (List<GhSong>) session.createQuery("from GhSong").list();
	    session.getTransaction().commit();
	    
	    for (Song s : result)
	    	System.out.println(s);
	}
}

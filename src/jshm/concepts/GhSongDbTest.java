package jshm.concepts;

import java.util.List;

import org.hibernate.Session;

import jshm.Difficulty;
import jshm.Song;
import jshm.hibernate.HibernateUtil;
import jshm.sh.gh.GhGame;
import jshm.sh.gh.GhSong;
import jshm.sh.gh.scraper.SongScraper;

public class GhSongDbTest {
	public static void main(String[] args) throws Exception {
		List<GhSong> songs = 
			SongScraper.scrape(
				GhGame.GH3_XBOX360, Difficulty.EXPERT);
		
		storeSongs(songs);
		
		printSongs();
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

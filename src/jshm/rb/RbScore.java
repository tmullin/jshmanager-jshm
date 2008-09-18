package jshm.rb;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Transient;
import javax.swing.ImageIcon;

import org.hibernate.criterion.Restrictions;

import jshm.Difficulty;
import jshm.Instrument;
import jshm.Score;
import jshm.SongOrder;

@Entity
public class RbScore extends Score {
	@SuppressWarnings("unchecked")
	public static List<RbScore> getScores(final RbGame game, final Instrument.Group group, final Difficulty difficulty) {
		org.hibernate.Session session = jshm.hibernate.HibernateUtil.getCurrentSession();
	    session.beginTransaction();
	    List<RbScore> result =
			(List<RbScore>)
			session.createQuery(
				String.format(
					"from RbScore where game='%s' and instrumentgroup='%s' and difficulty='%s' order by score DESC",
					game.toString(), group.toString(), difficulty.toString()))
				.list();
		
	    // set the SongOrder for each
	    for (RbScore s : result) {
	    	s.getSong().setSongOrder(
	    		(SongOrder)
	    		session.createCriteria(SongOrder.class)
	    			.add(Restrictions.eq("gameTitle", s.getGame().title))
	    			.add(Restrictions.eq("platform", s.getGame().platform))
	    			.add(Restrictions.eq("group", s.getGroup()))
	    			.add(Restrictions.eq("song", s.getSong()))
	    		.uniqueResult()
	    	);
	    }
	    
	    session.getTransaction().commit();
	    
		return result;
	}
	
	@Transient
	@Override
	public ImageIcon getRatingIcon() {
		return getRatingIcon(getRating());
	}

	private static final ImageIcon[] RATING_ICONS = new ImageIcon[7];
	
	public static ImageIcon getRatingIcon(int rating) {
		if (rating != 0 && rating < 1)
			rating = 1;
		else if (rating > 6)
			rating = 6;
		
		if (null == RATING_ICONS[rating]) {
			RATING_ICONS[rating] = new javax.swing.ImageIcon(
				RbScore.class.getResource(
					"/jshm/resources/images/ratings/rb/" + rating + ".gif"));
		}
		
		return RATING_ICONS[rating];
	}
}

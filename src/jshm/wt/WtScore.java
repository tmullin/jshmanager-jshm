package jshm.wt;

import java.util.Iterator;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Transient;
import javax.swing.ImageIcon;

import org.hibernate.criterion.Restrictions;

import jshm.*;

@Entity
public class WtScore extends Score {
	public static WtScore createNewScoreTemplate(final WtGame game, final Instrument.Group group, final Difficulty difficulty, final WtSong song) {
		WtScore s = new WtScore();
		s.setGame(game);
		s.setSong(song);
		s.setDifficulty(difficulty);
		s.setGroup(group);
		s.setStatus(Score.Status.TEMPLATE);
		
		for (Instrument i : group.instruments) {
			Part p = new Part();
			p.setInstrument(i);
			p.setDifficulty(difficulty);
			s.addPart(p);
		}
		
		return s;
	}
	
	
	@SuppressWarnings("unchecked")
	public static List<Score> getScores(final WtGame game, final Instrument.Group group, final Difficulty difficulty) {
		org.hibernate.Session session = jshm.hibernate.HibernateUtil.getCurrentSession();
	    session.beginTransaction();
	    List<Score> result =
			(List<Score>)
			session.createQuery(
				"FROM WtScore WHERE game=:game AND instrumentgroup=:group AND difficulty=:diff ORDER BY score DESC")
				.setString("game", game.toString())
				.setString("group", group.toString())
				.setString("diff", difficulty.toString())
				.list();
		
	    // set the SongOrder for each
	    for (Score s : result) {
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
	
	@SuppressWarnings("unchecked")
	public static List<Score> getSubmittableScores(final WtGame game, final Instrument.Group group, final Difficulty difficulty) {
		org.hibernate.Session session = jshm.hibernate.HibernateUtil.getCurrentSession();
	    session.beginTransaction();
	    List<Score> result =
			(List<Score>)
			session.createQuery(
				"FROM WtScore WHERE status IN ('NEW', 'UNKNOWN') AND score > 0 AND game=:game AND instrumentgroup=:group AND difficulty=:diff ORDER BY score DESC")
				.setString("game", game.toString())
				.setString("group", group.toString())
				.setString("diff", difficulty.toString())
				.list();
		
	    Iterator<Score> it = result.iterator();
	    
	    while (it.hasNext()) {
	    	if (!it.next().isSubmittable())
	    		it.remove();
	    }
	    
	    // set the SongOrder for each
	    for (Score s : result) {
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
	
	
	
	/**
	 * Adds a {@link Part}, checking for Expert+/drum constraints.
	 */
	@Override
	public void addPart(Part part) {
		if (Difficulty.EXPERT_PLUS == part.getDifficulty()) {
			if (!((WtGameTitle) getGameTitle()).supportsExpertPlus) {
				throw new IllegalArgumentException("trying to add expert+ score for a game that doesn't support it");
			}
			
			if (!part.getInstrument().isDrums()) {
				throw new IllegalArgumentException("trying to add expert+ part for non drum instrument");
			}
		}
		
		super.addPart(part);
	}
	
	@Transient @Override
	public ImageIcon getRatingIcon() {
		return jshm.gh.GhScore.getRatingIcon(getRating());
	}

	@Override
	public void submit() throws Exception {
		jshm.sh.Api.submitWtScore(this);
	}
}

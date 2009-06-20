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
package jshm.rb;

import java.util.Iterator;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Transient;
import javax.swing.ImageIcon;

import org.hibernate.criterion.Restrictions;

import jshm.Difficulty;
import jshm.Instrument;
import jshm.Part;
import jshm.Score;
import jshm.SongOrder;

@Entity
public class RbScore extends Score {
	public static RbScore createNewScoreTemplate(final RbGame game, final Instrument.Group group, final Difficulty difficulty, final RbSong song) {
		RbScore s = new RbScore();
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
	public static List<RbScore> getScores(final RbGame game, final Instrument.Group group, final Difficulty difficulty) {
		org.hibernate.Session session = jshm.hibernate.HibernateUtil.getCurrentSession();
	    session.beginTransaction();
	    List<RbScore> result =
			(List<RbScore>)
			session.createQuery(
				"FROM RbScore WHERE game=:game AND instrumentgroup=:group AND difficulty=:diff ORDER BY score DESC")
				.setString("game", game.toString())
				.setString("group", group.toString())
				.setString("diff", difficulty.toString())
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
	
	@SuppressWarnings("unchecked")
	public static List<RbScore> getSubmittableScores(final RbGame game, final Instrument.Group group, final Difficulty difficulty) {
		org.hibernate.Session session = jshm.hibernate.HibernateUtil.getCurrentSession();
	    session.beginTransaction();
	    List<RbScore> result =
			(List<RbScore>)
			session.createQuery(
				"FROM RbScore WHERE status IN ('NEW', 'UNKNOWN') AND score > 0 AND game=:game AND instrumentgroup=:group AND difficulty=:diff ORDER BY score DESC")
				.setString("game", game.toString())
				.setString("group", group.toString())
				.setString("diff", difficulty.toString())
				.list();
		
	    Iterator<RbScore> it = result.iterator();
	    
	    while (it.hasNext()) {
	    	if (!it.next().isSubmittable())
	    		it.remove();
	    }
	    
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

	public static ImageIcon getRatingIcon(int rating) {
		if (rating != 0 && rating < 1)
			rating = 1;
		else if (rating > 6)
			rating = 6;
		
		return jshm.gui.GuiUtil.getIcon(
			"ratings/rb/" + rating + ".gif");
	}
	
	
	public void submit() throws Exception {
		if (!isSubmittable())
			throw new IllegalArgumentException("This score is not submittable");
		
		jshm.sh.Api.submitRbScore(this);
	}
}

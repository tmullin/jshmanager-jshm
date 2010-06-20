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
package jshm.hibernate;

import org.hibernate.*;
import org.hibernate.cfg.*;

public class HibernateUtil {
	private static final SessionFactory sessionFactory;

	static {
		try {
			// Create the SessionFactory from hibernate.cfg.xml
			//            sessionFactory = new Configuration().configure().buildSessionFactory();
			sessionFactory = new AnnotationConfiguration()
				.configure(
					HibernateUtil.class.getResource(
						"/jshm/hibernate/hibernate.cfg.xml"))
				.buildSessionFactory();

		} catch (Throwable ex) {
			// Make sure you log the exception, as it might be swallowed
			System.err.println("Initial SessionFactory creation failed." + ex);
			throw new ExceptionInInitializerError(ex);
		}
	}

	public static SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	
	public static Session getCurrentSession() {
		return getSessionFactory().getCurrentSession();
	}

	public static Session openSession() {
		return getSessionFactory().openSession();
	}
	
	public static void shutdown() throws Exception {
		Session sess = null;
		Transaction tx = null;

		sess = getCurrentSession();
		tx = sess.beginTransaction();
		sess.createSQLQuery("SHUTDOWN COMPACT");
		tx.commit();
	}
}

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
				.configure("jshm/hibernate/hibernate.cfg.xml")
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

}
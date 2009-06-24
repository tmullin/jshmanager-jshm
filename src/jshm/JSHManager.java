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
package jshm;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import jshm.gui.GUI;
import jshm.gui.Splash;
import jshm.hibernate.HibernateUtil;

/**
 * A Java-based program to manage one's ScoreHero.com account.
 * @author Tim Mullin
 *
 */
public class JSHManager {
	public static class Version {
		public static final String NAME = "JSHManager";
		public static final int MAJOR = 0;
		public static final int MINOR = 3;
		public static final int POINT = 1;
		public static final boolean IS_BETA = true;
		
		public static final String
		VERSION =
			String.format("%s.%s.%s", MAJOR, MINOR, POINT),
		STRING =
			String.format("%s%s", VERSION, IS_BETA ? " beta" : "");
		
		public static final String LAST = "0.3.0";
		public static final int LAST_REVISION = 369;
		
		public static final java.util.Date DATE = initDate("$Date$");
		public static final int REVISION = initRevision("$Revision$");
		
		private static java.util.Date initDate(final String _APP_DATE) {
			try {
				// $Date$
				return new java.text.SimpleDateFormat("$'Date': yyyy-MM-dd HH:mm:ss Z (EE, dd MMM yyyy) $")
					.parse(_APP_DATE);
			} catch (java.text.ParseException e) {}
			
			return new java.util.Date();
		}
			
		private static int initRevision(final String _APP_REVISION) {
			try {
				// $Revision$
				return Integer.parseInt(_APP_REVISION.replaceAll("[^\\d]+", ""));
			} catch (NumberFormatException e) {}
			
			return 0;
		}
	}
	
	
	static final Logger LOG = Logger.getLogger(JSHManager.class.getName());
	static GUI gui = null;
	static Splash splash = null;
	
	/**
	 * Launches this application
	 */
	public static void main(String[] args) {
		try {
			checkJREVersion();
			
			splash = new Splash();
			
			// Ensure any uncaught exceptions are logged so that bugs
			// can be found more easily.
			Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
				public void uncaughtException(Thread thread, Throwable thrown) {
					LOG.log(Level.WARNING, "Exception in thread \"" + thread.getName() + "\"", thrown);
				}
			});
			
			splash.setStatus("Checking data folder...");
					
			for (String dir : new String[] {"db", "logs"}) {
				File f = new File("data/" + dir);
				if (f.exists()) continue;
				
				LOG.fine("Creating data folder: " + f.getPath());
				
				try {
					f.mkdirs();
				} catch (Throwable e) {
					fail("Failed to create data folders", e, -1);
				}
			}
			
			// have to do this after creating the log folder because
			// the logger won't make the dirs for us
			try {
				jshm.logging.Log.reloadConfig();
			} catch (Throwable e) {
				fail("Unable to load logger configuration", e, -3);
			}
			
			LOG.info("JSHManager " + Version.STRING + " starting up...");
			
			splash.setStatus("Loading configuration...");
			Config.init();
			
			splash.setStatus("Initializing database...");
			
			try {
				HibernateUtil.getCurrentSession();
			} catch (Throwable e) {
				fail("Unable to initialize database", e, -4);
			}
			
			splash.setStatus("Loading user interface...");
			
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {	
					try {
						gui = new GUI();
						gui.setVisible(true);
						gui.toFront();
						
						splash.dispose();
						splash = null;
					} catch (Throwable t) {
						fail(t.toString(), t, -43);
					}
				}
			});
		} catch (Throwable t) {
			fail("Unknown error", t, -42);
		}
	}
	
	/**
	 * This should be called when we want to exit the program.
	 * It will handling cleaning up stuff.
	 */
	public static void dispose() {
		splash = new Splash("Shutting down...");
		
		if (null != gui)
			gui.dispose();
		
		splash.setStatus("Saving database...");

		try {
			HibernateUtil.shutdown();
		} catch (Exception e) {
			LOG.log(Level.WARNING, "Error shutting down database", e);
		}
		
		splash.setStatus("Saving configuration...");
		Config.write();
		
		splash.dispose();
		
		System.exit(0);
	}
	
	private static void fail(String message, Throwable thrown, int exitCode) {
		LOG.log(Level.SEVERE, message, thrown);
		
		if (null != splash)
			splash.dispose();
		
		try {
			org.jdesktop.swingx.error.ErrorInfo ei = new org.jdesktop.swingx.error.ErrorInfo("Error",
				message, null, null, thrown, null, null);
			org.jdesktop.swingx.JXErrorPane.showDialog(null, ei);
		} catch (Exception t) {
			// in case there was a ClassNotFound or something
			
			LOG.log(Level.SEVERE, "Error displaying JXErrorPane", t);
			
			StringWriter sw = new StringWriter();
			thrown.printStackTrace(new PrintWriter(sw));
			
			JOptionPane.showMessageDialog(null,
				sw.toString(),
				message, JOptionPane.ERROR_MESSAGE);
		}
		
		System.exit(exitCode);
	}
	
	public static final int MIN_JRE_MAJOR_VERSION = 1;
	public static final int MIN_JRE_MINOR_VERSION = 6;
	public static final int MIN_JRE_POINT_VERSION = 0;
	
	private static void checkJREVersion() {
		try {
		    String version = System.getProperty("java.version");
		    java.util.regex.Matcher m =
			    java.util.regex.Pattern
			    	.compile("^(\\d+)\\.(\\d+)\\.(\\d+).*").matcher(version);
		    
		    if (!m.matches()) throw new Exception();
		    
		    int major = Integer.parseInt(m.group(1));
		    int minor = Integer.parseInt(m.group(2));
		    int point = Integer.parseInt(m.group(3));
		    
//		    JOptionPane.showMessageDialog(null,
//		    	String.format("version: %s\nfound: %s.%s.%s", version, major, minor, point));
		    	
		    if (major < MIN_JRE_MAJOR_VERSION ||
		    	(major == MIN_JRE_MAJOR_VERSION && minor < MIN_JRE_MINOR_VERSION) ||
		    	(major == MIN_JRE_MAJOR_VERSION && minor == MIN_JRE_MINOR_VERSION && point < MIN_JRE_POINT_VERSION)) {
		    	
				JOptionPane.showMessageDialog(null,
					"You are running a version of Java (" + version +
					") that is incompatible with this program.\nJava 1.6.0 or later is required.",
					"Error", JOptionPane.ERROR_MESSAGE);
				System.exit(-2);
		    }
		} catch (Throwable e) {
			JOptionPane.showMessageDialog(null,
				"Unable to determine JRE version.\nThis program requires at least Java 1.6.0 to run.",
				"Error", JOptionPane.ERROR_MESSAGE);
			System.exit(-2);
		}
	}
}

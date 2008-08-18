package jshm;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.jdesktop.swingx.error.ErrorInfo;

import jshm.gui.GUI;
import jshm.gui.Splash;
import jshm.hibernate.HibernateUtil;

/**
 * A Java-based program to manage one's ScoreHero.com account.
 * @author Tim Mullin
 *
 */
public class JSHManager {
	static final Logger LOG = Logger.getLogger(JSHManager.class.getName());
	static GUI gui = null;
	static Splash splash = null;
	
	/**
	 * Launches this application
	 */
	public static void main(String[] args) {
		checkJREVersion();
		
		splash = new Splash();
		
		try {
			jshm.logging.Log.reloadConfig();
		} catch (Exception e) {
			fail("Unable to load logger configuration", e, -3);
		}
		
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
			} catch (Exception e) {
				fail("Failed to create data folders", e, -1);
			}
		}
		
		splash.setStatus("Initializing database...");
		HibernateUtil.getCurrentSession();
		
		splash.setStatus("Loading user interface...");
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {				
				try {
					// Set the Look & Feel to match the current system
					UIManager.setLookAndFeel(
						UIManager.getSystemLookAndFeelClassName());
				} catch (Exception e) {
					LOG.log(Level.WARNING, "Couldn't set system look & feel (not fatal)", e);
				}

				gui = new GUI();
				gui.setVisible(true);
				gui.toFront();
				
				splash.dispose();
				splash = null;
			}
		});
	}
	
	public static void dispose() {
		splash = new Splash("Shutting down...");
		
		if (null != gui)
			gui.dispose();
		
		splash.dispose();
		
		System.exit(0);
	}
	
	private static void fail(String message, Throwable thrown, int exitCode) {
		LOG.log(Level.SEVERE, message, thrown);
		
		if (null != splash)
			splash.dispose();
		
		ErrorInfo ei = new ErrorInfo("Error",
			message, null, null, thrown, null, null);
		org.jdesktop.swingx.JXErrorPane.showDialog(null, ei);
		
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
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null,
				"Unable to determine JRE version.\nThis program requires at least Java 1.6.0 to run.",
				"Error", JOptionPane.ERROR_MESSAGE);
			System.exit(-2);
		}
	}
}

package jshm;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import jshm.gui.GUI;

/**
 * A Java-based program to manage one's ScoreHero.com account.
 * @author Tim Mullin
 *
 */
public class JSHManager {
	static final Logger log = Logger.getLogger(JSHManager.class.getName());
	
	/**
	 * Launches this application
	 */
	public static void main(String[] args) {
		// Ensure any uncaught exceptions are logged so that bugs
		// can be found more easily.
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			public void uncaughtException(Thread thread, Throwable thrown) {
				log.log(Level.WARNING, "Exception in thread \"" + thread.getName() + "\"", thrown);
			}
		});
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {				
				try {
					// Set the Look & Feel to match the current system
					UIManager.setLookAndFeel(
						UIManager.getSystemLookAndFeelClassName());
				} catch (Exception e) {
					log.log(Level.INFO, "Couldn't set system look & feel (not fatal)", e);
				}
				
				try {
					GUI application = new GUI();
					application.initialize();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

}

package jshm.util;

import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;

/**
 * Contains various random utilities.
 * @author Tim Mullin
 *
 */
public class Util {
	static final Logger LOG = Logger.getLogger(Util.class.getName());
	
	public static void openURL(String url) {
		try {
			openURL(url, true);
		} catch (Throwable t) {
			LOG.log(Level.SEVERE, "Failed to launch external browser ", t);
			ErrorInfo ei = new ErrorInfo("Error", "Failed to launch external browser", null, null, t, null, null);
			JXErrorPane.showDialog(null, ei);
		}
	}
	
/////////////////////////////////////////////////////////
//  Bare Bones Browser Launch                          //
//  Version 1.5                                        //
//  December 10, 2005                                  //
//  http://www.centerkey.com/java/browser/             //
//  Supports: Mac OS X, GNU/Linux, Unix, Windows XP    //
//  Example Usage:                                     //
//     String url = "http://www.centerkey.com/";       //
//     BareBonesBrowserLaunch.openURL(url);            //
//  Public Domain Software -- Free to Use as You Like  //
/////////////////////////////////////////////////////////

	// the boolean arg serves to distinguish this method that throws
	// an exception upon error from the above that handles an error
	public static void openURL(String url, boolean b) throws Exception {
		LOG.finer("Opening URL: " + url);
		
		String osName = System.getProperty("os.name");
		
		if (osName.startsWith("Mac OS")) {
			Class<?> fileMgr = Class.forName("com.apple.eio.FileManager");
			Method openURL =
				fileMgr.getDeclaredMethod("openURL", new Class[] {String.class});
			openURL.invoke(null, new Object[] {url});
		} else if (osName.startsWith("Windows")) {
			Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
		} else { //assume Unix or Linux
			String[] browsers = {
				"firefox", "opera", "konqueror", "epiphany", "mozilla", "netscape"};
			String browser = null;
			
			for (String s : browsers) {
				if (Runtime.getRuntime().exec(
					new String[] {"which", s}).waitFor() == 0) {
					browser = s;
					break;
				}
			}
			
			if (browser == null) {
				throw new RuntimeException("Could not find web browser, os=" + osName);
			} else {
				Runtime.getRuntime().exec(new String[] {browser, url});
			}
		}
	}
}

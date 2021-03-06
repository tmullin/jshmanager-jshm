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
	
	public static int versionCompare(int ... args) {
		if (args.length < 2 || args.length % 2 != 0)
			throw new IllegalArgumentException("args must have a positive, even number of elements");
		
		int i = 0;
		String v1 = String.valueOf(args[i++]);
		
		for (; i < args.length / 2; i++)
			v1 += "." + args[i];
		
		String v2 = String.valueOf(args[i++]);
		
		for (; i < args.length; i++)
			v2 += "." + args[i];
		
		return versionCompare(v1, v2);
	}
	
	public static int versionCompare(String v1, String v2) {
		String[] parts1 = v1.split("\\.");
		String[] parts2 = v2.split("\\.");
		
		if (parts1.length != parts2.length)
			throw new IllegalArgumentException("v1 and v2 must have the same number of parts");
		
		int[] i1 = new int[parts1.length];
		int[] i2 = new int[parts2.length];
		
		for (int i = 0; i < i1.length; i++) {
			try {
				i1[i] = Integer.parseInt(parts1[i]);
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("v1 contained an invalid string", e);
			}
			
			try {
				i2[i] = Integer.parseInt(parts2[i]);
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("v2 contained an invalid string", e);
			}
			
			if (i1[i] != i2[i])
				return i1[i] < i2[i] ? -1 : 1;
		}
		
		return 0;
	}
	
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

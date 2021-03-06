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
package jshm.internal;

import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import jshm.GameSeries;
import jshm.JSHManager;
import jshm.gui.LoginDialog;
import jshm.logging.Log;
import jshm.sh.Forum;
import jshm.sh.Forum.PostMode;
import jshm.util.PhpUtil;
import jshm.util.Util;

/**
 * This updates the first post of the 2 JSHManager threads. 
 * @author Tim Mullin
 *
 */
@SuppressWarnings("unused")
public class JshmThreadManager {
	static final boolean
		CREATE_NEW_VERSION_POST = false,
		APPEND_NEW_VERSION_CHANGELOG = true;
	
	static final String
		VERSION = JSHManager.Version.VERSION,
		DOWNLOAD_URL =
			"https://github.com/tmullin/jshmanager-jshm/releases/tag/JSHManager-0.3.6",
		SUBJECT =
			"JSHManager " + VERSION +
			" - RB4/GHL Support! - Manage Scores Locally and Upload to SH";
	
	static final int
		GH_THREAD_ID = 74670,
		GH_POST_ID = 1098748,
		RB_THREAD_ID = 15464,
		RB_POST_ID = 226303;
	
	public static void main(String[] args) throws Exception {
		Log.configTestLogging();
		
		List<String> lines = new ArrayList<String>();

		
		// read the MainThread content
	    BufferedReader in =
	    	new BufferedReader(
	    		new FileReader("internal/jshm/internal/MainThread.txt"));
	    
	    String line = null;
	    
	    while (null != (line = in.readLine())) {
	    	lines.add(replaceVars(line));
	    }
	    
	    in.close();
	    
	    
	    // read the ChangeLog
	    in =
	    	new BufferedReader(
	    		new FileReader("ChangeLog.txt"));
	    
	    // get rid of "JSHManager ChangeLog\n\n"
	    in.readLine();
	    in.readLine();
	    
	    // track the lines for the latest changes (needed below)
	    int versionsRead = 0;
	    int changesStartLine = lines.size();
	    int lastLatestChangeLine = changesStartLine;
	    boolean ignoreCurrentVersion = false;
	    
	    while (null != (line = in.readLine())) {
	    	if (line.startsWith("Version ")) {
	    		String curVersion = line.substring("Version ".length());
	    		int comp = Util.versionCompare(curVersion, VERSION);
	    		
	    		// perhaps we're editing the original post without
	    		// adding a new version so we don't want to show
	    		// the changelog for the beta version
	    		if (comp > 0) {
	    			System.out.println("Skipping changelog for version " + curVersion);
	    			ignoreCurrentVersion = true;
	    		} else {
	    			ignoreCurrentVersion = false;
	    			versionsRead++;
	    		}
	    	}

	    	if (ignoreCurrentVersion) continue;
	    	
	    	lines.add(line);
	    	
	    	if (versionsRead < 2) {
	    		lastLatestChangeLine++;
	    	}
	    }
	    
	    lastLatestChangeLine -= 2; // get rid of extra newlines
	    
	    in.close();
	    
	    String mainThreadBody = PhpUtil.implode("\n", lines);
	    
	    
	    // read NewVersionPost content
	    in =
	    	new BufferedReader(
	    		new FileReader("internal/jshm/internal/NewVersionPost.txt"));
	    
	    List<String> newVersionLines = new ArrayList<String>();
	    
	    while (null != (line = in.readLine())) {
	    	newVersionLines.add(replaceVars(line));
	    }
	    
	    in.close();
	    
	    // add the changes for the latest version
	    if (APPEND_NEW_VERSION_CHANGELOG)
	    	newVersionLines.addAll(lines.subList(
	    		changesStartLine, lastLatestChangeLine + 1));
	    
	    jshm.util.Print.print(newVersionLines);
	    
	    String newVersionPostBody = PhpUtil.implode("\n", newVersionLines);
	    
//	    System.exit(0);
	    
	    LoginDialog.showDialog();
	    Forum.post(GameSeries.GUITAR_HERO, PostMode.EDIT, GH_POST_ID, SUBJECT, mainThreadBody);
	    Forum.post(GameSeries.ROCKBAND, PostMode.EDIT, RB_POST_ID, SUBJECT, mainThreadBody);
	    
	    if (CREATE_NEW_VERSION_POST) {
		    Forum.post(GameSeries.GUITAR_HERO, PostMode.REPLY, GH_THREAD_ID, newVersionPostBody);
		    Forum.post(GameSeries.ROCKBAND, PostMode.REPLY, RB_THREAD_ID, newVersionPostBody);
	    }
	}
	
	static String replaceVars(String in) {
		return in
			.replace("$version", VERSION)
			.replace("$downloadUrl", DOWNLOAD_URL);
	}
}

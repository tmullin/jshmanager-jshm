package jshm.internal;

import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import jshm.GameSeries;
import jshm.JSHManager;
import jshm.gui.LoginDialog;
import jshm.sh.Forum;
import jshm.sh.Forum.PostMode;
import jshm.util.PhpUtil;

/**
 * This updates the first post of the 2 JSHManager threads. 
 * @author Tim Mullin
 *
 */
@SuppressWarnings("unused")
public class JshmThreadManager {
	static final boolean
		CREATE_NEW_VERSION_POST = true;
	
	static final String
		VERSION = JSHManager.Version.VERSION,
		DOWNLOAD_URL =
			"http://sourceforge.net/project/showfiles.php?group_id=240590&package_id=292656&release_id=629242",
		SUBJECT =
			"JSHManager " + VERSION +
			" - Manage Scores Locally and Upload to SH";
	
	static final int
		GH_THREAD_ID = 74670,
		GH_POST_ID = 1098748,
		RB_THREAD_ID = 15464,
		RB_POST_ID = 226303;
	
	public static void main(String[] args) throws Exception {
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
	    
	    while (null != (line = in.readLine())) {
	    	lines.add(line);
	    	
	    	if (line.startsWith("--------"))
	    		versionsRead++;
	    	
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
	    newVersionLines.addAll(lines.subList(changesStartLine, lastLatestChangeLine));
	    
	    String newVersionPostBody = PhpUtil.implode("\n", newVersionLines);
	    
	    
//	    System.out.println("Main thread body:");
//	    System.out.println("-------------------------------------------");
//	    System.out.println(mainThreadBody);
//	    System.out.println("-------------------------------------------\n\n");
//	    System.out.println("New version post body:");
//	    System.out.println("-------------------------------------------");
//	    System.out.println(newVersionPostBody);
//	    System.out.println("-------------------------------------------\n\n");
	    
	    LoginDialog.showDialog();
	    Forum.post(GameSeries.GUITAR_HERO, PostMode.EDIT, GH_POST_ID, SUBJECT, mainThreadBody);
	    Forum.post(GameSeries.ROCKBAND, PostMode.EDIT, RB_POST_ID, SUBJECT, mainThreadBody);
	    
	    if (CREATE_NEW_VERSION_POST) {
		    Forum.post(GameSeries.GUITAR_HERO, PostMode.REPLY, GH_THREAD_ID, SUBJECT, newVersionPostBody);
		    Forum.post(GameSeries.ROCKBAND, PostMode.REPLY, RB_THREAD_ID, SUBJECT, newVersionPostBody);
	    }
	}
	
	static String replaceVars(String in) {
		return in
			.replace("$version", VERSION)
			.replace("$downloadUrl", DOWNLOAD_URL);
	}
}

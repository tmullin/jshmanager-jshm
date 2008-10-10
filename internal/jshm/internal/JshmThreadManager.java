package jshm.internal;

import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import jshm.GameSeries;
import jshm.JSHManager;
import jshm.gui.LoginDialog;
import jshm.sh.Forum;
import jshm.util.PhpUtil;

/**
 * This updates the first post of the 2 JSHManager threads. 
 * @author Tim Mullin
 *
 */
@SuppressWarnings("unused")
public class JshmThreadManager {
	public static final int
		GH_THREAD_ID = 74670,
		GH_POST_ID = 1098748,
		RB_THREAD_ID = 15464,
		RB_POST_ID = 226303;
	
	public static String SUBJECT =
		"JSHManager " + JSHManager.Version.VERSION +
		" - Manage Scores Locally and Upload to SH";
	
	public static void main(String[] args) throws Exception {
		List<String> lines = new ArrayList<String>();
		
	    BufferedReader in =
	    	new BufferedReader(
	    		new FileReader("internal/jshm/internal/MainThread.txt"));
	    
	    String line = null;
	    
	    while (null != (line = in.readLine()))
	    	lines.add(line);
	    
	    in.close();
	    
	    in =
	    	new BufferedReader(
	    		new FileReader("ChangeLog.txt"));
	    
	    while (null != (line = in.readLine()))
	    	lines.add(line);
	    
	    in.close();
	    
	    for (String s : lines)
	    	System.out.println(s);
	    
//	    String body = PhpUtil.implode("\n", lines);
//	    
//	    LoginDialog.showDialog();
//	    Forum.editPost(GameSeries.GUITAR_HERO, GH_POST_ID, SUBJECT, body);
//	    Forum.editPost(GameSeries.ROCKBAND, RB_POST_ID, SUBJECT, body);
	}
}

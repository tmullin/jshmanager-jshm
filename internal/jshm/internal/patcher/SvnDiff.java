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
package jshm.internal.patcher;

import java.io.*;
import java.util.*;

/**
 * This tool runs the svn diff command to aid in generating
 * the patcher.
 * @author Tim Mullin
 *
 */
public class SvnDiff {
	static final String SVN_BASE = "http://jshm.svn.sourceforge.net/svnroot/jshm/trunk";
	static final int LAST_REVISION = jshm.JSHManager.Version.LAST_REVISION;
//	static final int LAST_REVISION = 69; // making 0.0.x to 0.1.0 patch
	
	static final File DIFF_FILE = new File("internal/jshm/internal/patcher/SvnDiff.txt");
	static final File CHANGED_FOLDER_FILE = new File("internal/jshm/internal/patcher/ChangedFolderFiles.txt");
	static final File CHANGED_JAR_FILE = new File("internal/jshm/internal/patcher/ChangedJarFiles.txt");
	
	static final String[] IGNORE_PREFIX = new String[] {
		".", "lib", "scripts/",
		"build.xml", "launch4j.xml", "test/", "internal", "src/jshm/concepts/"
	};
	
	static final String[] IGNORE_SUFFIX = new String[] {
		".form", "_64.png"
	};
	
	public static void main(String[] args) throws Exception {
		if (!DIFF_FILE.exists()) {
			// create the diff file
			
			ProcessBuilder pb = new ProcessBuilder(
				"svn", "diff", "--summarize", "--non-interactive",
				SVN_BASE + "@" + LAST_REVISION,
				SVN_BASE);
			pb.redirectErrorStream(true);
			
			System.out.println("Executing SVN diff...");
			Process p = pb.start();
			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = null;
			List<String> lines = new ArrayList<String>();
			
			while (null != (line = in.readLine())) {
				lines.add(line);
				System.out.println("SVN: " + line);
			}
			
			in.close();
			
			final int exitCode = p.exitValue();
			
			if (0 != exitCode) {
				System.out.println("Received non-zero exit code from SVN: " + exitCode);				
				System.exit(-1);
			}
			
			System.out.println("Writing DIFF_FILE...");
			BufferedWriter out = new BufferedWriter(new FileWriter(DIFF_FILE));
			
			for (String s : lines) {
				out.write(s.replace(SVN_BASE, ""));
				out.newLine();
			}
			
			out.close();
		}
		
		
		System.out.println("Reading DIFF_FILE...");
		BufferedReader in = new BufferedReader(new FileReader(DIFF_FILE));
		String line = null;
		List<SvnEntry> entries = new ArrayList<SvnEntry>();
		
		while (null != (line = in.readLine())) {
			entries.add(new SvnEntry(line));
		}
		
		in.close();
		
		
		// write the changed files
		
		BufferedWriter changedFolderOut = new BufferedWriter(new FileWriter(CHANGED_FOLDER_FILE));
		BufferedWriter changedJarOut = new BufferedWriter(new FileWriter(CHANGED_JAR_FILE));
		
EntriesLoop:
		for (SvnEntry e : entries) {
			if (e.path.isEmpty()) continue;
			
			// no extension so it's a folder
			if (e.path.lastIndexOf('/') > e.path.lastIndexOf('.')) continue;
			
			for (String s : IGNORE_PREFIX)
				if (e.path.startsWith(s)) continue EntriesLoop;
			for (String s : IGNORE_SUFFIX)
				if (e.path.endsWith(s)) continue EntriesLoop;

			System.out.println(e);
			
			if (e.path.startsWith("src/")) { // in the jar
				if (e.status == 'D') {
					// TODO handle deleting
				} else {
					String out = e.path.substring(4); // 4 = "src/".length()
					
					if (out.endsWith(".java")) {
						out = out.substring(0, out.length() - 5) + "*.class"; // 5 = ".java".length()
					}
					
					changedJarOut.write(out);
					changedJarOut.newLine();
				}
			} else { // in the folder
				if (e.status == 'D') {
					// TODO handle deleting
				} else {
					changedFolderOut.write(e.path);
					changedFolderOut.newLine();
				}
			}
		}

		changedJarOut.close();
		changedFolderOut.close();
	}
	
	
	static class SvnEntry {
		char status;
		String path;
		
		public SvnEntry(String s) {
			String[] parts = s.trim().split("\\s+", 2);
						
			status = parts[0].charAt(0);
			
			if (parts.length == 2)
				path = parts[1].charAt(0) == '/' ? parts[1].substring(1) : parts[1];
			else
				path = "";
		}
		
		public SvnEntry(char status, String path) {
			this(status + " " + path);
		}
		
		public String toString() {
			return status + " " + path;
		}
	}
}

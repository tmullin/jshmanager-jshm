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
import java.util.*;

/**
 * This prepends each .java file with the contents of
 * LicenseHeader.txt unless it has already been prepended.
 * @author Tim Mullin
 */
public class Licenser {
	static final String LICENSE_START = "LICENSE START";
	static final String LICENSE_END   = "LICENSE END";
	
	static enum FileType {
		java("/*", " * ", "*/"),
		properties("#", "# ", "#"),
		xml("<!--", "  ", "-->", "__"),
		form("<!--", "  ", "-->", "__");
		
		public final String
			PRE, LINE, POST, MARKER;
		
		private FileType(String pre, String line, String post) {
			this(pre, line, post, "-----");
		}
		
		private FileType(String pre, String line, String post, String marker) {
			PRE = pre;
			LINE = line;
			POST = post;
			MARKER = marker;
		}
	}
	
	static List<String> licenseLines = null;
	static int totalLicensed = 0;
	static int totalSkipped = 0;
	
	public static void main(String[] args) throws Exception {		
		licenseLines = new LinkedList<String>();
	    BufferedReader in =
	    	new BufferedReader(
	    		new FileReader("internal/jshm/internal/LicenseHeader.txt"));
	    
	    String line = null;
	    
	    while (null != (line = in.readLine())) {
	    	licenseLines.add(line);
	    }
	    
	    in.close();
	    
	    for (String s : new String[] {"src", "test", "internal"})
	    	insertLicense(new File(s));
	    
	    System.out.printf("\nLicensed %s, Skipped %s\n", totalLicensed, totalSkipped);
	}

	static FileType getType(File f) {
		int i = f.getName().lastIndexOf('.');
		String ext = f.getName().substring(i + 1);
		return FileType.valueOf(ext);
	}
	
	static final FileFilter FF = new FileFilter() {
		public boolean accept(File f) {
			String name = f.getName().toLowerCase();
			return f.isDirectory() ||
				name.endsWith(".java") ||
				name.endsWith(".properties") ||
				name.endsWith(".xml") ||
				name.endsWith(".form");
		}
	};
	
	static void insertLicense(File dir) throws Exception {
		BufferedReader in = null;
		PrintWriter out = null;
		String line = null;
		
		for (File f : dir.listFiles(FF)) {
			if (f.isDirectory()) {
				insertLicense(f);
				continue;
			}
			
			FileType ft = getType(f);
			File to = new File(f.getAbsolutePath() + ".tmp");
			
			if (alreadyLicensed(f, ft)) {
				System.out.println("Skipping licensed file " + f.getPath());
				totalSkipped++;
				continue;
			} else {
				System.out.println("Adding license to " + f.getPath());
				totalLicensed++;
			}

			in = new BufferedReader(new FileReader(f));
			out = new PrintWriter(new FileWriter(to));
			
			if (ft == FileType.xml || ft == FileType.form) {
				in.mark(1024);
				line = in.readLine();
				
				if (null != line && line.startsWith("<?xml")) {
					out.println(line);
				} else {
					in.reset();
				}
			}
			
			out.println(ft.PRE);
			
			out.print(ft.LINE);
			out.println(ft.MARKER + LICENSE_START + ft.MARKER);
			
			for (String s : licenseLines) {
				out.print(ft.LINE);
				out.println(s);
			}

			out.print(ft.LINE);
			out.println(ft.MARKER + LICENSE_END + ft.MARKER);			
			
			out.println(ft.POST);
			
			while (null != (line = in.readLine())) {
				out.println(line);
			}
			
			out.close();
			in.close();

			f.delete();
			to.renameTo(f);
		}
	}
	
	static boolean alreadyLicensed(File f, FileType ft) throws Exception {
		BufferedReader in = new BufferedReader(new FileReader(f));
		boolean ret = false;
		
		try {
			String line = in.readLine();
			
			if ((ft == FileType.xml || ft == FileType.form) &&
				line.startsWith("<?xml")) {
				line = in.readLine();
			}
			
			if (line.equals(ft.PRE)) {
				line = in.readLine();
				
				if (line.equals(ft.LINE + ft.MARKER + LICENSE_START + ft.MARKER)) {
					ret = true;
				}
			}
		} finally {
			in.close();
		}
		
		return ret;
	}
}

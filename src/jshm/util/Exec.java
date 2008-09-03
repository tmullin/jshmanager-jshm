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

import java.io.File;
import java.util.*;

/**
 * Utility to help execute commands from the system shell.
 * @author Tim Mullin
 *
 */
public class Exec {
	public static boolean IS_WINDOWS = System.getProperty("os.name").contains("Windows");
	
	public static void execNoOutput(String ... rawCmd) throws Exception {
		execNoOutput(null, rawCmd);
	}
	
	/**
	 * Executes the supplied command going through the OS's shell.
	 * cmd for Windows, sh for *nix. STDOUT and STDERR are redirected
	 * to nul for Windows, /dev/null for *nix.
	 * 
	 * <p>
	 * This should allow the command to be executed independently of
	 * this JVM instance.
	 * @param rawCmd
	 * @throws Exception
	 */
	public static void execNoOutput(File workingDir, String ... rawCmd) throws Exception {
		if (null == rawCmd || rawCmd.length < 1)
			throw new IllegalArgumentException("rawCmd cannot be null and must have at least 1 element");
			
		List<String> cmd = new ArrayList<String>();
		
		if (IS_WINDOWS) {
			cmd.add("cmd");
			cmd.add("/c");
		} else {
			cmd.add("/bin/sh");
			cmd.add("-c");
		}
		
		cmd.addAll(Arrays.asList(rawCmd));
		
		if (IS_WINDOWS) {
			cmd.add(">nul");
		} else {
			cmd.add(">/dev/null");
		}
		
		cmd.add("2>&1");
		
		Runtime.getRuntime().exec(cmd.toArray(new String[0]), null, workingDir);
	}
}

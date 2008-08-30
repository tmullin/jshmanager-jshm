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

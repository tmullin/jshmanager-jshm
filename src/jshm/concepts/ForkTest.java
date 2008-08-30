package jshm.concepts;

import java.io.File;

import jshm.util.Exec;

public class ForkTest {
	public static void main(String[] args) throws Exception {
		for (Object o : System.getProperties().keySet()) {
			System.out.println(o + "=" + System.getProperty(o.toString()));
		}
		
		System.out.println("\nCWD=" + new File(".").getAbsoluteFile().getCanonicalPath());
		System.out.println("Forking...");
		
		// this seems to work when running from a windows terminal but
		// ForkTest still seems to run from within eclipse
		Exec.execNoOutput(
			"java",
			"-cp", System.getProperty("java.class.path"),
			"jshm.gui.AboutDialog"
		);
		
		System.out.println("ForkTest is exiting.");
		System.exit(0);
	}
}

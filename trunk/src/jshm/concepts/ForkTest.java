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

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
import java.util.Date;

import jshm.gui.TextFileViewerDialog;
import jshm.util.PasteBin;

@SuppressWarnings("unused")
public class PasteBinTest {
	public static void main(String[] args) throws Exception {
//		String s =
//			"Please paste this into the JSHManager thread.\n\n" +
//			"JSHManager.txt\n" +
//			"http://pastebin/...";
//		
//		TextFileViewerDialog d = new TextFileViewerDialog();
//		d.setVisible(s);
//		
//		System.exit(0);
		
		File f = new File("data/logs/HttpClient.txt");
		
		String url = PasteBin.post("DarylZero", //f);
			"Test Post @ " + new Date());
		
		System.out.println("posted to " + url);
	}
}

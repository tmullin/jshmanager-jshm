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
package jshm.logging;

import java.util.logging.*;

//import jshm.Config;

public class Log {
	public static final boolean DEBUG = true;
	
	public static void reloadConfig() throws Exception {
		Handler consoleHandler = new ConsoleHandler();
		consoleHandler.setLevel(Level.ALL);
		consoleHandler.setFormatter(new OneLineFormatter());
		
		Logger cur = Logger.getLogger("");
		
		for (Handler h : cur.getHandlers())
			cur.removeHandler(h);
		
		cur.addHandler(consoleHandler);
		
		Formatter fileFormatter = new FileFormatter();
		Handler fileHandler = new FileHandler("data/logs/JSHManager.txt");
		fileHandler.setLevel(Level.ALL);
		fileHandler.setFormatter(fileFormatter);
		
		cur = Logger.getLogger("jshm");
		cur.addHandler(fileHandler);
		cur.setLevel(DEBUG ? Level.ALL : Level.INFO);
		
		Handler hibernateHandler = new FileHandler("data/logs/Hibernate.txt");
		hibernateHandler.setLevel(Level.ALL);
		hibernateHandler.setFormatter(fileFormatter);
		
		cur = Logger.getLogger("org.hibernate");
		
		for (Handler h : cur.getHandlers())
			cur.removeHandler(h);
		
		cur.addHandler(hibernateHandler);
		cur.setLevel(DEBUG ? Level.INFO : Level.WARNING);
	}
	
//	public static void reloadConfig() {
//		try {
//			String props =
//				/*(Config.getInstance() != null && Config.getInstance().getBool("general.debug")
//						 ?*/ "logging.debug.properties" /* : "logging.properties") */;
//			
////			System.out.println("Reading: " + props);
//			
//			InputStream in =
//				Log.class.getResourceAsStream("/jshm/properties/" + props);
//			LogManager.getLogManager().readConfiguration(in);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//	
//	public static void setLevel(Level level) {
//		setLevel("jshm", level);
//	}
//	
//	public static void setLevel(String logger, Level level) {
//		Logger.getLogger(logger).setLevel(level);
//	}
}

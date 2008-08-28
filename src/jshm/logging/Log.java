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
		// all logging
		Handler consoleHandler = new ConsoleHandler();
		consoleHandler.setLevel(Level.ALL);
		consoleHandler.setFormatter(new OneLineFormatter());
		
		Logger cur = Logger.getLogger("");
		removeHandlers(cur);
		
		cur.addHandler(consoleHandler);
		
		
		// jshm logging
		Formatter fileFormatter = new FileFormatter();
		Handler jshmHandler = new FileHandler("data/logs/JSHManager.txt");
		jshmHandler.setLevel(Level.ALL);
		jshmHandler.setFormatter(fileFormatter);
		
		cur = Logger.getLogger("jshm");
		cur.addHandler(jshmHandler);
		cur.setLevel(DEBUG ? Level.ALL : Level.INFO);
		
		
		// hibernate logging
		Handler hibernateHandler = new FileHandler("data/logs/Hibernate.txt");
		hibernateHandler.setLevel(Level.ALL);
		hibernateHandler.setFormatter(fileFormatter);
		
		cur = Logger.getLogger("org.hibernate");
		removeHandlers(cur);
		
		cur.addHandler(hibernateHandler);
		cur.setLevel(DEBUG ? Level.INFO : Level.WARNING);
		
		
		// HttpClient logging
		Handler httpClientHandler = new FileHandler("data/logs/HttpClient.txt");
		httpClientHandler.setLevel(Level.ALL);
		httpClientHandler.setFormatter(fileFormatter);
		
//		cur = Logger.getLogger("httpclient.wire");
		cur = Logger.getLogger("httpclient.wire.header");
		removeHandlers(cur);
		
		cur.addHandler(httpClientHandler);
		cur.setLevel(DEBUG ? Level.ALL : Level.INFO);
		
		cur = Logger.getLogger("org.apache.commons.httpclient");
		removeHandlers(cur);
		
		cur.addHandler(httpClientHandler);
		cur.setLevel(DEBUG ? Level.FINER : Level.INFO);
		
		
		// HtmlParser logging
		Handler htmlParserHandler = new FileHandler("data/logs/HtmlParser.txt");
		htmlParserHandler.setLevel(Level.ALL);
		htmlParserHandler.setFormatter(fileFormatter);

		cur = Logger.getLogger("org.htmlparser");
		removeHandlers(cur);
		
		cur.addHandler(htmlParserHandler);
		cur.setLevel(DEBUG ? Level.ALL : Level.INFO);
		
		
		// SwingX logging
		Handler swingxHandler = new FileHandler("data/logs/SwingX.txt");
		swingxHandler.setLevel(Level.ALL);
		swingxHandler.setFormatter(fileFormatter);

		cur = Logger.getLogger("org.jdesktop.swingx");
		removeHandlers(cur);
		
		cur.addHandler(swingxHandler);
		cur.setLevel(DEBUG ? Level.ALL : Level.INFO);
	}
	
	public static Logger removeHandlers(final Logger logger) {
		for (Handler h : logger.getHandlers())
			logger.removeHandler(h);
		return logger;
	}
}

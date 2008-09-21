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

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;

/**
 * This formatter is intended for console logging. It's format is:<br>
 * <pre>[HH:mm:ss][Level][Logger.method()]
 *  Message</pre>
 *  
 * ...yes it should be renamed.
 * @author Tim Mullin
 *
 */
public class OneLineFormatter extends Formatter {	
	static final SimpleDateFormat df =
		new SimpleDateFormat("HH:mm:ss");
	static final String fmt =
		"[%s][%-7s][%s.%s()]\n  %s\n";
	
	Date dt = new Date();
	
	public String format(LogRecord r) {
		StringBuilder sb = new StringBuilder();
		dt.setTime(r.getMillis());

		sb.append(String.format(
			fmt,
			getDateFormat().format(dt),
			r.getLevel(),
			r.getLoggerName(),
			r.getSourceMethodName(),
			r.getMessage()
		));	
		
		if (r.getThrown() != null) {
			try {
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				r.getThrown().printStackTrace(pw);
				pw.close();
				sb.append(sw.toString());
			} catch (Exception ex) {}
		}
				
		return sb.toString();
	}
	
	protected SimpleDateFormat getDateFormat() {
		return df;
	}
}

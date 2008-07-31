package jshm.logging;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;

public class OneLineFormatter extends Formatter {	
	static final SimpleDateFormat df =
		new SimpleDateFormat("HH:mm:ss");
	static final String fmt =
		"[%s][%-7s][%s][%s]: %s" + System.getProperty("line.separator");
	
	Date dt = new Date();
	
	public String format(LogRecord r) {
		StringBuilder sb = new StringBuilder();
		dt.setTime(r.getMillis());

		sb.append(String.format(
			fmt,
			getDateFormat().format(dt),
			r.getLevel().toString(),
			Thread.currentThread().getName(),
			r.getLoggerName(),
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

package jshm.logging;

import java.text.SimpleDateFormat;

public class FileFormatter extends OneLineFormatter {
	static SimpleDateFormat df =
		new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	protected SimpleDateFormat getDateFormat() {
		return df;
	}
}

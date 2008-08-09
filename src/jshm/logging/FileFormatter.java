package jshm.logging;

import java.text.SimpleDateFormat;

/**
 * This has the same format as {@link OneLineFormatter}
 * except with an extended date format of <code>[yyyy-MM-dd HH:mm:ss]</code>
 * since it's easier to scroll a long line in a file compared
 * to in a console.
 * @author Tim Mullin
 *
 */
public class FileFormatter extends OneLineFormatter {
	static SimpleDateFormat df =
		new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	protected SimpleDateFormat getDateFormat() {
		return df;
	}
}

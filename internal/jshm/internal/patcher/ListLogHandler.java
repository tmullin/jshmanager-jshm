package jshm.internal.patcher;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * This Handler directs log messages to the msgList in
 * PatcherGui via {@link PatcherGui#addMessage(String)}.
 * @author Tim Mullin
 *
 */
public class ListLogHandler extends Handler {
	static final SimpleDateFormat DATE_FMT =
		new SimpleDateFormat("HH:mm:ss");
	
	private PatcherGui gui;
	
	public ListLogHandler(final PatcherGui gui, final Level level) {
		this.gui = gui;
		this.setLevel(level);
	}
	
	@Override
	public void close() throws SecurityException {
		gui = null;
	}

	@Override
	public void flush() {
		// doesn't do anything
	}

	@Override
	public void publish(LogRecord record) {
		if (null == gui) return;
		
		gui.addMessage(
			String.format("%s: %s",
				DATE_FMT.format(new Date(record.getMillis())),
				record.getMessage()));
	}

}

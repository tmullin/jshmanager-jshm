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

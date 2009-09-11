/*
 * -----LICENSE START-----
 * JSHManager - A Java-based tool for managing one's ScoreHero account.
 * Copyright (C) 2008, 2009 Tim Mullin
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
package jshm.gui;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.KeyStroke;

public abstract class MyAction extends AbstractAction {
	MyAction(String name, Icon smallIcon,
			String shortDescription,
			KeyStroke accelerator, int mnemonic) {
		this(true, name, smallIcon,
			shortDescription,
			accelerator, mnemonic);
	}
	
	MyAction(boolean enabled, String name, Icon smallIcon,
			String shortDescription,
			KeyStroke accelerator, int mnemonic) {
		this(enabled, name, smallIcon, smallIcon,
			shortDescription, shortDescription,
			accelerator, mnemonic);
	}
	
	MyAction(boolean enabled, String name, Icon smallIcon, Icon largeIcon,
		String shortDescription, String longDescription,
		KeyStroke accelerator, int mnemonic) {
		setEnabled(enabled);
		putValue(Action.NAME, name);
		putValue(Action.SMALL_ICON, smallIcon);
		putValue(Action.LARGE_ICON_KEY, largeIcon);
		putValue(Action.SHORT_DESCRIPTION, shortDescription);
		putValue(Action.LONG_DESCRIPTION, longDescription);
		putValue(Action.ACCELERATOR_KEY, accelerator);
		putValue(Action.MNEMONIC_KEY, mnemonic);
	}
}

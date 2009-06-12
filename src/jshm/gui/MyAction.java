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
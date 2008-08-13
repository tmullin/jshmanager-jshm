package jshm.gui.tasks;

import javax.swing.*;

public abstract class Task<T, V> extends SwingWorker<T, V> {
	protected JFrame parent;
	protected ProgressMonitor progmon;
	
	public Task(JFrame parent, String message) {
		this(parent, message, false);
	}
	
	public Task(JFrame parent, String message, boolean forceShowProgress) {
		this.parent = parent;
		this.progmon = new ProgressMonitor(parent, message, "", 0, 100);
		
		if (forceShowProgress) {
			this.progmon.setMillisToDecideToPopup(0);
			this.progmon.setMillisToPopup(0);
		}
	}
}

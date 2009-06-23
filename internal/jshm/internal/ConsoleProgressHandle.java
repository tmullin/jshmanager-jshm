package jshm.internal;

import java.awt.Container;

import org.netbeans.spi.wizard.ResultProgressHandle;

/**
 * An implementation of {@link ResultProgressHandle} that just
 * prints to System.out.
 * @author Tim Mullin
 *
 */
public class ConsoleProgressHandle implements ResultProgressHandle {
	private static ConsoleProgressHandle instance = null;
	
	public static ConsoleProgressHandle getInstance() {
		if (null == instance)
			instance = new ConsoleProgressHandle();
		return instance;
	}
	
	private ConsoleProgressHandle() {}
	
	private boolean isRunning = true;
	
	@Override public void addProgressComponents(Container panel) {}
	
	@Override
	public void failed(String message, boolean canNavigateBack) {
		isRunning = false;
		System.out.println(message);
	}
	
	@Override
	public void finished(Object result) {
		isRunning = false;
	}
	
	@Override
	public boolean isRunning() {
		return isRunning;
	}

	@Override
	public void setBusy(String description) {
		System.out.println(description);
	}

	@Override public void setProgress(int currentStep, int totalSteps) {}

	@Override
	public void setProgress(String description, int currentStep,
			int totalSteps) {
		System.out.println(description);
	}
}

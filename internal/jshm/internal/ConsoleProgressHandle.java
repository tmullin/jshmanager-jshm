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

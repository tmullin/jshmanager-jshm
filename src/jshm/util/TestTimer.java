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
package jshm.util;

import java.util.*;

/**
 * This class serves as a helper for timing how long
 * sections of code take to execute. This class can be used by
 * multiple threads since the start time is stored for each
 * thread in a Map.
 * @author Tim Mullin
 *
 */
public class TestTimer {
	private static Map<Thread, Long> timers =
		new HashMap<Thread, Long>();
	
	public static void start() {
		start(true);
	}
	
	public static void start(boolean printMem) {
		if (printMem) printMemUsage();
		timers.put(Thread.currentThread(), System.currentTimeMillis());
	}
	
	public static double stop() {
		return stop(true);
	}
	
	public static double stop(boolean printMem) {
		double seconds = 0.0;
		
		try {
			long diff = System.currentTimeMillis() - timers.get(Thread.currentThread());
			seconds = diff / 1000.0;
			System.out.println("Time: " + seconds + " seconds");
			timers.remove(Thread.currentThread());
		} catch (NullPointerException e) {
			// silently ignore
//			throw new RuntimeException("Cannot stop timer without starting it", e);
		}
		
		if (printMem) printMemUsage();
		
		start(false);
		
		return seconds;
	}
	
	public static void printMemUsage() {
		long total = Runtime.getRuntime().totalMemory(); 
		double totalMb = total / (double) (1 << 20);
		
		double usedMb = // in mb
			(total - Runtime.getRuntime().freeMemory()) /
			(double) (1 << 20);
		
		System.out.printf("Mem: %01.3f/%01.3f (used/total) mb\n", usedMb, totalMb);
	}
}

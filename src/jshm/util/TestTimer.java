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
			throw new RuntimeException("Cannot stop timer without starting it", e);
		}
		
		if (printMem) printMemUsage();
		
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

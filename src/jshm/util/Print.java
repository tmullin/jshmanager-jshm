package jshm.util;

public class Print {
	/**
	 * Prints a two-dimensional array in a reasonable format
	 * @param data
	 */
	public static <T> void print(T[][] data) {
		for (T[] cur : data) {
			for (T s : cur) {
				System.out.print(s);
				System.out.print(',');
			}
			
//			if (cur.length > 0)
				System.out.println();
		}
	}
}

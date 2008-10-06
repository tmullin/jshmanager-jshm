package jshm.concepts;

import java.util.Enumeration;

import jshm.GameSeries;
import jshm.util.Text;

public class TextTest {
	public static void main(String[] args) {
		Text t = new Text(GameSeries.class);
		
		Enumeration<String> e = t.getKeys();
		
		while (e.hasMoreElements()) {
			String key = e.nextElement();
			
			System.out.printf("%s -> %s\n", key, t.get(key));
		}
	}
}

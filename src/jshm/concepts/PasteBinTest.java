package jshm.concepts;

import java.io.File;
import java.util.Date;

import jshm.util.PasteBin;

@SuppressWarnings("unused")
public class PasteBinTest {
	public static void main(String[] args) throws Exception {
		File f = new File("data/logs/HttpClient.txt");
		
		String url = PasteBin.post("DarylZero", //f);
			"Test Post @ " + new Date());
		
		System.out.println("posted to " + url);
	}
}

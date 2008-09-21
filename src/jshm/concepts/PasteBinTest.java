package jshm.concepts;

import java.io.File;
import java.util.Date;

import jshm.gui.TextFileViewerDialog;
import jshm.util.PasteBin;

@SuppressWarnings("unused")
public class PasteBinTest {
	public static void main(String[] args) throws Exception {
//		String s =
//			"Please paste this into the JSHManager thread.\n\n" +
//			"JSHManager.txt\n" +
//			"http://pastebin/...";
//		
//		TextFileViewerDialog d = new TextFileViewerDialog();
//		d.setVisible(s);
//		
//		System.exit(0);
		
		File f = new File("data/logs/HttpClient.txt");
		
		String url = PasteBin.post("DarylZero", //f);
			"Test Post @ " + new Date());
		
		System.out.println("posted to " + url);
	}
}

package jshm.logging;

import java.io.*;
import java.util.logging.*;

//import jshm.Config;

public class Log {
	public static void reloadConfig() {
		try {
			String props =
				/*(Config.getInstance() != null && Config.getInstance().getBool("general.debug")
						 ?*/ "logging.debug.properties" /* : "logging.properties") */;
			
//			System.out.println("Reading: " + props);
			
			InputStream in =
				Log.class.getResourceAsStream("/jshm/properties/" + props);
			LogManager.getLogManager().readConfiguration(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void setLevel(Level l) {
		Logger.getLogger("jshm").setLevel(l);
	}
}

package jshm.logging;

import java.util.logging.*;

//import jshm.Config;

public class Log {
	public static final boolean DEBUG = true;
	
	public static void reloadConfig() throws Exception {
		Handler consoleHandler = new ConsoleHandler();
		consoleHandler.setFormatter(new OneLineFormatter());
		
		Logger cur = Logger.getLogger("");
		
		for (Handler h : cur.getHandlers())
			cur.removeHandler(h);
		
		cur.addHandler(consoleHandler);
		
		Formatter fileFormatter = new FileFormatter();
		Handler fileHandler = new FileHandler("data/logs/JSHManager.txt");
		fileHandler.setFormatter(fileFormatter);
		
		cur = Logger.getLogger("jshm");
		cur.addHandler(fileHandler);
		cur.setLevel(DEBUG ? Level.ALL : Level.INFO);
		
		Handler hibernateHandler = new FileHandler("data/logs/Hibernate.txt");
		hibernateHandler.setFormatter(fileFormatter);
		
		cur = Logger.getLogger("org.hibernate");
		
		for (Handler h : cur.getHandlers())
			cur.removeHandler(h);
		
		cur.addHandler(hibernateHandler);
		cur.setLevel(DEBUG ? Level.INFO : Level.WARNING);
	}
	
//	public static void reloadConfig() {
//		try {
//			String props =
//				/*(Config.getInstance() != null && Config.getInstance().getBool("general.debug")
//						 ?*/ "logging.debug.properties" /* : "logging.properties") */;
//			
////			System.out.println("Reading: " + props);
//			
//			InputStream in =
//				Log.class.getResourceAsStream("/jshm/properties/" + props);
//			LogManager.getLogManager().readConfiguration(in);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//	
//	public static void setLevel(Level level) {
//		setLevel("jshm", level);
//	}
//	
//	public static void setLevel(String logger, Level level) {
//		Logger.getLogger(logger).setLevel(level);
//	}
}

package jshm.internal.patcher;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.jar.*;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.*;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import jshm.logging.FileFormatter;
import jshm.util.Properties;

//import org.jdesktop.swingx.JXErrorPane;
//import org.jdesktop.swingx.error.ErrorInfo;

/**
 * This class serves to replace files in an existing jar with the new patched
 * files.
 * 
 * @author Tim Mullin
 * 
 */
public class Patcher {
	static class PatchEntry {
		String name;
		InputStream inStream;
		
		public PatchEntry(String name, InputStream inStream) {
			this.name = name;
			this.inStream = inStream;
		}
		
		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			
			String oname = null;
			
			if (o instanceof PatchEntry)
				oname = ((PatchEntry) o).name;
			else if (o instanceof String)
				oname = (String) o;
			else
				return false;
			
			return this.name.equals(oname);
		}
	}
	
	static Properties props = new Properties();
	static File patchJarFile = null;
	static File progJarFile = null;
	static PatcherGui gui = null;
	final static Logger LOG = Logger.getLogger(Patcher.class.getName());
	
	public static void main(String[] args) throws Exception {
		try {
			// Set the Look & Feel to match the current system
			UIManager.setLookAndFeel(
				UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
//			LOG.log(Level.WARNING, "Couldn't set system look & feel (not fatal)", e);
		}
		
		try {
			progJarFile = getProgJarFile().getAbsoluteFile();
			
			gui = new PatcherGui();
			gui.setLocationRelativeTo(null);
			gui.setVisible(true);
			configLogging();
			
			log("Current folder is " + progJarFile.getParentFile().getCanonicalPath());
									
			log("Checking JSHManager.jar...");
//			JarLoader.load(progJarFile);
			
//			try {
				log("Checking patch file...");
				String url = Patcher.class.getResource("/jshm/internal/patcher/Patcher.class").toExternalForm();
				File f = getJarFileFromURL(url);
				
				if (null == f) {
					url = Patcher.class.getResource("/jshm/internal/patcher/PatchTest.jar").toExternalForm();
					f = new File(URI.create(url));
				}
				
				patchJarFile = f;
				
				log("Using patch file " + patchJarFile.getCanonicalPath());
				
				JarFile patchJar = new JarFile(f);
				JarEntry propsEntry = patchJar.getJarEntry("jshm/internal/patcher/Patcher.properties");
				
				if (null != propsEntry) {
					props.load(
						patchJar.getInputStream(propsEntry));
				}
				
				log("Loaded " + props.keySet().size() + " patcher properties");
				
				for (Object key : props.keySet()) {
					log("  " + key + "=" + props.get(key));
				}
				
				log("Patching JSHManager.jar...");
				
				addFilesToExistingZip(
					progJarFile,
					getPatchEntries(patchJar, "jar/"),
					Arrays.asList(props.getArray("jardelete")));
				
				log("Patching main directory...");
				patchMainDir(
					progJarFile.getParentFile(),
					getPatchEntries(patchJar, "folder/"),
					Arrays.asList(props.getArray("folderdelete")));
				
				log("Patching complete.", 1, 1);
				gui.setClosedEnabled(true);
//			} catch (Throwable t) {
//				LOG.log(Level.SEVERE, "Unknown error while patching", t);
//				Class.forName("org.jdesktop.swingx.JXErrorPane", true, JarLoader.getLoader(patchJarFile));
//				
//				ErrorInfo ei = new ErrorInfo("Error", "Unknown error while patching", null, null, t, null, null);
//				JXErrorPane.showDialog(null, ei);
//				
//				gui.dispose();
//				System.exit(0);
//			}
		} catch (Throwable t) {
			LOG.log(Level.SEVERE, "Unknown error while patching", t);
			
			StringWriter sw = new StringWriter();
			t.printStackTrace(new PrintWriter(sw));
			
			JOptionPane.showMessageDialog(null,
				sw.toString(),
				"Error", JOptionPane.ERROR_MESSAGE);
			
			gui.dispose();
			System.exit(0);
		}
	}

	static void configLogging() throws Exception {
		Handler h = new ListLogHandler(gui, Level.ALL);
		LOG.addHandler(h);
		File logDir = new File(progJarFile.getParentFile(), "data/logs");
		logDir.mkdirs();
		h = new FileHandler(logDir.getAbsolutePath() + "/Patcher.txt");
		h.setLevel(Level.ALL);
		h.setFormatter(new FileFormatter());
		LOG.addHandler(h);
	}
	
	static void log(String msg) {
		log(msg, -1, -1);
	}
	
	static void log(String msg, int cur, int max) {
		LOG.info(msg);
		gui.setProgress(msg, cur, max);
	}
	
	public static File getProgJarFile() throws Exception {
		File f = new File("JSHManager.jar");
		
		if (!f.exists()) {
			JOptionPane.showMessageDialog(null, 
				"JSHManager.jar is required but was not found.\n" +
				"Please locate it so that it can be patched.",
				"Error", JOptionPane.WARNING_MESSAGE);
			
			final JFileChooser fc = new JFileChooser();
			fc.setCurrentDirectory(new File("."));
			fc.setAcceptAllFileFilterUsed(false);
			fc.setFileFilter(new FileFilter() {
				@Override
				public boolean accept(File f) {
					return f.isDirectory() || f.getName().equals("JSHManager.jar");
				}

				@Override
				public String getDescription() {
					return "JSHManager.jar";
				}
			});
			
			int ret = fc.showDialog(null, null);
			
			if (ret != JFileChooser.APPROVE_OPTION) {
				JOptionPane.showMessageDialog(null,
					"JSHManager Patcher was canceled.",
					"", JOptionPane.INFORMATION_MESSAGE);
				System.exit(0);
			}
			
			f = fc.getSelectedFile();
		}
		
		return f;
	}
	
	public static List<PatchEntry> getPatchEntries(final JarFile patchJar, final String prefix) throws IOException {
		List<PatchEntry> ret = new ArrayList<PatchEntry>();
		
		Enumeration<JarEntry> en = patchJar.entries();
		
		while (en.hasMoreElements()) {
			JarEntry cur = en.nextElement();
			if (cur.isDirectory()) continue;
			
			if (cur.getName().startsWith(prefix)) {
				ret.add(
					new PatchEntry(
						cur.getName().substring(prefix.length()),
						patchJar.getInputStream(cur)));
			}
		}
		
		return ret;
	}

	public static void patchMainDir(final File dir, final List<PatchEntry> entries, final List<String> delete) throws Exception {
		if (!dir.isDirectory()) throw new IllegalArgumentException("dir is not a directory: " + dir.getCanonicalPath());
		
		int i = 0;
		for (String s : delete) {
			File cur = new File(dir, s);
			log(
				String.format("Deleting %s (%s of %s)", s, i + 1, delete.size()));
			cur.delete();
			i++;
		}
		
		i = 0;
		for (PatchEntry e : entries) {
			File cur = new File(dir, e.name);
			log(
				String.format("Patching %s (%s of %s)", e.name, i + 1, entries.size()),
				i, entries.size());
			JarLoader.copy(e.inStream, cur);
			i++;
		}
	}
	
	public static void addFilesToExistingZip(final File zipFile, final List<PatchEntry> entries, final List<String> delete)
			throws IOException {
		// get a temp file
		File tempFile = File.createTempFile(zipFile.getName(), null);
		// delete it, otherwise you cannot rename your existing zip to it.
		tempFile.delete();

		boolean renameOk = zipFile.renameTo(tempFile);
		if (!renameOk) {
			throw new RuntimeException("could not rename the file "
					+ zipFile.getAbsolutePath() + " to "
					+ tempFile.getAbsolutePath());
		}
		
//		log("Copying files...");
		
		byte[] buf = new byte[1024];

		ZipInputStream zin = new ZipInputStream(new FileInputStream(tempFile));
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile));

		ZipEntry entry = zin.getNextEntry();
		int unchangedCount = 0;
		int deletedCount = 0;
		while (entry != null) {
			String name = entry.getName();
			boolean notInFiles = true;
			
			// don't copy file so it can be overridden
			for (PatchEntry e : entries) {
				if (e.name.equals(name)) {
					notInFiles = false;
					break;
				}
			}
			
			// don't copy file so it can be deleted
			if (notInFiles && delete.contains(name)) {
				deletedCount++;
				log(
					String.format("Deleting %s (%s of %s)", name, deletedCount, delete.size()));
				notInFiles = false;
			}
			
			if (notInFiles) {
				unchangedCount++;
				
				gui.setProgress(
					String.format("Copying unchanged files (%s so far)...", unchangedCount));
				
				// Add ZIP entry to output stream.
				out.putNextEntry(new ZipEntry(name));
				// Transfer bytes from the ZIP file to the output file
				int len;
				while ((len = zin.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
			}
			
			entry = zin.getNextEntry();
		}
		// Close the streams
		zin.close();
		
		// Compress the files
		int i = 0;
		for (PatchEntry e : entries) {
			log(
				String.format("Patching %s (%s of %s)", e.name, i + 1, entries.size()),
				i, entries.size());
			
			InputStream in = e.inStream;
			// Add ZIP entry to output stream.
			out.putNextEntry(new ZipEntry(e.name));
			// Transfer bytes from the file to the ZIP file
			int len;
			
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			
			// Complete the entry
			out.closeEntry();
			in.close();
			
			i++;
		}
		
		// Complete the ZIP file
		out.close();
		tempFile.delete();
	}

	
	// from azureus
    public static File getJarFileFromURL(String url_str ) {
    	if (url_str.startsWith("jar:file:")) {
        	
        	// java web start returns a url like "jar:file:c:/sdsd" which then fails as the file
        	// part doesn't start with a "/". Add it in!
    		// here's an example 
    		// jar:file:C:/Documents%20and%20Settings/stuff/.javaws/cache/http/Dparg.homeip.net/P9090/DMazureus-jnlp/DMlib/XMAzureus2.jar1070487037531!/org/gudy/azureus2/internat/MessagesBundle.properties
    			
        	// also on Mac we don't get the spaces escaped
        	
    		url_str = url_str.replaceAll(" ", "%20" );
        	
        	if ( !url_str.startsWith("jar:file:/")) {
        		url_str = "jar:file:/".concat(url_str.substring(9));
        	}
        	
        	try{
        			// 	you can see that the '!' must be present and that we can safely use the last occurrence of it
          	
        		int posPling = url_str.lastIndexOf('!');
            
        		String jarName = url_str.substring(4, posPling);
        		
        			//        System.out.println("jarName: " + jarName);
        		
        		URI uri = URI.create(jarName);
        		
        		File jar = new File(uri);
        		
        		return( jar );
        		
        	}catch( Throwable e ){
        		e.printStackTrace();
        		//Debug.printStackTrace( e );
        	}
    	}
    	
    	return( null );
    }
}

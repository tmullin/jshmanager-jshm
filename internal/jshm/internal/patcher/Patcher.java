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

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;

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
	}
	
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
			progJarFile = getProgJarFile();
			
			gui = new PatcherGui();
			gui.setLocationRelativeTo(null);
			gui.setVisible(true);
			configLogging();
			
			log("Checking JSHManager.jar...");
			JarLoader.load(progJarFile);
			
			try {
				log("Checking patch file...");
				String url = Patcher.class.getResource("/jshm/internal/patcher/Patcher.class").toExternalForm();
				File f = getJarFileFromURL(url);
				
				if (null == f) {
					url = Patcher.class.getResource("/jshm/internal/patcher/PatchTest.jar").toExternalForm();
					f = new File(URI.create(url));
				}
				
				log("Using patch file " + f.getCanonicalPath());
				
				JarFile patchJar = new JarFile(f);
				
				log("Patching JSHManager.jar...");
				
				addFilesToExistingZip(
					progJarFile,
					getPatchEntries(patchJar, "jar/"));
				
				log("Patching main directory...");
				patchMainDir(
					progJarFile.getParentFile(),
					getPatchEntries(patchJar, "folder/"));
				
				log("Patching complete.", 1, 1);
				gui.setClosedEnabled(true);
			} catch (Throwable t) {
				ErrorInfo ei = new ErrorInfo("Error", "Unknown error while patching", null, null, t, null, null);
				JXErrorPane.showDialog(null, ei);
				
				gui.dispose();
				System.exit(0);
			}
		} catch (Throwable t) {
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

	public static void patchMainDir(final File dir, final List<PatchEntry> entries) throws Exception {
		if (!dir.isDirectory()) throw new IllegalArgumentException("dir is not a directory: " + dir.getCanonicalPath());
		
		int i = 0;
		for (PatchEntry e : entries) {
			File cur = new File(dir, e.name);
			log(
				String.format("Patching %s (%s of %s)", e.name, i + 1, entries.size()),
				i, entries.size());
			JarLoader.copy(e.inStream, cur);
			i++;
		}
	}
	
	public static void addFilesToExistingZip(final File zipFile, final List<PatchEntry> entries)
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
		
		log("Copying unchanged files...");
		
		byte[] buf = new byte[1024];

		ZipInputStream zin = new ZipInputStream(new FileInputStream(tempFile));
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile));

		ZipEntry entry = zin.getNextEntry();
		int unchangedCount = 0;
		while (entry != null) {
			String name = entry.getName();
			boolean notInFiles = true;
			
			for (PatchEntry e : entries) {
				if (e.name.equals(name)) {
					notInFiles = false;
					break;
				}
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

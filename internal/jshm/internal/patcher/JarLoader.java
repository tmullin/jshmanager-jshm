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
package jshm.internal.patcher;

import java.io.*;
import java.net.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.jar.*;

/**
 * This needs work...
 * @author Tim Mullin
 *
 */
public class JarLoader {
	public static URLClassLoader getLoader(File jar) throws Exception {
		File tmpJar = File.createTempFile(jar.getName(), null);
		copy(jar, tmpJar);
		tmpJar.deleteOnExit();
		
		return
			new URLClassLoader(
				new URL[] {
					tmpJar.toURI()
						.toURL()});
	}
	
	/**
	 * Loads all the classes in the provided jar file. The file is
	 * copied to a temp file that will be deleted when the JVM exits
	 * so that the original file is not locked.
	 * @param jar
	 * @throws Exception
	 */
	public static void load(File jar) throws Exception {
		File tmpJar = File.createTempFile(jar.getName(), null);
		copy(jar, tmpJar);
		tmpJar.deleteOnExit();
		
		URLClassLoader loader = getLoader(tmpJar);
		
		JarInputStream jis = new JarInputStream(new FileInputStream(tmpJar));
		JarEntry entry = jis.getNextJarEntry();
		int loadedCount = 0, totalCount = 0;

		while (null != entry) {
			String name = entry.getName();

			if (name.endsWith(".class")) {
				totalCount++;
				name = name.substring(0, name.length() - 6);
				name = name.replace('/', '.');

				try {
					loader.loadClass(name);
					loadedCount++;
				} catch (Throwable e) {
					System.out.printf("Failed to load %s. %s: %s\n",
						name, e.getClass().getName(), e.getMessage());
				}
			}

			entry = jis.getNextJarEntry();
		}
		
		System.out.printf("Loaded %s of %s successfully.\n", loadedCount, totalCount);
	}
	
	public static void copy(InputStream source, File dest) throws IOException {
		FileOutputStream out = null;
		
		try {
			out = new FileOutputStream(dest);
			byte[] buff = new byte[1024];
			int read = -1;
			
			while ((read = source.read(buff)) > -1) {
				out.write(buff, 0, read);
			}
		} finally {
			if (null != source) source.close();
			if (null != out) out.close();
		}
	}
	
	public static void copy(File source, File dest) throws IOException {
		FileChannel in = null, out = null;
		try {
			in = new FileInputStream(source).getChannel();
			out = new FileOutputStream(dest).getChannel();

			long size = in.size();
			MappedByteBuffer buf = in.map(FileChannel.MapMode.READ_ONLY, 0, size);

			out.write(buf);

		} finally {
			if (in != null) in.close();
			if (out != null) out.close();
		}
	}
}

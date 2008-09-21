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
import java.lang.reflect.Method;
import java.net.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 
 * @author Tim Mullin
 *
 */
public class JarLoader {
	public static void addFileToClasspath(File file) throws Exception {	
		Method method = URLClassLoader.class.getDeclaredMethod("addURL", new Class[]{ URL.class }); //$NON-NLS-1$
		method.setAccessible(true);
		method.invoke(ClassLoader.getSystemClassLoader(), new Object[]{ file.toURI().toURL() });
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

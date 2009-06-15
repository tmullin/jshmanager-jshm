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
package jshm.util;

import java.io.*;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;

import jshm.exceptions.ClientException;
import jshm.sh.client.HttpForm;

/**
 * This utility class allows posting data to PasteBin.
 * @author Tim Mullin
 *
 */
public class PasteBin {
	public static final String
		BASE_URL = "http://jshm.pastebin.com",
		PASTEBIN_URL = BASE_URL + "/pastebin.php";
	
	public static String post(File file) throws Exception {
		return post(null, file);
	}
	
	public static String post(String name, File file) throws Exception {
		BufferedReader in = null;
		StringBuilder sb = new StringBuilder();
		
		try {
			in = new BufferedReader(new FileReader(file));
			
			sb.append(file.getAbsolutePath());
			sb.append("\n\n");
			
			int charsRead = -1;
			char[] buff = new char[1024];
			
			while (-1 != (charsRead = in.read(buff))) {
				sb.append(buff, 0, charsRead);
			}
		} finally {
			if (null != in)
				in.close();
		}
		
		return post(name, sb.toString());
	}
	
	public static String post(String content) throws Exception {
		return post(null, content);
	}
	
	public static String post(String name, String content) throws Exception {
		if (null == name)
			name = jshm.sh.Client.getUsername();
		
		HttpForm form = new HttpForm((Object) PASTEBIN_URL,
			"parent_pid", "",
			"format", "text",
			"code2", content,
			"poster", name,
			"expiry", "m",
			"paste", "Send") {
			
			public void afterSubmit(final int response, final HttpClient client, final HttpMethod method) throws Exception {
				try {
					if (response != 302)
						throw new ClientException("expecting 302 response, got " + response);
					
					Header h = method.getResponseHeader("Location");
					
					if (null == h)
						throw new ClientException("response did not have Location header");
					
					userData = h.getValue();
				} finally {
					method.releaseConnection();
				}
			}
		};
		form.submit();
		
		return form.getUserData() != null
		? form.getUserData().toString()
		: null;
	}
}

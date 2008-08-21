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
package jshm.sh;

import org.apache.commons.httpclient.*;

import jshm.exceptions.*;
import jshm.sh.URLs;
import jshm.sh.client.HttpForm;

public class Client {
	private static HttpClient httpClient = null;
	
	public static HttpClient getHttpClient() {
		if (null == httpClient) {
			httpClient = new HttpClient();
		}
		
		return httpClient;
	}
	
	private static Cookie[] cookieCache = null;
	
	/**
	 * 
	 * @return Whether we already have the auth cookies
	 */
	public static boolean hasAuthCookies() {
		return null != cookieCache;
	}
	
	/**
	 *  
	 * @return The auth cookies if they are cached or null
	 * otherwise. This is useful to obtain the cookies
	 * for subsequent requests after they have been retrieved
	 * initially without needing to provide credentials again.
	 */
	public static Cookie[] getAuthCookies() {
		return getAuthCookies(true);
	}
	
	/**
	 * 
	 * @param required Whether the cookies are required. If true, an
	 * exception is thrown if null == cookieCache. Useful for dev.
	 * @return
	 * @see #getAuthCookies()
	 */
	public static Cookie[] getAuthCookies(boolean required) {
		if (required && null == cookieCache)
			throw new RuntimeException("auth cookies are required but not present");
		return cookieCache;
	}
	
	/**
	 * Login to ScoreHero and retrieve the necessary cookies
	 * for subsequent requests that require being logged in.
	 * The results are cached.
	 * @param userName
	 * @param password
	 * @return
	 * @throws Exception
	 */
	public static Cookie[] getAuthCookies(
		final String userName, final String password)
	throws Exception {
		return getAuthCookies(userName, password, true);
	}

	/**
	 * Login to ScoreHero and retrieve the necessary cookies
	 * for subsequent requests that require being logged in.
	 * @param userName
	 * @param password
	 * @param useCache Whether to use the cached results, if possible
	 * @return
	 * @throws java.io.IOException
	 * @throws ClientException
	 */
	public static Cookie[] getAuthCookies(
		final String userName, final String password, final boolean useCache)
	throws Exception {
		if (null != cookieCache && useCache) return cookieCache;
		
		new HttpForm((Object) URLs.LOGIN_URL,
			"uname", userName,
			"pass", password,
			"remember", "1",
			"submit", "Login") {
			
			@Override
			public void afterSubmit(final int response, final HttpClient client, final HttpMethod method) throws Exception {
				Cookie[] cookies = client.getState().getCookies();
				
				if (response == 302 && checkAuthCookies(cookies, userName)) {
//					System.out.println("Validated Successfully!");
					method.releaseConnection();
					cookieCache = cookies;
					return;
				}
				
				String body = method.getResponseBodyAsString();
				method.releaseConnection();
				
				if (body.contains("Invalid login, please try again.")) {
					throw new ClientException("invalid login credentials");
				}
				
				if (body.contains("Too many failed attempts, you must wait before trying again.")) {
					throw new ClientException("too many failed login attempts");
				}
				
				throw new ClientException("login failed, unknown error");

			}
		}.submit();
		
		return cookieCache;
	}
	
	/**
	 * Checks if the retrieved cookies match what we're expecting
	 * from the server.
	 * @param cookies
	 * @param userName
	 * @return
	 */
	static boolean checkAuthCookies(final Cookie[] cookies, final String userName) {
		if (cookies.length < 9) return false;
		
		int found = 0;
		
		for (Cookie c : cookies) {
			if (c.getName().equals("gh_uname") && c.getValue().equals(userName))
				found++;
			
			if (c.getName().equals("ghf_uname") && c.getValue().equals(userName))
				found++;
		}
		
		return found == 2;
	}
}

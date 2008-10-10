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

import java.io.IOException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.HeadMethod;

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
	
	private static String username = "";
	
	public static String getUsername() {
		return username;
	}
	
	public static String getPhpBb2MySqlSid() {
		for (Cookie c : getHttpClient().getState().getCookies()) {
			if (c.getName().equals("phpbb2mysql_sid"))
				return c.getValue();
		}
		
		return "";
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
		
		Client.username = userName;
		
		new HttpForm((Object) URLs.LOGIN_URL,
			"uname", userName,
			"pass", password,
			"remember", "1",
			"submit", "Login") {
			
			@Override
			public void afterSubmit(final int response, final HttpClient client, final HttpMethod method) throws Exception {
				HttpForm.LOG.finest("entered getAuthCookies().HttpForm.afterSubmit()");
				
				Cookie[] cookies = client.getState().getCookies();
				
				if (response == 302 && checkAuthCookies(cookies, userName)) {
//					System.out.println("Validated Successfully!");
					method.releaseConnection();
					cookieCache = cookies;
					HttpForm.LOG.finest("exiting getAuthCookies().HttpForm.afterSubmit()");
					return;
				}
				
				String body = method.getResponseBodyAsString();
				method.releaseConnection();
				
				try {
					Pattern p = Pattern.compile("<span class=\"error\"[^>]*>(.+?)</span>");
					Matcher m = p.matcher(body);
					
					if (m.find()) {
						throw new ClientException(m.group(1));
					}
					
					LOG.warning("Unhandled login failure, responseCode=" + response + ", response body follows");
					LOG.warning(body);
					
					throw new ClientException("login failed, unknown error");
				} catch (Exception t) {
					LOG.throwing("Client", "getAuthCookies().HttpClient.afterSubmit()", t);
					throw t;
				}
			}
		}.submit();
		
		return cookieCache;
	}
	
	static final int REQUIRED_MATCHING_COOKIES = 2;
	
	/**
	 * Checks if the retrieved cookies match what we're expecting
	 * from the server.
	 * @param cookies
	 * @param userName
	 * @return
	 */
	static boolean checkAuthCookies(final Cookie[] cookies, final String userName) {
		if (cookies.length < 9) {
			HttpForm.LOG.warning("expecting 9 cookies but got " + cookies.length);
			return false;
		}
		
		int found = 0;
		
		for (Cookie c : cookies) {
			if (c.getName().equals("gh_uname") && c.getValue().equalsIgnoreCase(userName))
				found++;
			
			if (c.getName().equals("ghf_uname") && c.getValue().equalsIgnoreCase(userName))
				found++;
		}
		
		if (found != REQUIRED_MATCHING_COOKIES) {
			HttpForm.LOG.warning("expecting to match " + REQUIRED_MATCHING_COOKIES + " cookies but got " + found);
			return false;
		}
		
		return true;
	}
	
	public static HeadMethod makeHeadRequest(URL url) throws HttpException, IOException {
		return makeHeadRequest(url.toExternalForm());
	}
	
	public static HeadMethod makeHeadRequest(String url) throws HttpException, IOException {
		HeadMethod method = new HeadMethod(url);
		getHttpClient().executeMethod(method);
		method.releaseConnection();
		return method;
	}
}

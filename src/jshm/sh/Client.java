package jshm.sh;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.*;

import jshm.exceptions.*;
import jshm.sh.URLs;

public class Client {
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
	 * @throws java.io.IOException
	 * @throws ClientException
	 */
	public static Cookie[] getAuthCookies(
		final String userName, final String password)
	throws java.io.IOException, ClientException {
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
	throws java.io.IOException, ClientException {
		if (null != cookieCache && useCache) return cookieCache;
		
		HttpClient client = new HttpClient();
		client.getHostConfiguration().setHost(URLs.DOMAIN);
		
		PostMethod post = new PostMethod(URLs.LOGIN_URL);
		post.setRequestBody(new NameValuePair[]{
			new NameValuePair("uname", userName),
			new NameValuePair("pass", password),
			new NameValuePair("remember", "1"),
			new NameValuePair("submit", "Login")
		});
		
		int response = client.executeMethod(post);
//		System.out.println("Login form post: " + post.getStatusLine().toString());
		
		Cookie[] cookies = client.getState().getCookies();
		
		if (response == 302 && checkAuthCookies(cookies, userName)) {
//			System.out.println("Validated Successfully!");
			post.releaseConnection();
			cookieCache = cookies;
			return cookies;
		}
		
		String body = post.getResponseBodyAsString();
		post.releaseConnection();
		
		if (body.contains("Invalid login, please try again.")) {
			throw new ClientException("invalid login credentials");
		}
		
		if (body.contains("Too many failed attempts, you must wait before trying again.")) {
			throw new ClientException("too many failed login attempts");
		}
		
		throw new ClientException("login failed, unknown error");
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

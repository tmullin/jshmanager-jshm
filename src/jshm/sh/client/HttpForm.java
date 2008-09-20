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
package jshm.sh.client;

import java.util.logging.Logger;

import jshm.sh.Client;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.*;

public class HttpForm {
	public static final Logger LOG = Logger.getLogger(HttpForm.class.getName());
	
	public final String url;
	public final NameValuePair[] data;
	public final HttpMethod method;
	public final String methodName;
	
	protected Object userData;
	
	public HttpForm(final Object url, final String ... data) {
		this("POST", url, data);
	}
	
	/**
	 * 
	 * @param method Is an {@link Object} for the purpose of overloading the constructor
	 * @param url
	 * @param data An array of Strings with alternating keys and values
	 */
	public HttpForm(final String method, final Object url, final String ... data) {
		if ((data.length & 1) != 0)
			throw new IllegalArgumentException("data must have an even number of values");
		if (!(url instanceof String))
			throw new IllegalArgumentException("url must be a String");
		
		this.url = (String) url;
		this.data = new NameValuePair[data.length / 2];
		
		// this.data[0] = data[0], data[1]
		// this.data[1] = data[2], data[3]
		// this.data[2] = data[4], data[5]
		for (int i = 0; i < data.length; i += 2) {
			this.data[i / 2] = new NameValuePair(data[i], data[i + 1]);
		}
		
		this.methodName = method;
		
		if ("POST".equalsIgnoreCase(method.toString())) {
			PostMethod postMethod = new PostMethod(this.url);
			postMethod.setRequestBody(this.data);
			this.method = postMethod;
		} else if ("GET".equalsIgnoreCase(method.toString())) {
			GetMethod getMethod = new GetMethod(this.url);
			getMethod.setQueryString(this.data);
			this.method = getMethod;
		} else {
			throw new IllegalArgumentException("method must be POST or GET, given: " + method);
		}
	}
	
	public final void submit() throws Exception {
		LOG.fine("Submitting form via " + methodName + " to " + url);
		
		for (NameValuePair nvp : data) {
			LOG.finer("  " + nvp.getName() + "=" +
				(nvp.getName().toLowerCase().contains("pass")
				 ? "*****"
				 : nvp.getValue()));
		}
		
		LOG.finest("calling getHttpClient()");
		HttpClient client = Client.getHttpClient();
		LOG.finest("executing method");
		int response = client.executeMethod(this.method);
		LOG.fine("Received " + response + " response code");
		this.afterSubmit(response, client, this.method);
		
		LOG.finest("exiting HttpForm.submit()");
	}
	
	/**
	 * Will be called once the form is submitted. Can be overridden to perform
	 * some action. If overridden, method.releaseConnection() should be called.
	 * @param responseCode
	 * @param client
	 * @param method
	 */
	public void afterSubmit(final int response, final HttpClient client, final HttpMethod method) throws Exception {
		method.releaseConnection();
	}
	
	/**
	 * A subclass can use the protected userData field if it
	 * needs to give information back to the caller.
	 * @return
	 */
	public Object getUserData() {
		return userData;
	}
}

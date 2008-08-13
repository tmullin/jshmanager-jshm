package jshm.sh.client;

import jshm.sh.Client;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.*;

public class HttpForm {
	public final String url;
	public final NameValuePair[] data;
	public final HttpMethod method;
	
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
		HttpClient client = Client.getHttpClient();
		int response = client.executeMethod(this.method);
		this.afterSubmit(response, client, this.method);
	}
	
	/**
	 * Will be called once the form is submitted. Can be overridden to perform
	 * some action.
	 * @param responseCode
	 * @param client
	 * @param method
	 */
	public void afterSubmit(final int response, final HttpClient client, final HttpMethod method) throws Exception {
		
	}
}

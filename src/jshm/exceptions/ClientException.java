package jshm.exceptions;

/**
 * This represents an exception that would occur when
 * the program is acting as an HTTP client, such as
 * when retrieving login cookies with invalid credentials.
 * @author Tim Mullin
 *
 */
public class ClientException extends Exception {
	public ClientException(String message) {
		super(message);
	}
	
	public ClientException(String message, Throwable thrown) {
		super(message, thrown);
	}
}

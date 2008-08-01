package jshm.exceptions;

/**
 * This represents an exception that would occur during
 * a scraper operation, such as the page not being
 * formatted as we thought it would be.
 * @author Tim Mullin
 *
 */
public class ScraperException extends Exception {
	public ScraperException(String message) {
		super(message);
	}
	
	public ScraperException(String message, Throwable thrown) {
		super(message, thrown);
	}
}

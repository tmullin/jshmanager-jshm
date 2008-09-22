package jshm.exceptions;

/**
 * Represents an exception from the csv package.
 * @author Tim Mullin
 *
 */
public class CsvException extends Exception {
	public CsvException(String message) {
		super(message);
	}
	
	public CsvException(String message, Throwable thrown) {
		super(message, thrown);
	}
}

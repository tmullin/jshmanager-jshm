package jshm.scraper.format;

public class FormatException extends RuntimeException {
	public FormatException(String message) {
		super(message);
	}
	
	public FormatException(String message, Throwable cause) {
		super(message, cause);
	}
}

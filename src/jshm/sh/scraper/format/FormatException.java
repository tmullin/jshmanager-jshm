package jshm.sh.scraper.format;

public class FormatException extends Exception {
	public FormatException(String message) {
		super(message);
	}
	
	public FormatException(String message, Throwable cause) {
		super(message, cause);
	}
}

package jshm.xml;

import java.util.logging.Logger;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class ErrorHandler implements org.xml.sax.ErrorHandler {
	static final Logger LOG = Logger.getLogger(ErrorHandler.class.getName());
	static ErrorHandler instance = null;
	
	public static ErrorHandler getInstance() {
		if (null == instance)
			instance = new ErrorHandler();
		return instance;
	}
	
	private ErrorHandler() {}
	
	public void warning(SAXParseException e) throws SAXException {
		LOG.warning("Warning while parsing XML:");
		printInfo(e);
	}
	
	public void error(SAXParseException e) throws SAXException {
		LOG.warning("Error while parsing XML:");
		printInfo(e);
	}
	
	public void fatalError(SAXParseException e) throws SAXException {
		LOG.warning("Fatal error while parsing XML:");
		printInfo(e);
	}
	
	private void printInfo(SAXParseException e) {
		LOG.warning("   Public ID: " + e.getPublicId());
		LOG.warning("   System ID: " + e.getSystemId());
		LOG.warning("   Line number: " + e.getLineNumber());
		LOG.warning("   Column number: " + e.getColumnNumber());
		LOG.warning("   Message: " + e.getMessage());
	}
}

/*
 * -----LICENSE START-----
 * JSHManager - A Java-based tool for managing one's ScoreHero account.
 * Copyright (C) 2008, 2009 Tim Mullin
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

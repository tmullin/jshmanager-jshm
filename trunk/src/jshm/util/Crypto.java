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
package jshm.util;

import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.codec.binary.Base64;

/**
 * By "encryption" I actually mean Base64 encoding... 
 * javax.crypto.* wasn't working on Linux.
 * @author Tim Mullin
 *
 */
public class Crypto {
	static final Logger LOG = Logger.getLogger(Crypto.class.getName());
	
	public static String encrypt(String clearText) {
		LOG.finest("entered Crypto.encrypt()");
		
		LOG.finer("Base64 encoding clearText");
		clearText = new String(Base64.encodeBase64(clearText.getBytes()));
		
		LOG.finest("exiting Crypto.encrypt()");
		return clearText;
	}
	
	public static String decrypt(String cipherText) {
		LOG.finest("entered Crypto.decrypt()");
		
		LOG.finer("Base64 decoding cipherText");
		
		try {
			cipherText = new String(Base64.decodeBase64(cipherText.getBytes()));
		} catch (ArrayIndexOutOfBoundsException e) {
			// this is only necessary to deal with passwords that were
			// previously encoded via the old javax.crypto.* method
			
			LOG.info("Clearing password to handle new storage mechanism");
			LOG.log(Level.FINER, "Exception from old password", e);
			cipherText = "";
		}
		
		LOG.finest("exiting Crypto.decrypt()");
		return cipherText;
	}
	
	
	public static void main(String[] args) {
		Scanner s = new Scanner(System.in);
		String in = s.nextLine();
		
		System.out.println(
			args.length > 0 && args[0].toLowerCase().startsWith("d")
			? decrypt(in)
			: encrypt(in));
	}
}

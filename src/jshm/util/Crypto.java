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

import javax.crypto.*;
import javax.crypto.spec.*;

/**
 * This class serves to provide a <i>basic</i> means
 * for encryption. This is mainly so that we can save
 * passwords so that they aren't simply plain text.
 * Obviously since this is open source it is easy to
 * see the key, but the point is just so that it's not
 * plain.
 * 
 * <p>
 * If the crypto classes aren't available it should simply
 * pass the unencrypted string along without doing anything.
 * </p>
 * @author Tim Mullin
 *
 */
public class Crypto {
	static final Logger LOG = Logger.getLogger(Crypto.class.getName());
	
	private static final SecretKey key = initKey();
	
	private static SecretKey initKey() {
		try {
			// jenny jenny HHGG
			byte[] desKeyData = {
				(byte) 0x08, (byte) 0x06, (byte) 0x07, (byte) 0x05,
				(byte) 0x03, (byte) 0x00, (byte) 0x09, (byte) 0x42
			};
			
			DESKeySpec desKeySpec = new DESKeySpec(desKeyData);
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			return keyFactory.generateSecret(desKeySpec);
		} catch (Throwable e) {
			LOG.log(Level.WARNING, "Unable to initialize Crypto.key", e);
		}
		
		return null;
	}
	
	private static final Cipher cipher = initCipher();
	
	private static Cipher initCipher() {
		try {
			return Cipher.getInstance("DES/ECB/PKCS5Padding");
		} catch (Throwable e) {
			LOG.log(Level.WARNING, "Unable to initialize Crypto.cipher", e);
		}
		
		return null;
	}
	
	public static String encrypt(String clearText) {
		LOG.finest("entered Crypto.encrypt()");
		
		try {
			LOG.finest("initting encryption cipher");
			cipher.init(Cipher.ENCRYPT_MODE, key);
			LOG.finest("encrypting clearText");
			clearText = new String(cipher.doFinal(clearText.getBytes()));
			LOG.finest("encryption succeeded");
		} catch (Throwable e) {
			LOG.log(Level.WARNING, "Unable to encrypt string", e);
		}
		
		LOG.finest("exiting Crypto.encrypt()");
		return clearText;
	}
	
	public static String decrypt(String cipherText) {
		LOG.finest("entered Crypto.decrypt()");
		
		try {
			LOG.finest("initting decryption cipher");
			cipher.init(Cipher.DECRYPT_MODE, key);
			LOG.finest("decrypting cipherText");
			cipherText = new String(cipher.doFinal(cipherText.getBytes()));
			LOG.finest("decryption succeeded");
		} catch (Throwable e) {
			LOG.log(Level.WARNING, "Unable to decrypt string", e);
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

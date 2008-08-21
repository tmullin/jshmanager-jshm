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
		try {
			cipher.init(Cipher.ENCRYPT_MODE, key);
			return new String(cipher.doFinal(clearText.getBytes()));
		} catch (Throwable e) {
			LOG.log(Level.WARNING, "Unable to encrypt string", e);
		}
		
		return clearText;
	}
	
	public static String decrypt(String cipherText) {
		try {
			cipher.init(Cipher.DECRYPT_MODE, key);
			return new String(cipher.doFinal(cipherText.getBytes()));
		} catch (Throwable e) {
			LOG.log(Level.WARNING, "Unable to decrypt string", e);
		}
		
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

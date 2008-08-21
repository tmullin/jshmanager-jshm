package jshm.util;

import org.junit.*;
import static org.junit.Assert.*;

public class CryptoTest {
	static final String plain1 = "test string 123";
	
	@Test public void cryptoTest1() {
		String encrypted = Crypto.encrypt(plain1);
//		System.out.println(plain1 + " -> " + encrypted);
		String decrypted = Crypto.decrypt(encrypted);
		
		assertEquals(plain1, decrypted);
	}
}

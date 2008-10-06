package jshm.util;

import org.junit.*;
import static org.junit.Assert.*;

public class PhpUtilTest {
	public static final int
		L = 0, R = 1, T = 2;
	
	private void trim(int func, String in, String charList, String expected) {
		String out = null;
		
		switch (func) {
			case L:
				out = null != charList ? PhpUtil.ltrim(in, charList) : PhpUtil.ltrim(in);
				break;
			case R:
				out = null != charList ? PhpUtil.rtrim(in, charList) : PhpUtil.rtrim(in);
				break;
			case T:
				out = null != charList ? PhpUtil.trim(in, charList) : PhpUtil.trim(in);
				break;
				
			default:
				throw new IllegalArgumentException("Invalid func number: " + func);
		}
		
		assertEquals(expected, out);
	}
	
	
	@Test public void ltrim1() {
		trim(L, "  \t\tfoo  bar  1234  baz\t\t  ", null,
				"foo  bar  1234  baz\t\t  ");
	}
	
	@Test public void ltrim2() {
		trim(L, "\t\tfoo  bar  1234 baz", null,
				"foo  bar  1234 baz");
	}
	
	@Test public void ltrim3() {
		trim(L, "foo  bar  1234 baz\t\t", null,
				"foo  bar  1234 baz\t\t");
	}
	
	@Test public void ltrim4() {
		trim(L, "foo  bar  1234 baz", null,
				"foo  bar  1234 baz");
	}
	
	@Test public void ltrim5() {
		trim(L, ".: . \tfoo . bar\t . :.", ".:", " . \tfoo . bar\t . :.");	
	}
	
	@Test public void ltrim6() {
		trim(L, ".: . \tfoo . bar\t . :.", ".: \t", "foo . bar\t . :.");	
	}
	
	
	@Test public void rtrim1() {
		trim(R, "  \t\tfoo  bar  1234  baz\t\t  ", null,
				"  \t\tfoo  bar  1234  baz");
	}
	
	@Test public void rtrim2() {
		trim(R, "\t\tfoo  bar  1234 baz", null,
				"\t\tfoo  bar  1234 baz");
	}
	
	@Test public void rtrim3() {
		trim(R, "foo  bar  1234 baz\t\t", null,
				"foo  bar  1234 baz");
	}
	
	@Test public void rtrim4() {
		trim(R, "foo  bar  1234 baz", null,
				"foo  bar  1234 baz");
	}
	
	@Test public void rtrim5() {
		trim(R, ".: . \tfoo . bar\t . :.", ".:", ".: . \tfoo . bar\t . ");	
	}
	
	@Test public void rtrim6() {
		trim(R, ".: . \tfoo . bar\t . :.", ".: \t", ".: . \tfoo . bar");	
	}
	
	
	@Test public void trim1() {
		trim(T, "  \t\tfoo  bar  1234  baz\t\t  ", null,
				"foo  bar  1234  baz");
	}
	
	@Test public void trim2() {
		trim(T, "\t\tfoo  bar  1234 baz", null,
				"foo  bar  1234 baz");
	}
	
	@Test public void trim3() {
		trim(T, "foo  bar  1234 baz\t\t", null,
				"foo  bar  1234 baz");
	}
	
	@Test public void trim4() {
		trim(T, "foo  bar  1234 baz", null,
				"foo  bar  1234 baz");
	}
	
	@Test public void trim5() {
		trim(T, ".: . \tfoo . bar\t . :.", ".:", " . \tfoo . bar\t . ");	
	}
	
	@Test public void trim6() {
		trim(T, ".: . \tfoo . bar\t . :.", ".: \t", "foo . bar");	
	}
}

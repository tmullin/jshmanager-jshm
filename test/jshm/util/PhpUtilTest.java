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
	
	@Test public void ltrim7() {
		trim(L, "", null, "");
	}
	
	@Test public void ltrim8() {
		trim(L, "", ".: \t", "");
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
	
	@Test public void rtrim7() {
		trim(R, "", null, "");
	}
	
	@Test public void rtrim8() {
		trim(R, "", ".: \t", "");
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
	
	@Test public void trim7() {
		trim(T, "", null, "");
	}
	
	@Test public void trim8() {
		trim(T, "", ".: \t", "");
	}
}

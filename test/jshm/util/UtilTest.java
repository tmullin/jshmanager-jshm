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

public class UtilTest {
	private void versionCompare(int expected, String v1, String v2) {
		int actual = Util.versionCompare(v1, v2);
		assertEquals(expected, actual);
	}
	
	@Test public void versionCompare1() {
		versionCompare(0, "1.2.3", "1.2.3");
	}
	
	@Test public void versionCompare2() {
		versionCompare(-1, "1.2.3", "1.2.4");
	}
	
	@Test public void versionCompare3() {
		versionCompare(-1, "1.2.3", "1.3.3");
	}
	
	@Test public void versionCompare4() {
		versionCompare(-1, "1.2.3", "2.2.3");
	}
	
	@Test public void versionCompare5() {
		versionCompare(1, "1.2.4", "1.2.3");
	}
	
	@Test public void versionCompare6() {
		versionCompare(1, "1.3.3", "1.2.3");
	}
	
	@Test public void versionCompare7() {
		versionCompare(1, "2.2.3", "1.2.3");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void versionCompare8() {
		versionCompare(0, "one.2.3", "1.two.3");
	}
	
	@Test public void versionCompare9() {
		assertEquals(0, 
			Util.versionCompare(1, 2, 3,   1, 2, 3));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void versionCompare10() {
		Util.versionCompare(1, 2, 3, 4,   1, 2, 3);
	}
}

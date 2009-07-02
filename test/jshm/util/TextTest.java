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

import jshm.*;

import org.junit.*;
import static org.junit.Assert.*;

/**
 * This test ensures that text keys have been defined for all 
 * enum or fake enum values.
 * @author Tim Mullin
 *
 */
public class TextTest {
	@Test public void difficulty() {
		String[] keys = {"longName"};
		
		for (Difficulty d : Difficulty.values()) {
			for (String key : keys) {
				String cur = d.getText(key);
				assertNotNull(cur);
				assertFalse(cur.isEmpty());
			}
		}
	}
	
	@Test public void gameSeries() {
		String[] keys = {"longName"};
		
		for (GameSeries d : GameSeries.values()) {
			for (String key : keys) {
				String cur = d.getText(key);
				assertNotNull(cur);
				assertFalse(cur.isEmpty());
			}
		}
	}
	
	@Test public void gameTitle() {
		String[] keys = {"longName", "wikiAbbr"};
		
		for (GameTitle d : GameTitle.values()) {
			for (String key : keys) {
				String cur = d.getText(key);
				assertNotNull(cur);
				assertFalse(cur.isEmpty());
			}
		}
	}
	
	@Test public void instrument() {
		String[] keys = {"longName"};
		
		for (Instrument d : Instrument.values()) {
			for (String key : keys) {
				String cur = d.getText(key);
				assertNotNull(cur);
				assertFalse(cur.isEmpty());
			}
		}
	}
	
	@Test public void instrumentGroup() {
		String[] keys = {"longName", "wikiUrl"};
		
		for (Instrument.Group d : Instrument.Group.values()) {
			for (String key : keys) {
				String cur = d.getText(key);
				assertNotNull(cur);
				assertFalse(cur.isEmpty());
			}
			
			// check WT names too
			
			String cur = d.getLongName(true);
			assertNotNull(cur);
			assertFalse(cur.isEmpty());
			
			cur = d.getWikiUrl(true);
			assertNotNull(cur);
			assertFalse(cur.isEmpty());
		}
	}
	
	@Test public void platform() {
		String[] keys = {"shortName"};
		
		for (Platform d : Platform.values()) {
			for (String key : keys) {
				String cur = d.getText(key);
				assertNotNull(cur);
				assertFalse(cur.isEmpty());
			}
		}
	}
}

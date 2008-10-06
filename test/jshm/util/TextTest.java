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
		String[] keys = {"longName", "wikiUrl"};
		
		for (Instrument.Group d : Instrument.Group.values()) {
			for (String key : keys) {
				String cur = d.getText(key);
				assertNotNull(cur);
				assertFalse(cur.isEmpty());
			}
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

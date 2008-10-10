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
package jshm.sh;

import org.junit.*;
import static org.junit.Assert.*;

public class WikiTest {
	public void wikiize(String in, String expected) {
		String out = Wiki.wikiize(in);
		assertEquals(expected, out);
	}
	
	@Test public void wikiize1() {
		wikiize("Guitar Hero Encore: Rocks the 80's",
			"GuitarHeroEncoreRocksThe80s");
	}
	
	@Test public void wikiize2() {
		wikiize("Guitar Hero III: Legends of Rock",
			"GuitarHeroIIILegendsOfRock");
	}
	
	@Test public void wikiize3() {
		wikiize("F.C.P.R.E.M.I.X.", "FCPREMIX");
	}
	
	@Test public void wikiize4() {
		wikiize("(F)lannigan's Ball", "FlannigansBall");
	}
	
	@Test public void wikiize5() {
		wikiize("Suicide & Redemption J.H.", "SuicideAndRedemptionJH");
	}
	
	@Test public void wikiize6() {
		wikiize("Yellow", "Yellow");
	}
	
	@Test public void wikiize7() {
		wikiize("3's & 7's", "3sAnd7s");
	}
	
	@Test public void wikiize8() {
		wikiize("Halo Theme MJOLNIR Mix", "HaloThemeMJOLNIRMix");
	}
	
	@Test public void wikiize9() {
		wikiize("Co-op", "Coop");
	}
	
	// TODO see how SH deals with the encoding issue
	@Test public void wikiize10() {
		wikiize("Ernten Was Wir Säen", "ErntenWasWirSäen");
	}
}

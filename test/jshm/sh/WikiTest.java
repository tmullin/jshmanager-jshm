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
	
	// TODO add test for Motörhead once SH works it out
}

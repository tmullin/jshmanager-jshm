package jshm.concepts;

// import org.apache.commons.httpclient.Cookie;
import org.htmlparser.util.*;

import jshm.Difficulty;
import jshm.gh.GhGame;
import jshm.sh.*;
import jshm.sh.scraper.GhScraper;

public class HttpClientLoginTest {	
	public static void main(String[] args) throws Exception {
		// Cookie[] cookies =
			Client.getAuthCookies(
				"someuser", "somepass", true);
		
		NodeList nodes = GhScraper.scrape(
			jshm.sh.URLs.gh.getManageScoresUrl(
				GhGame.GH3_XBOX360, Difficulty.EXPERT),
			false);
		
		System.out.println(nodes.toHtml());
	}
}

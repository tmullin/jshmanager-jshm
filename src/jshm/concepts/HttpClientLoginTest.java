package jshm.concepts;

// import org.apache.commons.httpclient.Cookie;
import org.htmlparser.util.*;

import jshm.sh.*;
import jshm.sh.gh.*;
import jshm.sh.gh.scraper.Scraper;

public class HttpClientLoginTest {	
	public static void main(String[] args) throws Exception {
		// Cookie[] cookies =
			Client.getAuthCookies(
				"someuser", "somepass", true);
		
		NodeList nodes = Scraper.scrape(
			jshm.sh.URLs.gh.getManageScoresUrl(
				GhGame.GH3_XBOX360, Difficulty.EXPERT),
			false);
		
		System.out.println(nodes.toHtml());
	}
}

package jshm.concepts;

/*
  ___  __  __  ____  __  ______       _   _       _     
 / _ \|  \/  |/ ___| \ \/ /  _ \ __ _| |_| |__   (_)___ 
| | | | |\/| | |  _   \  /| |_) / _` | __| '_ \  | / __|
| |_| | |  | | |_| |  /  \|  __/ (_| | |_| | | | | \__ \
 \___/|_|  |_|\____| /_/\_\_|   \__,_|\__|_| |_| |_|___/
                                                        
 ____  _     _____        ___ _ _ 
/ ___|| |   / _ \ \      / / | | |
\___ \| |  | | | \ \ /\ / /| | | |
 ___) | |__| |_| |\ V  V / |_|_|_|
|____/|_____\___/  \_/\_/  (_|_|_)

*/

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import jshm.Difficulty;
import jshm.gh.GhGame;
import jshm.gh.GhSong;
import jshm.sh.GhSongStat;
import jshm.sh.URLs;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;

@SuppressWarnings("unused")
public class TidyTest {
	public static void main(String[] args) throws Exception {
		jshm.util.TestTimer.start();
		
		final GhGame game = GhGame.GH3_XBOX360;
		final Difficulty difficulty = Difficulty.EXPERT;
		
		String url = URLs.gh.getSongStatsUrl(
			GhSongStat.ALL_CUTOFFS,
			game,
			difficulty);
		
		HttpClient client = new HttpClient();
		HttpMethod method = new GetMethod(url);
		client.executeMethod(method);		
		
		Tidy tidy = new Tidy();
		tidy.setXmlOut(true);
		tidy.setIndentContent(true);
		
		Document doc = tidy.parseDOM(method.getResponseBodyAsStream(), null);
		
		XPath xpath = XPathFactory.newInstance().newXPath();
		String expr =
			"//table[@border='1' and @cellspacing='0']/tr[@height='30' and not(@class='headrow')]";
//			"//table[@border='1' and @cellspacing='0']/tr[@height='30' and not(@class='headrow')]/td[not(@class='tier2')]//text()";
		NodeList nodes = (NodeList) xpath.evaluate(expr, doc, XPathConstants.NODESET);

		XPathExpression classExpr = xpath.compile("./td[1]/@class");
		XPathExpression textExpr = xpath.compile(".//text()");
		
		String curTierName = "UNKNOWN";
		int curTierLevel = 0;
		
		for (int i = 0; i < nodes.getLength(); i++) {
			Node cur = nodes.item(i);
			if ("tier1".equals(classExpr.evaluate(cur, XPathConstants.STRING))) {
				System.out.println("*** TIER NAME");
				curTierName = (String) xpath.evaluate("./td[1]//text()", cur, XPathConstants.STRING);
				curTierLevel++;
			} else {
				GhSong curSong = new GhSong();
				curSong.setGame(game);
				curSong.setDifficulty(difficulty);
				curSong.setTierLevel(curTierLevel);
				
				for (int j = 1; j <= 8; j++) {
					String s = (String) xpath.evaluate("./td[" + j + "]//text()", cur, XPathConstants.STRING);
					int k = -1;
					
					try {
						k = Integer.parseInt(s);
					} catch (NumberFormatException e) {}
					
	    			switch (j - 1) {
	    				case 0: curSong.setTitle(s); break;
	    				case 1: curSong.setBaseScore(k); break;
	    				case 2: curSong.setFourStarCutoff(k); break;
	    				case 3: curSong.setFiveStarCutoff(k); break;
	    				case 4: curSong.setSixStarCutoff(k); break;
	    				case 5: curSong.setSevenStarCutoff(k); break;
	    				case 6: curSong.setEightStarCutoff(k); break;
	    				case 7: curSong.setNineStarCutoff(k); break;
	    			}
				}
				
				System.out.println(curSong);
			}
//			System.out.println(textExpr.evaluate(cur, XPathConstants.STRING));
			
//			System.out.println(cur.getNodeValue());
		}
		
		jshm.util.TestTimer.stop();
	}
}

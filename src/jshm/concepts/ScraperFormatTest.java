package jshm.concepts;

import org.htmlparser.Node;
import org.htmlparser.tags.TableRow;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.SimpleNodeIterator;

import jshm.exceptions.ScraperException;
import jshm.sh.*;
import jshm.sh.gh.*;
import jshm.sh.gh.scraper.*;
import jshm.sh.scraper.format.*;

public class ScraperFormatTest {
	public static void main(String[] args) throws Exception {
		myScores();
	}
	
	static void myScores() throws Exception {
		TableRowFormat f1 = TableRowFormat.factory("text");
		TableRowFormat f2 = TableRowFormat.factory(
			"-|-|text|-|-|text=int|img=src~text=float|text=int|text=int|text|text");
		
		Game game = Game.GH3_XBOX360;
		Difficulty diff = Difficulty.EXPERT;
		
		Client.getAuthCookies("somuser", "somepass");
		NodeList nodes = Scraper.scrape(
			URLs.gh.getManageScoresUrl(
				game,
				diff));
		
		SimpleNodeIterator it = nodes.elements();
		
        // have to track the tier while we traverse the rows
        String curTierName = "";
    	int curTierLevel = 0;
    	int totalSongs = 0;
        
        while (it.hasMoreNodes()) {
        	Node node = it.nextNode();
        	
        	if (!(node instanceof TableRow)) {
        		throw new ScraperException("Expecting nodes to contain TableRows, got a " + node.getClass().getName());
        	}
        	
//        	System.out.println(node.getClass().getSimpleName() + "-> " + node.getText());
        	
        	TableRow tr = (TableRow) node;
        	String cssClass = tr.getAttribute("class");
        	
        	if (null != cssClass && "headrow".equals(cssClass)) {
        		// skip header row
//        		System.out.println("* Skip header row");
        		continue;
        	}
        	
        	if (tr.getChildCount() == 4) {
        		// should be tier name row
        		String[][] tierData = f1.getData(tr);
        		print(tierData);
        		
        		curTierName = tierData[0][0];
        		curTierLevel++;
        		
        		continue;
        	}
        	
    		String[][] songData = f2.getData(tr);
    		print(songData);        	
        	
        	totalSongs++;
        }
	}
	
	static void allStats() throws Exception {
//		TableRowFormat f1 = new TableRowFormat(
//			TableColumnFormat.PLAIN_TEXT
//		);
		TableRowFormat f1 = TableRowFormat.factory("text");
	
		// "text|text=int|tag=img-src ..."
		
		// for my score page
		// links|# scores|title|rank|rank|score|stars|percent|streak|date|comment
		// -|-|text|-|-|text=int|img=src~text=float|text=int|text=int|text=date|text
		
		// for 5* cutoff page
		// history|submission #|title|low - hi|offset|target|submitter
		// link=href|-|text|text=int,-,int|text=int|text=int|text

		TableRowFormat f2 = TableRowFormat.factory(
			"text|text=int|text=int|text=int|text=int|text=int|text=int|text=int");
		
		Game game = Game.GH3_XBOX360;
		Difficulty diff = Difficulty.EXPERT;
		
		NodeList nodes = Scraper.scrape(
			URLs.gh.getSongStatsUrl(
				SongStat.ALL_CUTOFFS,
				game,
				diff));
			
		SimpleNodeIterator it = nodes.elements();
		
        // have to track the tier while we traverse the rows
        String curTierName = "";
    	int curTierLevel = 0;
    	int totalSongs = 0;
        
        while (it.hasMoreNodes()) {
        	Node node = it.nextNode();
        	
        	if (!(node instanceof TableRow)) {
        		throw new ScraperException("Expecting nodes to contain TableRows, got a " + node.getClass().getName());
        	}
        	
//        	System.out.println(node.getClass().getSimpleName() + "-> " + node.getText());
        	
        	TableRow tr = (TableRow) node;
        	String cssClass = tr.getAttribute("class");
        	
        	if (null != cssClass && "headrow".equals(cssClass)) {
        		// skip header row
//        		System.out.println("* Skip header row");
        		continue;
        	}
        	
        	if (tr.getChildCount() == 2) {
        		// should be tier name row
        		String[][] tierData = f1.getData(tr);
        		// print(tierData);
        		
        		curTierName = tierData[0][0];
        		curTierLevel++;
        		
        		continue;
        	}
        	
    		String[][] songData = f2.getData(tr);
    		// print(songData);
    		
        	Song curSong = new Song();
        	curSong.game = game;
        	curSong.difficulty = diff;
        	curSong.tierLevel = curTierLevel;
        	curSong.tierName = curTierName;
        	
        	for (int i = 0; i < 8; i++) {
        		int k = 0;
        		
        		try {
        			k = Integer.parseInt(songData[i][0]);
        		} catch (NumberFormatException e) {}
        		
        		switch (i) {
        			case 0: curSong.title = songData[i][0]; break;
        			case 1: curSong.baseScore = k; break;
        			case 2: curSong.fourStarCutoff = k; break;
        			case 3: curSong.fiveStarCutoff = k; break;
        			case 4: curSong.sixStarCutoff = k; break;
        			case 5: curSong.sevenStarCutoff = k; break;
        			case 6: curSong.eightStarCutoff = k; break;
        			case 7: curSong.nineStarCutoff = k; break;
        		}
        	}
        	
        	System.out.println(curSong);
        	totalSongs++;
        }
	}
	
	private static void print(String[][] data) {
		for (String[] cur : data) {
			for (String s : cur) {
				System.out.print(s);
				System.out.print(',');
			}
			
			if (cur.length > 0)
				System.out.println();
		}
	}
}

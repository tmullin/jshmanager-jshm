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
package jshm.concepts;

import org.htmlparser.Node;
import org.htmlparser.tags.TableRow;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.SimpleNodeIterator;

import jshm.Difficulty;
import jshm.exceptions.ScraperException;
import jshm.gh.GhGame;
import jshm.gh.GhSong;
import jshm.scraper.format.*;
import jshm.sh.*;
import jshm.sh.scraper.GhScraper;

public class ScraperFormatTest {
	public static void main(String[] args) throws Exception {
		myScores();
	}
	
	static void myScores() throws Exception {
		TableRowFormat f1 = TableRowFormat.factory("text");
		TableRowFormat f2 = TableRowFormat.factory(
			"-|-|text|-|-|text=int|img=rating~text=float|text=int|text=int|text|text");
		
		GhGame game = GhGame.GH3_XBOX360;
		Difficulty diff = Difficulty.EXPERT;
		
		Client.getAuthCookies("someuser", "somepass");
		NodeList nodes = GhScraper.scrape(
			URLs.gh.getManageScoresUrl(
				game,
				diff));
		
		SimpleNodeIterator it = nodes.elements();
		
        // have to track the tier while we traverse the rows
//        String curTierName = "";
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
        		
//        		curTierName = tierData[0][0];
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
//		TableRowFormat f1 = TableRowFormat.factory("text");
	
		// "text|text=int|tag=img-src ..."
		
		// for my score page
		// links|# scores|title|rank|rank|score|stars|percent|streak|date|comment
		// -|-|text|-|-|text=int|img=src~text=float|text=int|text=int|text=date|text
		
		// for 5* cutoff page
		// history|submission #|title|low - hi|offset|target|submitter
		// link=href|-|text|text=int,-,int|text=int|text=int|text

		TableRowFormat f2 = TableRowFormat.factory(
			"text|text=int|text=int|text=int|text=int|text=int|text=int|text=int");
		
		GhGame game = GhGame.GH3_XBOX360;
		Difficulty diff = Difficulty.EXPERT;
		
		NodeList nodes = GhScraper.scrape(
			URLs.gh.getSongStatsUrl(
				GhSongStat.ALL_CUTOFFS,
				game,
				diff));
			
		SimpleNodeIterator it = nodes.elements();
		
        // have to track the tier while we traverse the rows
//        String curTierName = "";
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
//        		String[][] tierData = f1.getData(tr);
        		// print(tierData);
        		
//        		curTierName = tierData[0][0];
        		curTierLevel++;
        		
        		continue;
        	}
        	
    		String[][] songData = f2.getData(tr);
    		// print(songData);
    		
        	GhSong curSong = new GhSong();
        	curSong.setGame(game);
        	curSong.setDifficulty(diff);
        	curSong.setTierLevel(curTierLevel);
        	
        	for (int i = 0; i < 8; i++) {
        		int k = 0;
        		
        		try {
        			k = Integer.parseInt(songData[i][0]);
        		} catch (NumberFormatException e) {}
        		
        		switch (i) {
        			case 0: curSong.setTitle(songData[i][0]); break;
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

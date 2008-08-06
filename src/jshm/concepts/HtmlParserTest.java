package jshm.concepts;

import org.htmlparser.*;
import org.htmlparser.nodes.*;
import org.htmlparser.tags.*;
import org.htmlparser.util.*;

import jshm.exceptions.*;
import jshm.sh.gh.GhSong;
import jshm.sh.gh.scraper.Scraper;

public class HtmlParserTest {
	static final String GH3X_CUTOFFS = "http://www.scorehero.com/songstats.php?stat=4&group=4&game=6&diff=4";
	static final String GH3X_TOTAL_NOTES = "http://www.scorehero.com/songstats.php?stat=3&group=4&game=6&diff=4";
	
	public static void main(String[] args) throws Exception {
		NodeList nodes = Scraper.scrape(GH3X_CUTOFFS);
		
//		NodeList nodes = GhScraper.scrape(GH3X_TOTAL_NOTES, false);
//        System.out.println(nodes.toHtml());
//        System.exit(0);
        
        SimpleNodeIterator it = nodes.elements();
        
        // have to track the tier while we traverse the rows
//        String curTierName = "";
    	int curTierLevel = 0;
    	int totalSongs = 0;
        
outerRowLoop:
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
        	
        	int curCol = 0;
        	boolean expectTierName = false;
        	GhSong curSong = new GhSong();
        	curSong.setTierLevel(curTierLevel);
        	
        	NodeTreeWalker walker = new NodeTreeWalker(node);
        	
        	while (walker.hasMoreNodes()) {
        		node = walker.nextNode();
        		
        		// declutter the tag of stuff we don't care about
	        	if (node instanceof Tag) {
	        		Tag tag = (Tag) node;
	        		
	        		tag.removeAttribute("height");
	        		tag.removeAttribute("width");
	        		tag.removeAttribute("align");
	        		tag.removeAttribute("style");
	        	}
        		
//	        	String padding = String.format("%" + Integer.toString(2 * walker.getCurrentNodeDepth()) + "s", "");
//	        	System.out.println(padding + node.getClass().getSimpleName() + "--> " + node.getText());
        		
        		if (expectTierName && !(node instanceof TextNode)) {
        			throw new ScraperException("Expecting TextNode for tierName but got a " + node.getClass().getName());
        		}
	        	
	        	if (node instanceof TableColumn) {	        		
	        		TableColumn tc = (TableColumn) node;
	        		
	        		String colspan = tc.getAttribute("colspan");
	        		cssClass = tc.getAttribute("class");
	        		
	        		if (null != colspan && 3 == Integer.parseInt(colspan) &&
	        			null != cssClass && "tier1".equals(cssClass)) {
	        			System.out.println("*** Tier Header Row");
	        			expectTierName = true;
	        		} else {
	        			// it should be song data now
	        			
	        			node = walker.nextNode();
	        			
	        			if (node instanceof Span) {
	        				// probaly a red ??? so chomp this span and the TextNode will be next
	        				node = walker.nextNode();
	        				
	        				if (node instanceof TagNode) {
	        					// chomp the b tag
	        					node = walker.nextNode(); // this should be the text node
	        					
	        					// now chomp the closing b tag
	        					walker.nextNode();
	        				}
	        			}
	        			
	        			if (!(node instanceof TextNode)) {
	        				throw new ScraperException("Expecting a TextNode for song data but got a " + node.getClass().getName());
	        			}
	        			
	        			TextNode tn = (TextNode) node;
	        			// need to decode any html entities
	        			String s = Translate.decode(tn.getText());
	        			int i = 0;
	        			
	        			try {
	        				i = Integer.parseInt(s.replaceAll("[^\\d]+", ""));
	        			} catch (NumberFormatException e) {}
	        			
	        			switch (++curCol) {
	        				case 1: curSong.setTitle(s); break;
	        				case 2: curSong.setBaseScore(i); break;
	        				case 3: curSong.setFourStarCutoff(i); break;
	        				case 4: curSong.setFiveStarCutoff(i); break;
	        				case 5: curSong.setSixStarCutoff(i); break;
	        				case 6: curSong.setSevenStarCutoff(i); break;
	        				case 7: curSong.setEightStarCutoff(i); break;
	        				case 8: curSong.setNineStarCutoff(i); break;
	        				
	        				default:
	        					throw new ScraperException("Invalid curCol value: " + curCol);
	        			}
	        		}
	        	} else if (node instanceof TextNode) {
	        		TextNode tn = (TextNode) node;
//	        		 need to decode any html entities
	        		tn.setText(Translate.decode(tn.getText()));
	        		
	        		if (expectTierName) {
//	        			curTierName = tn.getText();
	        			curTierLevel++;
	        			expectTierName = false;
	        			
	        			curSong.setTierLevel(curTierLevel);
	        			
	        			// get rid of the next td and text node
	        			try {
	        				// first the td
	        				node = walker.nextNode();
	        				
	        				if (!(node instanceof TableColumn)) {
	        					throw new ScraperException("Expecting to chomp a TableColumn but got a " + node.getClass().getName());
	        				}
	        				
	        				TableColumn tc = (TableColumn) node;
	        				
	        				String colspan = tc.getAttribute("colspan");
	        				cssClass = tc.getAttribute("class");
	        				
	        				if (!(null != colspan && 5 == Integer.parseInt(colspan) &&
	        					  null != cssClass && "tier2".equals(cssClass))) {
	        					throw new ScraperException("Expecting a TableColumn with colspan=5 and class=tier2");
	        				}
	        				
	        				
	        				// now the text node
	        				node = walker.nextNode();
	        				
	        				if (!(node instanceof TextNode)) {
	        					throw new ScraperException("Expecting to chomp a TextNode but got a " + node.getClass().getName());
	        				}
	        				
	        				// don't care about the content of the text node, just that it was a text node
	        			} catch (Exception e) {
	        				throw new ScraperException("Caught exception while chomping tier header row", e);
	        			}
	        			
	        			continue outerRowLoop; // don't pretend we got a song from the header row
	        			// never had to use a label in java before :p
	        		}
	        	} else {
	        		throw new ScraperException("Unexpected node type, got a " + node.getClass().getName());
	        	}
        	}
        	
        	curSong.setOrder(++totalSongs);
        	
        	System.out.println("SONG: " + curSong);
        }
	}
}

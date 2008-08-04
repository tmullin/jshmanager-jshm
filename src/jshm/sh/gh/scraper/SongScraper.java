package jshm.sh.gh.scraper;

import java.util.*;
import java.util.regex.*;

import org.htmlparser.*;
import org.htmlparser.filters.*;
import org.htmlparser.nodes.*;
import org.htmlparser.tags.*;

import org.htmlparser.util.*;

import jshm.exceptions.*;
import jshm.sh.*;
import jshm.sh.gh.*;
import jshm.sh.gh.scraper.Scraper;

/**
 * This class serves to scrape all necessary info from
 * ScoreHero to build complete GhSong objects.
 * @author Tim Mullin
 *
 */
public class SongScraper {
	public static List<Song> scrape(
		final Game game, final Difficulty difficulty)
	throws ScraperException {
		List<Song> songs = new ArrayList<Song>();
		getIdsAndNotes(songs, game, difficulty);
		getCutoffs(songs, game, difficulty);
		return songs;
	}
	
	private static void getIdsAndNotes(
		final List<Song> songs, final Game game, final Difficulty difficulty)
	throws ScraperException {
		NodeList nodes = Scraper.scrape(
			URLs.gh.getSongStatsUrl(
				SongStat.TOTAL_NOTES,
				game,
				difficulty)/*, false*/);
		
		//System.out.println(nodes.toHtml());
		//System.exit(0);
		
        SimpleNodeIterator it = nodes.elements();
        
        // have to track the tier while we traverse the rows
        String curTierName = "";
    	int curTierLevel = 0;
    	int totalSongs = 0;
        
		final Pattern ID_FINDER_PATTERN =
			Pattern.compile("^proof_history\\.php\\?stat=\\d+&song=(\\d+)$");
    	
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
        	Song curSong = new Song();
        	curSong.game = game;
        	curSong.difficulty = difficulty;
        	curSong.tierName = curTierName;
        	curSong.tierLevel = curTierLevel;
        	
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
	        		++curCol;
	        		TableColumn tc = (TableColumn) node;
	        		
	        		String colspan = tc.getAttribute("colspan");
	        		cssClass = tc.getAttribute("class");
	        		
	        		if (null != colspan && 3 == Integer.parseInt(colspan) &&
	        			null != cssClass && "tier1".equals(cssClass)) {
//	        			System.out.println("*** Tier Header Row");
	        			expectTierName = true;
	        		} else {
	        			// here we want to manually traverse the nodes so we'll
	        			// remove the children but save a reference to them
	        			
	        			NodeList colChildren = node.getChildren();
	        			node.setChildren(null);
	        			
	        			switch (curCol) {
	        				case 1: // extract song id from link
	        					//System.out.println(colChildren.toHtml());
	        					//System.exit(0);
	        					
	        					NodeList linkNodes =
	        						colChildren.extractAllNodesThatMatch(
	        							new TagNameFilter("A"), true);
	        					
	        					if (linkNodes.size() != 1) {
	        						throw new ScraperException("Expecting to find 1 A tag but found " + linkNodes.size());
	        					}
	        					
	        					if (!(linkNodes.elementAt(0) instanceof LinkTag)) {
	        						throw new ScraperException("Expecting a LinkTag but got a " + linkNodes.elementAt(0).getClass().getName());
	        					}
	        					
	        					LinkTag link = (LinkTag) linkNodes.elementAt(0);
	        					String href = link.getAttribute("href");
	        					
	        					if (null == href) {
	        						throw new ScraperException("Got a null href value");
	        					}
	        					
	        					// finally extract the song's id
	        					Matcher m = ID_FINDER_PATTERN.matcher(href);
	        					
	        					if (!m.matches()) {
	        						throw new ScraperException("The found href didn't match the regex, got \"" + href + "\"");
	        					}
	        					
	        					curSong.id = Integer.parseInt(m.group(1));
//	        					System.out.println("Found id: " + curSong.id);
//	        					System.exit(0);
	        					break;
	        					
	        				case 3: // song title
	        					if (colChildren.size() != 1) {
	        						throw new ScraperException("Expecting only 1 child, found " + colChildren.size());
	        					}
	        					
	        					if (!(colChildren.elementAt(0) instanceof TextNode)) {
	        						throw new ScraperException("Expecting TextNode, got " + colChildren.elementAt(0).getClass().getName());
	        					}
	        					
	        					curSong.title = Translate.decode(
	        						colChildren.elementAt(0).getText());
	        					break;
	        					
	        				// TODO Fix to account for a link being there (GH1)
	        				case 4: // total notes
	        					if (colChildren.size() != 1) {
	        						throw new ScraperException("Expecting only 1 child, found " + colChildren.size());
	        					}
	        					
	        					if (colChildren.elementAt(0) instanceof TextNode) {
	        						try {
		             					curSong.noteCount = 
		             						Integer.parseInt(
		             							colChildren.elementAt(0).getText().replaceAll("[^\\d]+", ""));
//		             					System.out.println("Got noteCount: " + curSong.noteCount);
//		             					System.exit(0);
	        						} catch (NumberFormatException e) {
	        							throw new ScraperException("Expecting a number, got \"" + colChildren.elementAt(0).getText() + "\"", e);
	        						}
	        					} else {
	        						// probably got ???
	        						curSong.noteCount = 0;
	        					}
	        					
	        					break;
	        					
	        				default:
	    	        			// ignore the other columns	        				
	        					break;
	        			}
	        		}
	        	} else if (node instanceof TextNode) {
	        		TextNode tn = (TextNode) node;
//	        		 need to decode any html entities
	        		tn.setText(Translate.decode(tn.getText()));
	        		
	        		// TODO implement a max number of tiers
	        		// (to ignore GH3 demo songs on GH2 page)
	        		if (expectTierName) {
	        			curTierName = tn.getText();
	        			curTierLevel++;
	        			expectTierName = false;
	        			
	        			curSong.tierName = curTierName;
	        			curSong.tierLevel = curTierLevel;
	        			
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
	        				
	        				if (!(null != colspan && 3 == Integer.parseInt(colspan) &&
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
        	
        	curSong.order = ++totalSongs;
        	
        	songs.add(curSong);
        }
	}
	
	private static void getCutoffs(
		final List<Song> songs, final Game game, final Difficulty difficulty)
	throws ScraperException {
		NodeList nodes = Scraper.scrape(
			URLs.gh.getSongStatsUrl(
				SongStat.ALL_CUTOFFS,
				game,
				difficulty));
		
        SimpleNodeIterator it = nodes.elements();
        
        // have to track the tier while we traverse the rows
        String curTierName = "";
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
        	Song curSong = new Song();
        	curSong.game = game;
        	curSong.difficulty = difficulty;
        	curSong.tierName = curTierName;
        	curSong.tierLevel = curTierLevel;
        	
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
//	        			System.out.println("*** Tier Header Row");
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
	        				case 1: curSong.title = s; break;
	        				case 2: curSong.baseScore = i; break;
	        				case 3: curSong.fourStarCutoff = i; break;
	        				case 4: curSong.fiveStarCutoff = i; break;
	        				case 5: curSong.sixStarCutoff = i; break;
	        				case 6: curSong.sevenStarCutoff = i; break;
	        				case 7: curSong.eightStarCutoff = i; break;
	        				case 8: curSong.nineStarCutoff = i; break;
	        				
	        				default:
	        					throw new ScraperException("Invalid curCol value: " + curCol);
	        			}
	        		}
	        	} else if (node instanceof TextNode) {
	        		TextNode tn = (TextNode) node;
//	        		 need to decode any html entities
	        		tn.setText(Translate.decode(tn.getText()));
	        		
	        		if (expectTierName) {
	        			curTierName = tn.getText();
	        			curTierLevel++;
	        			expectTierName = false;
	        			
	        			curSong.tierName = curTierName;
	        			curSong.tierLevel = curTierLevel;
	        			
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
        	
        	try {
        		songs.get(totalSongs).setScoreAndCutoffs(curSong);
        	} catch (Exception e) {
        		throw new ScraperException("Song list is out of sync", e);
        	}
        	
        	++totalSongs;
        }
	}
}

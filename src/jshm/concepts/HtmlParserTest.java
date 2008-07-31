package jshm.concepts;

import org.htmlparser.*;
import org.htmlparser.nodes.*;
import org.htmlparser.filters.*;
import org.htmlparser.beans.*;
import org.htmlparser.tags.*;
import org.htmlparser.util.*;

import jshm.sh.Song;

public class HtmlParserTest {
	static final String GH3X_CUTOFFS = "http://www.scorehero.com/songstats.php?stat=4&group=4&game=6&diff=4";
	
	public static void main(String[] args) {
		// HtmlParser auto-generated filter stuff {{{
		NodeFilter[] array2 = null;
		
		{
	        TagNameFilter filter0 = new TagNameFilter ();
	        filter0.setName ("TABLE");
	        HasAttributeFilter filter1 = new HasAttributeFilter ();
	        filter1.setAttributeName ("border");
	        filter1.setAttributeValue ("1");
	        HasAttributeFilter filter2 = new HasAttributeFilter ();
	        filter2.setAttributeName ("cellspacing");
	        filter2.setAttributeValue ("0");
	        NodeFilter[] array0 = new NodeFilter[3];
	        array0[0] = filter0;
	        array0[1] = filter1;
	        array0[2] = filter2;
	        AndFilter filter3 = new AndFilter ();
	        filter3.setPredicates (array0);
	        HasParentFilter filter4 = new HasParentFilter ();
	        filter4.setRecursive (false);
	        filter4.setParentFilter (filter3);
	        TagNameFilter filter5 = new TagNameFilter ();
	        filter5.setName ("TR");
	        HasAttributeFilter filter6 = new HasAttributeFilter ();
	        filter6.setAttributeName ("height");
	        filter6.setAttributeValue ("30");
	        NodeFilter[] array1 = new NodeFilter[3];
	        array1[0] = filter4;
	        array1[1] = filter5;
	        array1[2] = filter6;
	        AndFilter filter7 = new AndFilter ();
	        filter7.setPredicates (array1);
	        
	        array2 = new NodeFilter[1];
	        array2[0] = filter7;
		}
		
        FilterBean bean = new FilterBean ();
        bean.setFilters (array2);
        
        // }}}

        bean.setURL(GH3X_CUTOFFS);
        
        
        // now filter out text nodes that only have whitespace {{{
        RegexFilter filter8 = new RegexFilter ();
        filter8.setStrategy (RegexFilter.MATCH);
        filter8.setPattern ("^\\s*$");
        NodeClassFilter filter9 = new NodeClassFilter(TextNode.class);
        AndFilter filter10 = new AndFilter(new NodeFilter[]{
        	filter9, filter8
        });
        NotFilter filter11 = new NotFilter(filter10);

        NodeList nodes = bean.getNodes();
        nodes.keepAllNodesThatMatch(filter11, true);
        
        // }}}
        
        
        SimpleNodeIterator it = nodes.elements();
        
        // have to track the tier while we traverse the rows
        String curTierName = "";
    	int curTierLevel = 0;
        
outerRowLoop:
        while (it.hasMoreNodes()) {
        	Node node = it.nextNode();
        	
        	if (!(node instanceof TableRow)) {
        		throw new RuntimeException("Expecting nodes to contain TableRows, got a " + node.getClass().getName());
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
        			throw new RuntimeException("Expecting TextNode for tierName but got a " + node.getClass().getName());
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
	        				throw new RuntimeException("Expecting a TextNode for song data but got a " + node.getClass().getName());
	        			}
	        			
	        			TextNode tn = (TextNode) node;
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
	        					throw new RuntimeException("Invalid curCol value: " + curCol);
	        			}
	        		}
	        	} else if (node instanceof TextNode) {
	        		TextNode tn = (TextNode) node;
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
	        					throw new RuntimeException("Expecting to chomp a TableColumn but got a " + node.getClass().getName());
	        				}
	        				
	        				TableColumn tc = (TableColumn) node;
	        				
	        				String colspan = tc.getAttribute("colspan");
	        				cssClass = tc.getAttribute("class");
	        				
	        				if (!(null != colspan && 5 == Integer.parseInt(colspan) &&
	        					  null != cssClass && "tier2".equals(cssClass))) {
	        					throw new RuntimeException("Expecting a TableColumn with colspan=5 and class=tier2");
	        				}
	        				
	        				
	        				// now the text node
	        				node = walker.nextNode();
	        				
	        				if (!(node instanceof TextNode)) {
	        					throw new RuntimeException("Expecting to chomp a TextNode but got a " + node.getClass().getName());
	        				}
	        				
	        				// don't care about the content of the text node, just that it was a text node
	        			} catch (Exception e) {
	        				throw new RuntimeException("Caught exception while chomping tier header row", e);
	        			}
	        			
	        			continue outerRowLoop; // don't pretend we got a song from the header row
	        			// never had to use a label in java before :p
	        		}
	        	} else {
	        		throw new RuntimeException("Unexpected node type, got a " + node.getClass().getName());
	        	}
        	}
        	
        	System.out.println("SONG: " + curSong);
        }
	}
}

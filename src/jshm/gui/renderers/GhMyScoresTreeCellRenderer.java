package jshm.gui.renderers;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import jshm.Score;
import jshm.gh.GhScore;
import jshm.gh.GhSong;

public class GhMyScoresTreeCellRenderer extends DefaultTreeCellRenderer {
    public Component getTreeCellRendererComponent(JTree tree, Object value,
			  boolean sel,
			  boolean expanded,
			  boolean leaf, int row,
			  boolean hasFocus) {
    	
    	if (value instanceof GhScore) {
    		GhScore score = (GhScore) value;
    		
    		if (score.getStatus() == Score.Status.TEMPLATE) {
    			value = "Double click to edit...";
    		} else {
	    		value = score.getComment().isEmpty()
	    			? "No Comment" : score.getComment();
    		}
    	} else if (value instanceof GhSong) {
    		value = ((GhSong) value).getTitle();
    	}
    	
    	return super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
    }
}

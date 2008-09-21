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
package jshm.gui.renderers;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import jshm.Score;
import jshm.Song;

public class GhMyScoresTreeCellRenderer extends DefaultTreeCellRenderer {
    public Component getTreeCellRendererComponent(JTree tree, Object value,
			  boolean sel,
			  boolean expanded,
			  boolean leaf, int row,
			  boolean hasFocus) {
    	
    	if (value instanceof Score) {
    		Score score = (Score) value;
    		
    		if (score.getStatus() == Score.Status.TEMPLATE) {
    			value = "Double click to edit...";
    		} else {
	    		value = score.getComment().isEmpty()
	    			? "No Comment" : score.getComment();
    		}
    	} else if (value instanceof Song) {
    		value = ((Song) value).getTitle();
    	}
    	
    	return super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
    }
}

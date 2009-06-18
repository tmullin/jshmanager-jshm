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

import javax.swing.Icon;
import javax.swing.ImageIcon;

import jshm.Score;
import jshm.Song;
import jshm.gui.datamodels.ScoresTreeTableModel;

import org.jdesktop.swingx.renderer.DefaultTreeRenderer;
import org.jdesktop.swingx.renderer.IconValue;
import org.jdesktop.swingx.renderer.StringValue;

public class ScoresTreeCellRenderer extends DefaultTreeRenderer {
	public ScoresTreeCellRenderer() {
		super(new IconValue() {
			@Override public Icon getIcon(final Object value) {
//				System.out.println("ICON " + value.getClass().getName() + ": " + value);
				Song song = null;
				
				if (value instanceof Song) {
					song = (Song) value;
				} else if (value instanceof ScoresTreeTableModel.SongScores) {
					song = ((ScoresTreeTableModel.SongScores) value).song;
				}
		    	
		    	if (null != song) {
		    		ImageIcon icon = song.getSongSourceIcon();
		    		
		    		if (null != icon) {
		    			return icon;
		    		}
		    	}
		    	
				return null;
			}
		}, new StringValue() {
			@Override public String getString(Object value) {
				String ret = "";
				
		    	if (value instanceof Score) {
		    		Score score = (Score) value;
		    		
		    		if (score.getStatus() == Score.Status.TEMPLATE) {
		    			ret = "Double click to edit...";
		    		} else {
			    		ret = score.getComment().isEmpty()
			    			? "No Comment" : score.getComment();
		    		}
		    	} else if (value instanceof Song) {
		    		ret = ((Song) value).getTitle();
		    	} else {
		    		ret = value.toString();
		    	}
		    	
				return ret;
			}
		});
	}
}

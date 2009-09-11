/*
 * -----LICENSE START-----
 * JSHManager - A Java-based tool for managing one's ScoreHero account.
 * Copyright (C) 2008, 2009 Tim Mullin
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
package jshm.gui.wizards.scoredownload;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import jshm.Difficulty;
import jshm.Instrument;
import jshm.Platform;

public class MyListCellRenderer extends DefaultListCellRenderer {
	boolean wtVersion;
	
	public MyListCellRenderer(boolean wtVersion) {
		this.wtVersion = wtVersion;
	}
	
    public Component getListCellRendererComponent(
            JList list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {
    	
    	if (value instanceof Platform) {
    		value = ((Platform) value).getShortName();
    	} else if (value instanceof Difficulty) {
    		value = ((Difficulty) value).getLongName();
    	} else if (value instanceof Instrument.Group) {
    		value = ((Instrument.Group) value).getLongName(wtVersion);
    	}
    	
    	return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
    }
}

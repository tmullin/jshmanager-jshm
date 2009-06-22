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

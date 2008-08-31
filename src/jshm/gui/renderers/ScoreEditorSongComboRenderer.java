package jshm.gui.renderers;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import jshm.gh.GhSong;

public class ScoreEditorSongComboRenderer extends DefaultListCellRenderer {
    public Component getListCellRendererComponent(
            JList list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {

    	if (value instanceof GhSong)
    		value = ((GhSong) value).getTitle();
    	
    	return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
    }
}

package jshm.gui.editors;

import java.awt.Component;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;

import jshm.gh.GhScore;

public class GhMyScoresRatingEditor extends DefaultCellEditor {			
	public GhMyScoresRatingEditor() {
		super(
			new JComboBox(
				new Object[] {
					GhScore.getRatingIcon(0),
					GhScore.getRatingIcon(5),
					GhScore.getRatingIcon(4),
					GhScore.getRatingIcon(3)
				}
			));
	}
	
    public Component getTableCellEditorComponent(JTable table, Object value,
			 boolean isSelected,
			 int row, int column) {
    	
		return super.getTableCellEditorComponent(
			table, ((GhScore) value).getRatingIcon(),
			isSelected, row, column);
	}
}

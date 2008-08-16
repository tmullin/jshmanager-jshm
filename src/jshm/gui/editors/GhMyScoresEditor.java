package jshm.gui.editors;

import java.awt.Component;

import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;

import jshm.gh.GhScore;

public class GhMyScoresEditor extends DefaultCellEditor {
	public GhMyScoresEditor() {
		super(new JTextField());
	}
	
    public Component getTableCellEditorComponent(JTable table, Object value,
			 boolean isSelected,
			 int row, int column) {
   	
    	System.out.println("edt " + column + ": " + value);
    	
    	if (value instanceof GhScore) { 
    		GhScore score = (GhScore) value;
    		
    		switch (column) {
    			case 0: value = score.getComment(); break;
    			case 1: value = score.getScore(); break;
    			case 3: value = score.getPart(1).getHitPercent(); break;
    			case 4: value = score.getPart(1).getStreak(); break;
    		}
    		
    		System.out.println("edt " + column + " (new value): " + value);
    	}
    	
		return super.getTableCellEditorComponent(
			table, value,
			isSelected, row, column);
	}
}

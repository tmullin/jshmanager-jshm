package jshm.gui.editors;

import java.awt.Component;

import javax.swing.DefaultCellEditor;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTextField;

import jshm.gh.GhScore;

public class GhMyScoresEditor extends DefaultCellEditor {
	private static final InputVerifier
		SCORE_VERIFIER = new InputVerifier() {
			@Override
			public boolean verify(JComponent input) {
				try {
					int i = Integer.parseInt(
						((JTextField) input).getText());
					
					return i > 0;
				} catch (NumberFormatException e) {}
				
				return false;
			}
		},
		
		PERCENT_VERIFIER = new InputVerifier() {
			@Override
			public boolean verify(JComponent input) {
				try {
					String s = ((JTextField) input).getText();
					if (s.isEmpty()) return true;
					int i = Integer.parseInt(s);
					return 0 <= i && i <= 100;
				} catch (NumberFormatException e) {}
				
				return false;
			}
		},
		
		STREAK_VERIFIER = new InputVerifier() {
			@Override
			public boolean verify(JComponent input) {
				try {
					String s = ((JTextField) input).getText();
					if (s.isEmpty()) return true;
					int i = Integer.parseInt(s);
					return 0 <= i;
				} catch (NumberFormatException e) {}
				
				return false;
			}
		};
		
	public GhMyScoresEditor() {
		super(new JTextField());
	}
	
    public Component getTableCellEditorComponent(JTable table, Object value,
			 boolean isSelected,
			 int row, int column) {
   	
    	System.out.println("edt " + column + ": " + value);
    	
    	InputVerifier iv = null;
    	
    	if (value instanceof GhScore) { 
    		GhScore score = (GhScore) value;
    		
    		switch (column) {
    			case 0: value = score.getComment(); break;
    			case 1:
    				value = score.getScore() > 0
    					? score.getScore() : "";
    				iv = SCORE_VERIFIER;
    				break;
    				
    			case 3:
    				value = score.getPart(1).getHitPercent() != 0f
    					? score.getPart(1).getHitPercent() : "";
    				iv = PERCENT_VERIFIER;
    				break;
    				
    			case 4:
    				value = score.getPart(1).getStreak() > 0
    					? score.getPart(1).getStreak() : "";
    				iv = STREAK_VERIFIER;
    				break;
    		}
    		
    		System.out.println("edt " + column + " (new value): " + value);
    	}
    	
		Component comp = super.getTableCellEditorComponent(
			table, value,
			isSelected, row, column);
		
		if (null != iv) {
			((JComponent) comp).setInputVerifier(iv);
		}
		
		return comp;
	}
}

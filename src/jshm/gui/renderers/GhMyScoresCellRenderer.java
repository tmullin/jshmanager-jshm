package jshm.gui.renderers;

import java.awt.Component;
import java.text.DecimalFormat;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import jshm.gh.GhScore;

public class GhMyScoresCellRenderer extends DefaultTableCellRenderer {
	private final DecimalFormat NUM_FMT = new DecimalFormat("#,###");
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {

    	// ensure we do what is normally done to render a cell first
    	super.getTableCellRendererComponent(
    		table, value, isSelected, hasFocus, row, column);
    	
		switch (column) {
			case 5:
				if (value instanceof GhScore) {
					GhScore score = (GhScore) value;
					
					if (score.getStreak() > 0)
						setText(NUM_FMT.format(score.getStreak()));
					else
						setText("");
				}
				break;
		}
		
		return this;
	}

}

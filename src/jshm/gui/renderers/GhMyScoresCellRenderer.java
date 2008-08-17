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

//		System.out.println(row + "x" + column + ": " + value);
    	setIcon(null);
    	    	    	
    	super.getTableCellRendererComponent(
       		table, value, isSelected, hasFocus, row, column);
    	
    	if (value instanceof GhScore) {
    		GhScore score = (GhScore) value;
    	
			switch (column) {
				case 0:
//					System.out.println(value.getClass().getName() + ": " + value);
					String s = score.getComment();
						
					/*if (s.isEmpty()) {
						setText("No Comment");
					} else*/ if (s.length() > 40) {
						setText(s.substring(0, 40) + "...");
					} else {
						setText(s);
					}
					break;
				
				case 1:
					if (score.getScore() > 0)
						setText(NUM_FMT.format(score.getScore()));
					else
						setText("");
					break;
					
				case 2:
					setIcon(score.getRatingIcon());
					setText(
						score.getCalculatedRating() != 0.0f
						? String.format("(%01.1f)", score.getCalculatedRating())
						: "");
					break;
				
				case 3:
					setText(
						score.getHitPercent() != 0.0f
						? String.valueOf((int) (score.getHitPercent() * 100))
						: "");
					break;
					
				case 4:
					if (score.getStreak() > 0)
						setText(NUM_FMT.format(score.getStreak()));
					else
						setText("");
					break;
			}
    	}
		
		return this;
	}

}

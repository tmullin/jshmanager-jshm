package jshm.gui.renderers;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import jshm.gh.GhScore;

import org.jdesktop.swingx.decorator.AbstractHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.HighlightPredicate;

public class GhMyScoresFcHighlighter extends AbstractHighlighter {	
	public GhMyScoresFcHighlighter() {
		super(new HighlightPredicate() {
			@Override
			public boolean isHighlighted(Component renderer,
					ComponentAdapter adapter) {	
				
				if (!(adapter.getValueAt(adapter.row, 1) instanceof GhScore)) return false;
				
				GhScore score = (GhScore) adapter.getValueAt(adapter.row, 1);
				
				return 1f == score.getHitPercent() || score.isFullCombo();
				
//				switch (adapter.column) {
//					case 3:
//						if (adapter.getValue() instanceof GhScore)
//							return 1f == ((GhScore) adapter.getValue()).getHitPercent();
//						
//					case 4:
//						if (adapter.getValue() instanceof GhScore)
//							return ((GhScore) adapter.getValue()).isFullCombo();
//				}
				
//				return false;
			}
		});
	}
	
	Color fg = new Color(0x009900);
	Color bg = new Color(0xcff6dd);
	
	@Override
	protected Component doHighlight(Component component,
			ComponentAdapter adapter) {
		
		if (!adapter.isSelected())
			component.setBackground(bg);
		
		if ((adapter.column == 3 && 1f == ((GhScore) adapter.getValue()).getHitPercent()) ||
			(adapter.column == 4 && ((GhScore) adapter.getValue()).isFullCombo())) {
			component.setForeground(fg);
			component.setFont(component.getFont().deriveFont(Font.BOLD));	
		}
		
		return component;
	}

}

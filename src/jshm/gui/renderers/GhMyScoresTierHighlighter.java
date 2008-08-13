package jshm.gui.renderers;

import java.awt.Color;
import java.awt.Component;

import jshm.gui.datamodels.GhMyScoresTreeTableModel;

import org.jdesktop.swingx.decorator.AbstractHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.HighlightPredicate;

public class GhMyScoresTierHighlighter extends AbstractHighlighter {
	Color color = new Color(0x990000);
	
	public GhMyScoresTierHighlighter() {
		super(new HighlightPredicate() {
			@Override
			public boolean isHighlighted(Component renderer,
					ComponentAdapter adapter) {
				
				Object o = adapter.getValueAt(adapter.row, 0);
				
				if (o instanceof GhMyScoresTreeTableModel.Tier)
					return true;
				
				return false;
			}
		});
	}
	
	@Override
	protected Component doHighlight(Component component,
			ComponentAdapter adapter) {
		component.setForeground(color);
//		component.setFont(component.getFont().deriveFont(java.awt.Font.BOLD));
		return component;
	}

}

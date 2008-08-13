package jshm.gui.renderers;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import jshm.gh.GhScore;

import org.jdesktop.swingx.decorator.AbstractHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.HighlightPredicate;

public class GhMyScoresFcHighlighter extends AbstractHighlighter {
	Color color = new Color(0x009900);
	
	public GhMyScoresFcHighlighter() {
		super(new HighlightPredicate() {
			@Override
			public boolean isHighlighted(Component renderer,
					ComponentAdapter adapter) {				
				switch (adapter.column) {
					case 4:
						return new Integer(100).equals(adapter.getValue());
						
					case 5:
						if (adapter.getValue() instanceof GhScore)
							return ((GhScore) adapter.getValue()).isFullCombo();
				}
				
				return false;
			}
		});
	}
	
	@Override
	protected Component doHighlight(Component component,
			ComponentAdapter adapter) {
		component.setForeground(color);
		component.setFont(component.getFont().deriveFont(Font.BOLD));
		return component;
	}

}

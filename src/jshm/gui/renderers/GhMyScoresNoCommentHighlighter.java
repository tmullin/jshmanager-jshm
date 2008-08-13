package jshm.gui.renderers;

import java.awt.Color;
import java.awt.Component;

import javax.swing.UIManager;

import org.jdesktop.swingx.decorator.AbstractHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.HighlightPredicate;

public class GhMyScoresNoCommentHighlighter extends AbstractHighlighter {
	Color color = new Color(0x009900);
	
	public GhMyScoresNoCommentHighlighter() {
		super(new HighlightPredicate() {
			@Override
			public boolean isHighlighted(Component renderer,
					ComponentAdapter adapter) {				
				switch (adapter.column) {
					case 0:
						return !adapter.isSelected() && "No Comment".equals(adapter.getValue());
				}
				
				return false;
			}
		});
	}
	
	@Override
	protected Component doHighlight(Component component,
			ComponentAdapter adapter) {
		component.setForeground(UIManager.getColor("textInactiveText"));
		return component;
	}

}

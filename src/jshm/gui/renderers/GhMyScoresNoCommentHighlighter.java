package jshm.gui.renderers;

import java.awt.Color;
import java.awt.Component;

import javax.swing.UIManager;

import jshm.gh.GhScore;

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
				
				if (!(adapter.getValueAt(adapter.row, 1) instanceof GhScore)) return false;
				
				switch (adapter.column) {
					case 0:
						return
						!adapter.isSelected() &&
						((GhScore) adapter.getValueAt(adapter.row, 1)).getComment().isEmpty();
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

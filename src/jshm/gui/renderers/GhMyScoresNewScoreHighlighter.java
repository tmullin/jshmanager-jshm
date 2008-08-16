package jshm.gui.renderers;

import java.awt.Color;
import java.awt.Component;

import jshm.Score;
import jshm.gh.GhScore;

import org.jdesktop.swingx.decorator.AbstractHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.HighlightPredicate;

public class GhMyScoresNewScoreHighlighter extends AbstractHighlighter {
	Color color = new Color(0x009900);
	
	public GhMyScoresNewScoreHighlighter() {
		super(new HighlightPredicate() {
			@Override
			public boolean isHighlighted(Component renderer,
					ComponentAdapter adapter) {
				
				if (!(adapter.getValueAt(adapter.row, 1) instanceof GhScore)) return false;
				
				Score.Status status = ((GhScore) adapter.getValueAt(adapter.row, 1)).getStatus();
				
				return !adapter.isSelected() &&
				(status == Score.Status.NEW || status == Score.Status.TEMPLATE);
			}
		});
	}
	
	Color bg = new Color(0x9fe4f1);
	
	@Override
	protected Component doHighlight(Component component,
			ComponentAdapter adapter) {
		component.setBackground(bg);
		return component;
	}

}

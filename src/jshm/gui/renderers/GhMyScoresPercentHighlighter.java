/*
 * -----LICENSE START-----
 * JSHManager - A Java-based tool for managing one's ScoreHero account.
 * Copyright (C) 2008 Tim Mullin
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * -----LICENSE END-----
 */
package jshm.gui.renderers;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import jshm.Score;

import org.jdesktop.swingx.decorator.AbstractHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.HighlightPredicate;

public class GhMyScoresPercentHighlighter extends AbstractHighlighter {	
	public GhMyScoresPercentHighlighter() {
		super(new HighlightPredicate() {
			@Override
			public boolean isHighlighted(Component renderer,
					ComponentAdapter adapter) {	
				if (adapter.column != 3) return false;
				if (!(adapter.getValue() instanceof Score)) return false;
				
				Score score = (Score) adapter.getValue(); 
				return score.getHitPercent() < 1f;
			}
		});
	}
	
	static final Color ORANGE = new Color(0xF48400);
	static final Color RED = new Color(0xEE0000);
	
	@Override
	protected Component doHighlight(Component component,
			ComponentAdapter adapter) {
		
		Score score = (Score) adapter.getValue();
		Color fg = score.getHitPercent() >= 0.8f
		? ORANGE : RED;
			
		component.setForeground(fg);
		component.setFont(component.getFont().deriveFont(Font.BOLD));	
		
		return component;
	}
}

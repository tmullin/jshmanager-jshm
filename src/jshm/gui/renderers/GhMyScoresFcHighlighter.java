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

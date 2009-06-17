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

import jshm.gui.datamodels.ScoresTreeTableModel;
import jshm.gui.datamodels.GhSongDataTreeTableModel;
import jshm.gui.datamodels.RbSongDataTreeTableModel;

import org.jdesktop.swingx.decorator.AbstractHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.HighlightPredicate;

public class TierHighlighter extends AbstractHighlighter {
	Color color = new Color(0x990000);
	
	public TierHighlighter() {
		super(new HighlightPredicate() {
			@Override
			public boolean isHighlighted(Component renderer,
					ComponentAdapter adapter) {
				
				Object o = adapter.getValueAt(adapter.row, 0);
				
				if (o instanceof ScoresTreeTableModel.Tier ||
					o instanceof GhSongDataTreeTableModel.Tier ||
					o instanceof RbSongDataTreeTableModel.Tier)
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

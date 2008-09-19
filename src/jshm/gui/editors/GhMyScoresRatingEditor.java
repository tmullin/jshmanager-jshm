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
package jshm.gui.editors;

import java.awt.Component;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;

import jshm.Game;
import jshm.GameTitle;
import jshm.Score;
import jshm.gh.GhGameTitle;
import jshm.gh.GhScore;
import jshm.rb.RbGameTitle;
import jshm.rb.RbScore;

public class GhMyScoresRatingEditor extends DefaultCellEditor {
	public static JComboBox createRatingComboBox(Game game) {
		return createRatingComboBox(game.title);
	}
	
	public static JComboBox createRatingComboBox(GameTitle game) {
		Object[] values = null;
		
		if (game instanceof GhGameTitle) {
			values = new Object[] {
				GhScore.getRatingIcon(0),
				GhScore.getRatingIcon(5),
				GhScore.getRatingIcon(4),
				GhScore.getRatingIcon(3)
			};
		} else if (game instanceof RbGameTitle) {
			values = new Object[] {
				RbScore.getRatingIcon(0),
				RbScore.getRatingIcon(6),
				RbScore.getRatingIcon(5),
				RbScore.getRatingIcon(4),
				RbScore.getRatingIcon(3),
				RbScore.getRatingIcon(2),
				RbScore.getRatingIcon(1)
			};
		} else {
			assert false: "game not a GhGameTitle or RbGameTitle";
		}
		
		return 
		new JComboBox(values);
	}
	
	public GhMyScoresRatingEditor(Game game) {
		super(createRatingComboBox(game));
	}
	
    public Component getTableCellEditorComponent(JTable table, Object value,
			 boolean isSelected,
			 int row, int column) {
    	
		return super.getTableCellEditorComponent(
			table, ((Score) value).getRatingIcon(),
			isSelected, row, column);
	}
}

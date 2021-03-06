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

import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;

import jshm.gh.GhScore;

import org.jdesktop.swingx.treetable.TreeTableCellEditor;

public class ScoresCommentEditor extends TreeTableCellEditor {

    public ScoresCommentEditor(JTree tree) {
		super(tree);
	}

	public Component getTableCellEditorComponent(JTable table, Object value,
			 boolean isSelected,
			 int row, int column) {
    	System.out.println("edt " + column + ": " + value);
    	
		if (value instanceof GhScore) { 
			value = ((GhScore) value).getComment();
		}
		
		JTextField c = (JTextField) super.getTableCellEditorComponent(
			table, value,
			isSelected, row, column);
		
		c.selectAll();
		return c;
    }
}

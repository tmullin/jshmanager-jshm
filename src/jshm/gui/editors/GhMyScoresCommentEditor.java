package jshm.gui.editors;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.JTree;

import jshm.gh.GhScore;

import org.jdesktop.swingx.treetable.TreeTableCellEditor;

public class GhMyScoresCommentEditor extends TreeTableCellEditor {

    public GhMyScoresCommentEditor(JTree tree) {
		super(tree);
	}

	public Component getTableCellEditorComponent(JTable table, Object value,
			 boolean isSelected,
			 int row, int column) {
    	System.out.println("edt " + column + ": " + value);
    	
		if (value instanceof GhScore) { 
			value = ((GhScore) value).getComment();
		}
		
		return super.getTableCellEditorComponent(
			table, value,
			isSelected, row, column);
    }
}

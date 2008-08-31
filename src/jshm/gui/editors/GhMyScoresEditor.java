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
//import javax.swing.InputVerifier;
//import javax.swing.JComponent;
//import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import jshm.gh.GhScore;
import jshm.gui.EditPopupMenu;

public class GhMyScoresEditor extends DefaultCellEditor {
//	private static final InputVerifier
//		SCORE_VERIFIER = new InputVerifier() {
//			@Override
//			public boolean verify(JComponent input) {
//				boolean ret = false;
//				System.out.print("Verifying '" + ((JTextField) input).getText() + "'");
//				try {
//					int i = Integer.parseInt(
//						((JTextField) input).getText());
//					
//					ret = i > 0;
//				} catch (NumberFormatException e) {}
//				
//				System.out.println(ret);
//				
//				if (!ret) {
//					JOptionPane.showMessageDialog(input, "Score must be > 0", "Error", JOptionPane.WARNING_MESSAGE);
//				}
//				
//				return ret;
//			}
//		},
//		
//		PERCENT_VERIFIER = new InputVerifier() {
//			@Override
//			public boolean verify(JComponent input) {
//				boolean ret = false;
//				
//				try {
//					String s = ((JTextField) input).getText();
//					if (s.isEmpty()) return true;
//					int i = Integer.parseInt(s);
//					return 0 < i && i <= 100;
//				} catch (NumberFormatException e) {}
//				
//				if (!ret) {
//					JOptionPane.showMessageDialog(input, "Percent must be between 1 and 100 or left blank", "Error", JOptionPane.WARNING_MESSAGE);
//				}
//				
//				return ret;
//			}
//		},
//		
//		STREAK_VERIFIER = new InputVerifier() {
//			@Override
//			public boolean verify(JComponent input) {
//				boolean ret = false;
//				
//				try {
//					String s = ((JTextField) input).getText();
//					if (s.isEmpty()) return true;
//					int i = Integer.parseInt(s);
//					return 0 < i;
//				} catch (NumberFormatException e) {}
//				
//				if (!ret) {
//					JOptionPane.showMessageDialog(input, "Streak must be > 0 or left blank", "Error", JOptionPane.WARNING_MESSAGE);
//				}
//				
//				return ret;
//			}
//		};
		
	public GhMyScoresEditor() {
		super(new JTextField());
	}
	
	public boolean stopCellEditing() {
		EditPopupMenu.remove((JTextField) getComponent());
		return super.stopCellEditing();
	}
	
    public Component getTableCellEditorComponent(JTable table, Object value,
			 boolean isSelected,
			 int row, int column) {
   	
//    	System.out.println("edt " + column + ": " + value);
    	
//    	InputVerifier iv = null;
    	
    	if (value instanceof GhScore) { 
    		GhScore score = (GhScore) value;
    		
    		switch (column) {
    			case 0: value = score.getComment(); break;
    			case 1:
    				value = score.getScore() > 0
    					? score.getScore() : "";
//    				iv = SCORE_VERIFIER;
    				break;
    				
    			case 3:
    				value = score.getPart(1).getHitPercent() != 0f
    					? (int) (score.getPart(1).getHitPercent() * 100) : "";
//    				iv = PERCENT_VERIFIER;
    				break;
    				
    			case 4:
    				value = score.getPart(1).getStreak() > 0
    					? score.getPart(1).getStreak() : "";
//    				iv = STREAK_VERIFIER;
    				break;
    		}
    		
//    		System.out.println("edt " + column + " (new value): " + value);
    	}
    	
		JTextField comp = (JTextField) super.getTableCellEditorComponent(
			table, value,
			isSelected, row, column);
		comp.selectAll();
//		((JComponent) comp).setInputVerifier(iv);
		
		EditPopupMenu.add(comp);
		
		return comp;
	}
}

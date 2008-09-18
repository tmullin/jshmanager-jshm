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

import java.awt.Component;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import jshm.Score;

public class GhMyScoresCellRenderer extends DefaultTableCellRenderer {
	private final DecimalFormat NUM_FMT = new DecimalFormat("#,###");
	private final SimpleDateFormat DATE_FMT = new SimpleDateFormat("MM/dd/yy HH:mm");
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {

//		System.out.println(row + "x" + column + ": " + value);
    	setIcon(null);
    	    	    	
    	super.getTableCellRendererComponent(
       		table, value, isSelected, hasFocus, row, column);
    	
    	if (value instanceof Score) {
    		final Score score = (Score) value;
    	
			switch (column) {
				case 0:
//					System.out.println(value.getClass().getName() + ": " + value);
					String s = score.getComment();
						
					/*if (s.isEmpty()) {
						setText("No Comment");
					} else*/ if (s.length() > 40) {
						setText(s.substring(0, 40) + "...");
					} else {
						setText(s);
					}
					break;
				
				case 1:
					if (score.getScore() > 0) {
						String formattedScore = NUM_FMT.format(score.getScore());
						if (!score.getImageUrl().isEmpty()) {
							formattedScore = "<html><font color=blue><u>" + formattedScore + "</u></font>";
						}
						setText(formattedScore);
						
						if (!score.getVideoUrl().isEmpty()) {
							setHorizontalTextPosition(LEFT);
							setIcon(
								new ImageIcon(
									GhMyScoresCellRenderer.class.getResource("/jshm/resources/images/video.gif")));
						}
					} else {
						setText("");
					}
					break;
					
				case 2:
					setHorizontalTextPosition(RIGHT);
					setIcon(score.getRatingIcon());
					setText(
						score.getGameTitle().supportsCalculatedRating() &&
						score.getCalculatedRating() != 0.0f
						? String.format("(%01.1f)", 
							(float) ((int) (score.getCalculatedRating() * 10)) / 10.0f
						  )
						: null);
					break;
				
				case 3:
					setText(
						score.getPartHitPercent(1) != 0.0f
						? String.valueOf((int) (score.getPartHitPercent(1) * 100))
						: "");
					break;
					
				case 4:
					if (score.getPartStreak(1) > 0)
						setText(NUM_FMT.format(score.getPartStreak(1)));
					else
						setText("");
					break;
			}
    	} else if (value instanceof Date) {
    		switch (column) {
				case 5: case 6:
					setText(DATE_FMT.format((Date) value));
    		}
    	}
		
		return this;
	}

}

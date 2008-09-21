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
package jshm.gui.plaf;

import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicProgressBarUI;

public class MyProgressBarUI extends BasicProgressBarUI {
	protected int getBoxLength(int availableLength, int otherDimension) {
		return 1;
	}

    protected void setAnimationIndex(int newValue) {
    	super.setAnimationIndex(newValue);
        progressBar.repaint();
    }
	
	private BufferedImage gradient = null;
//	private int delta = 0;
	
	private void makeGradient() {
		Dimension size = progressBar.getSize();
		
//		delta = size.width / getFrameCount();
		
		gradient = new BufferedImage(size.width * 2, size.height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = (Graphics2D) gradient.getGraphics();
		Shape shape = new Rectangle2D.Float(0, 0, gradient.getWidth(), gradient.getHeight());
		g2.setPaint(
			new GradientPaint(
				0, 0, progressBar.getForeground(),
				size.width / 3, 0, progressBar.getBackground(), true));
		g2.fill(shape);
	}
		
	protected void paintIndeterminate(Graphics g, JComponent c) {
		if (!(g instanceof Graphics2D)) {
			return;
		}

		if (null == gradient) makeGradient();
		
		Graphics2D g2 = (Graphics2D) g;
		Dimension size = progressBar.getSize();

//		int startx = delta * getAnimationIndex();
		
		int startx = getBox(boxRect).x;
		
		g2.drawImage(gradient,
			0, 0, size.width, size.height,
			size.width - startx, 0, 2 * size.width - startx, size.height, null);
	}
}

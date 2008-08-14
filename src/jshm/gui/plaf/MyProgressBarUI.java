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
	
	private void makeGradient() {
		Dimension size = progressBar.getSize();
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

		int startx = getBox(boxRect).x;
		
		g2.drawImage(gradient,
			0, 0, size.width, size.height,
			startx, 0, startx + size.width, size.height, null);
	}
}
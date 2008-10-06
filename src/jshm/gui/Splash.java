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
package jshm.gui;

import java.awt.Font;
import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JProgressBar;
import javax.swing.JWindow;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.UIManager;

import jshm.gui.plaf.MyProgressBarUI;

public class Splash extends JWindow {
	Color bg = Color.BLACK;
	Color fg = Color.white;
	
	JPanel cp;
	JLabel bgLabel;
	ImageIcon icon;
	JProgressBar progress;
	JLabel status;
	
	public Splash() {
		this("Loading...");
	}
	
	public Splash(String statusText) {
		setBackground(bg);
		
		icon = new ImageIcon(Splash.class.getResource("/jshm/resources/images/splash.png"));
		bgLabel = new JLabel(icon);
		progress = new JProgressBar();
		progress.setUI(new MyProgressBarUI());
		progress.setIndeterminate(true);
		progress.setOpaque(false);
		progress.setBorder(null);
		progress.setForeground(Color.ORANGE);
		progress.setBackground(Color.RED);
//		progress.setPreferredSize(new Dimension(progress.getPreferredSize().width, 5));
		status = new JLabel(statusText, JLabel.CENTER);
		status.setFont(
			status.getFont()
			.deriveFont(Font.BOLD,
				status.getFont().getSize2D() * 1.5f));
		status.setForeground(fg);
		
		cp = new JPanel(new BorderLayout());
		cp.setBackground(bg);
		setContentPane(cp);
		JPanel botPanel = new JPanel(new BorderLayout(0, 5));
		botPanel.setOpaque(false);
		botPanel.add(status, BorderLayout.CENTER);
		botPanel.add(progress, BorderLayout.SOUTH);
		add(botPanel, BorderLayout.SOUTH);
		add(bgLabel, BorderLayout.CENTER);
		pack();
		
		setLocationRelativeTo(null);
		setVisible(true);
		toFront();
	}
	
//	public void setProgress(int value) {
//		progress.setValue(value);
//		setIndeterminante(false);
//	}
//	
//	public void setIndeterminante(boolean on) {
//		progress.setIndeterminate(on);
//	}
	
	public void setStatus(String s) {
		status.setText(s);
	}
	
	public static void main(String[] args) {
		try {
			// Set the Look & Feel to match the current system
			UIManager.setLookAndFeel(
				UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.out.println("Couldn't set system look & feel (not fatal)");
		}
		
		Splash splash = new Splash("Splash Test....");
		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {}
		
		splash.dispose();
	}
}

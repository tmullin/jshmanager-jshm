package jshm.gui;

import java.awt.Font;
import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JWindow;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JLabel;
//import javax.swing.JProgressBar;

public class Splash extends JWindow {
	JPanel cp;
	JLabel bgLabel;
	ImageIcon bg;
//	JProgressBar progress;
	JLabel status;
	
	public Splash(String statusText) {
		this.setAlwaysOnTop(true);
		setBackground(Color.WHITE);
		
		bg = new ImageIcon(Splash.class.getResource("/jshm/resources/images/splash.png"));
		bgLabel = new JLabel(bg);
//		progress = new JProgressBar();
//		setIndeterminante(true);
		status = new JLabel(statusText, JLabel.CENTER);
		status.setFont(status.getFont().deriveFont(Font.BOLD, 16.0f));
		status.setForeground(Color.BLACK);
		
		cp = new JPanel(new BorderLayout());
		cp.setBackground(Color.WHITE);
		setContentPane(cp);
		add(status, BorderLayout.SOUTH);
		add(bgLabel, BorderLayout.CENTER);
//		add(progress, BorderLayout.SOUTH);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
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
}

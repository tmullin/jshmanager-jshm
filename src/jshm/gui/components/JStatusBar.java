package jshm.gui.components;

import java.awt.*;
import javax.swing.*;

public class JStatusBar extends JPanel {
	private JLabel text = new JLabel();
	private JProgressBar progress = new JProgressBar();
	//private JLabel extra = new JLabel("        ");
	
	public JStatusBar() {
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		Insets insets = new Insets(0, 2, 0, 2);
		javax.swing.border.Border b =
			BorderFactory.createCompoundBorder(
					BorderFactory.createMatteBorder(1, 0, 0, 0, UIManager.getColor("controlShadow")),
					BorderFactory.createMatteBorder(1, 0, 0, 0, UIManager.getColor("controlLtHighlight")));
//			BorderFactory.createCompoundBorder(
//				BorderFactory.createEmptyBorder(5, 5, 5, 5),
//				BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		
		c.insets = insets; c.weightx = 1.0; c.weighty = 1.0;
		c.fill = GridBagConstraints.BOTH;
		text.setBorder(b);
		add(text, c);
		
		c.gridx++; c.weightx = 0.001;
		c.fill = GridBagConstraints.NONE;
		progress.setVisible(false);
		progress.setBorder(b);
		progress.setBorderPainted(true);
		add(progress, c);
		
		//c.gridx++; c.weightx = 0.0;
		//extra.setBorder(b);
		//add(extra, c);
	}
	
	public void setText(String str) {
		text.setText("  " + str);
	}
	
	public JProgressBar getProgressBar() {
		return progress;
	}
	
	//public void setExtra(String str) {
	//	extra.setText("    " + str + "    ");
	//}
}

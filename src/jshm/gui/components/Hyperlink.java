package jshm.gui.components;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import jshm.gui.plaf.MyHyperlinkUI;
import jshm.util.Util;

import org.jdesktop.swingx.JXHyperlink;

/**
 * 
 * @author Tim Mullin
 *
 */
public class Hyperlink extends JXHyperlink {
	public Hyperlink() {
		this(null, null);
	}
	
	public Hyperlink(final String title, final String url) {
		super();
		
		setText(title);
		setUI(new MyHyperlinkUI());

		if (null != url) {
			setToolTipText(url);
			
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Util.openURL(url);
				}
			});
		}
	}
}

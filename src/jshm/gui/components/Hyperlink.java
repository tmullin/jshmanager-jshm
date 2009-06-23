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
package jshm.gui.components;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import jshm.gui.GuiUtil;
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

		setUrl(url);
	}
	
	ActionListener urlListener = null;
	
	public void setUrl(final String url) {
		setUrl(url, false);
	}
	
	public void setUrl(final String url, final boolean checkForImage) {
		if (null != url) {
			setToolTipText(url);
			
			if (null != urlListener) {
				removeActionListener(urlListener);
			}
			
			if (checkForImage) {
				urlListener = new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						GuiUtil.openImageOrBrowser(null, url);
					}
				};
			} else {
				urlListener = new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						Util.openURL(url);
					}
				};
			}
			
			addActionListener(urlListener);
		}
	}
}

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

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashMap;
import java.util.Map;

import jshm.gui.components.StatusBar;

/**
 * Provides a means to set the status bar text with 
 * a help hint when the mouse enters a component and
 * revert the status bar when the mouse exits the
 * component.
 * @author Tim Mullin
 *
 */
public class HoverHelp implements MouseListener, MouseMotionListener {
	final Map<Component, String> simpleMap = new HashMap<Component, String>();
	final Map<Component, Callback> callbackMap = new HashMap<Component, Callback>();
	final StatusBar status;
	
	boolean isHelpShowing = false;
	
	public HoverHelp(StatusBar status) {
		this.status = status;
	}
	
	public void add(Component c, String message) {
		simpleMap.put(c, message);
		c.addMouseListener(this);
	}
	
	public void add(Component c, Callback cb) {
		callbackMap.put(c, cb);
		c.addMouseListener(this);
		c.addMouseMotionListener(this);
	}
	
	public void remove(Component c) {
		if (null != callbackMap.remove(c))
			c.removeMouseMotionListener(this);
		else {
			simpleMap.remove(c);
		}
		
		c.removeMouseListener(this);
	}
	
	@Override
	public void mouseEntered(MouseEvent e) {
		Object src = e.getSource();
		String msg = "";

		msg = simpleMap.get(src);
		
		if (null != msg && !msg.isEmpty()) {
			status.setTempText(msg);
			isHelpShowing = true;
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		Object src = e.getSource();
		
		String msg = callbackMap.get(src).getMessage();
		
		if (null == msg) {
			if (isHelpShowing) {
				status.revertText();
				isHelpShowing = false;
			}
		} else if (!msg.isEmpty()) {
			if (!msg.equals(status.getText())) {
				if (isHelpShowing)
					status.revertText();
				
				status.setTempText(msg);
				isHelpShowing = true;
			}
		}
	}
	
	@Override
	public void mouseExited(MouseEvent e) {
		if (isHelpShowing) {
			status.revertText();
			isHelpShowing = false;
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		// ignore
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// ignore
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// ignore
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// ignore
	}
	
	public static interface Callback {
		public String getMessage();
	}
}

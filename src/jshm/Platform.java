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
package jshm;

import jshm.util.Text;

public enum Platform {
	PS2,
	PS3,
	XBOX360,
	WII,
	PC,
	DS
	;
	
	private transient javax.swing.ImageIcon icon = null;
	
	public javax.swing.ImageIcon getIcon() {
		if (null == icon) {
			try {
				icon = new javax.swing.ImageIcon(
					GameTitle.class.getResource("/jshm/resources/images/platforms/" + this.toString() + "_32.png"));
			} catch (Exception e) {}
		}
		
		return icon;
	}
	
	public String getShortName() {
		return getText("shortName");
	}
	
	private static Text t = null;
	
	public final String getText(String key) {
		if (null == t)
			t = new Text(Platform.class);
		
		return t.get(name() + "." + key);
	}
}

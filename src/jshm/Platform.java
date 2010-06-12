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
	
	/**
	 * A constant array of the "big four" systems,
	 * PS2, Xbox 360, PS3, and Wii
	 */
	public static final Platform[] BIG_FOUR = {
		PS2, XBOX360, PS3, WII
	};
	
	/**
	 * A constant array consisting of Xbox, PS3, and Wii
	 */
	public static final Platform[] BIG_THREE = {
		Platform.XBOX360, Platform.PS3, Platform.WII
	};
	
	public static final Platform[] BIG_FOUR_AND_PC = {
		PS2, XBOX360, PS3, WII, PC
	};
	
	public static final Platform[] NEXT_GEN = {
		XBOX360, PS3
	};
	
	public final javax.swing.ImageIcon getIcon() {
		return jshm.gui.GuiUtil.getIcon(
			"platforms/" + name() + "_32.png");
	}
	
	public final String getShortName() {
		return getText("shortName");
	}
	
	private static Text t = null;
	
	public final String getText(final String key) {
		if (null == t)
			t = new Text(Platform.class);
		
		return t.get(name() + "." + key);
	}
}

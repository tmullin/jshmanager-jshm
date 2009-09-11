/*
 * -----LICENSE START-----
 * JSHManager - A Java-based tool for managing one's ScoreHero account.
 * Copyright (C) 2008, 2009 Tim Mullin
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

import javax.swing.ImageIcon;

import jshm.gui.GuiUtil;

/**
 * Represents the source where a particular song comes from. Mainly
 * geared for Rock Band where in RB2 songs can be from RB1, RB2, DLC,
 * or the AC/DC pack.
 * 
 * @author Tim Mullin
 *
 */
public enum SongSource {
	GH1,
	GH80,
	GH2,
	GH2DLC,
	GH3,
	GH3DLC,
	GHA,
	GHM,

	RB1,
	RB2,
	RBDLC,
	ACDC,
	TBRB;
	
	public ImageIcon getIcon() {
		return getIcon(32);
	}
	
	public ImageIcon getIcon(int size) {
		switch (size) {
//			case 16:
//			case 64:
			case 32: break;
			default: throw new IllegalArgumentException("invalid size: " + size);
		}
		
		return GuiUtil.getIcon("songsources/" + size + "/" + name() + ".png");
	}
	
	public static SongSource smartValueOf(String name) {
		return smartValueOf(name, false);
	}
	
	/**
	 * Returns the SongSource for the provided name. Can return a DLC version
	 * if desired (namely for GH2/3).
	 * @param name
	 * @param dlcVersion
	 * @return
	 */
	public static SongSource smartValueOf(String name, boolean dlcVersion) {
		if (null == name || name.isEmpty())
			return null;
		if ("DLC".equals(name))
			return RBDLC;
		if ("AC/DC".equals(name))
			return ACDC;
		if ("RB1X".equals(name))
			return RB1;
		
		SongSource ret = null;
		
		try {
			ret = SongSource.valueOf(name);
		} catch (IllegalArgumentException e) {}
		
		if (dlcVersion && null != ret) {
			switch (ret) {
				case GH2: ret = GH2DLC; break;
				case GH3: ret = GH3DLC; break;
			}
		}
		
		return ret;
	}
}

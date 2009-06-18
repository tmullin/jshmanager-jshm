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
	ACDC;
	
	public ImageIcon getIcon() {
		return getIcon(32);
	}
	
	public ImageIcon getIcon(int size) {
		switch (size) {
			case 16: case 32: case 64: break;
			default: throw new IllegalArgumentException("invalid size: " + size);
		}
		
		return GuiUtil.getIcon("songsources/" + size + "/" + name() + ".png");
	}
	
	public static SongSource smartValueOf(String name) {
		if (null == name)
			return null;
		if ("AC/DC".equals(name))
			return ACDC;
		if ("DLC".equals(name))
			return RBDLC;
		
		try {
			return SongSource.valueOf(name);
		} catch (IllegalArgumentException e) {}
		
		return null;
	}
}

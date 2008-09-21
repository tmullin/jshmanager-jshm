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
package jshm.gui.datamodels;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.jdesktop.swingx.treetable.AbstractTreeTableModel;

import jshm.gh.*;
import jshm.gui.renderers.TierHighlighter;

/**
 *
 * @author Tim Mullin
 */
public class GhSongDataTreeTableModel extends AbstractTreeTableModel implements Parentable {
	private class DataModel {
		List<Tier> tiers = new ArrayList<Tier>();
		
		public DataModel(
			final GhGame game,
			final List<GhSong> songs) {
	
			for (int i = 1; i <= game.getTierCount(); i++) {
				tiers.add(new Tier(game.getTierName(i)));
			}
			
			for (GhSong song : songs) {
				tiers.get(song.getTierLevel() - 1).songs.add(song);
			}
		}
	}
	
	public class Tier {
		public final String name;
		public final List<GhSong> songs = new ArrayList<GhSong>();
		
		public Tier(String name) {
			this.name = name;
		}
		
		@Override
		public String toString() {
			return String.format("%s (%s songs)", name, songs.size());
		}
	}
	
	
	private final DataModel model;
	
	public GhSongDataTreeTableModel(
		final GhGame game,
		final List<GhSong> songs) {
		
		super("ROOT");
		this.model = new DataModel(game, songs);
	}

	public void setParent(JXTreeTable parent) {
//		this.parent = parent;

	    parent.setHighlighters(
	    	HighlighterFactory.createSimpleStriping(),
	    	new TierHighlighter());
	    
	    parent.getColumnExt(1).setPrototypeValue("00000");
	    
	    for (int i = 2; i <= 7; i++)
	    	parent.getColumnExt(i).setPrototypeValue("00000000");
	    
	    parent.setAutoResizeMode(JXTreeTable.AUTO_RESIZE_OFF);
	    parent.packAll();
	}
	
	public void removeParent(JXTreeTable parent) {}
	
	@Override
	public int getColumnCount() {
		return 8;
	}

	private static final String[] COLUMN_NAMES = {
		"Song", "Notes", "Base Score", "4* Cutoff", "5* Cutoff",
		"6* Cutoff", "7* Cutoff", "8* Cutoff"
	};
	
	@Override
	public String getColumnName(int arg0) {
		return COLUMN_NAMES[arg0];
	}
	
	
	private final DecimalFormat NUM_FMT = new DecimalFormat("#,###");
	
	@Override
	public Object getValueAt(Object node, int column) {
		if (node instanceof Tier) {
			if (column == 0) return node;
			return "";
		}
		
		if (!(node instanceof GhSong)) return "";
		
		GhSong song = (GhSong) node;
		
		String ret = "";
		
		switch (column) {
			case 0: ret = song.getTitle(); break;
			case 1: ret = NUM_FMT.format(song.getNoteCount()); break; // noteCount
			case 2: ret = NUM_FMT.format(song.getBaseScore()); break; // baseScore
			case 3: ret = NUM_FMT.format(song.getFourStarCutoff()); break; // 4*
			case 4: ret = NUM_FMT.format(song.getFiveStarCutoff()); break; // 5*
			case 5: ret = NUM_FMT.format(song.getSixStarCutoff()); break; // 6*
			case 6: ret = NUM_FMT.format(song.getSevenStarCutoff()); break; // 7*
			case 7: ret = NUM_FMT.format(song.getEightStarCutoff()); break; // 8*
				
			default: assert false;
		}
		
		if ("-1".equals(ret))
			ret = "???";
		
		return ret;
	}

	@Override
	public Object getChild(Object parent, int index) {
		if (parent.equals(root)) {
			return model.tiers.get(index);
		}
		
		if (parent instanceof Tier) {
			return ((Tier) parent).songs.get(index);
		}
		
		return null;
	}

	@Override
	public int getChildCount(Object parent) {
		if (parent.equals(root)) {
			return model.tiers.size();
		}
		
		if (parent instanceof Tier) {
			return ((Tier) parent).songs.size();
		}
		
		return 0;
	}

	@Override
	public int getIndexOfChild(Object parent, Object child) {
		if (parent.equals(root)) {
			return model.tiers.indexOf(child);
		}
		
		if (parent instanceof Tier) {
			if (!(child instanceof GhSong)) return -1;
			
			return ((Tier) parent).songs.indexOf(child);
		}
		
		return -1;
	}

	@Override
	public boolean isLeaf(Object node) {
		return node instanceof GhSong;
	}
}

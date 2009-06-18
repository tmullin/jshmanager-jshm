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

//import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.jdesktop.swingx.treetable.AbstractTreeTableModel;

import jshm.SongOrder;
import jshm.rb.*;
import jshm.gui.GUI;
import jshm.gui.renderers.GhMyScoresTreeCellRenderer;
import jshm.gui.renderers.TierHighlighter;

/**
 *
 * @author Tim Mullin
 */
public class RbSongDataTreeTableModel extends AbstractTreeTableModel implements Parentable {
	private class DataModel {
		List<Tier> tiers = new ArrayList<Tier>();
		
		public DataModel(
			final RbGame game,
			final List<SongOrder> songs) {
	
			for (int i = 1; i <= game.getTierCount(); i++) {
				tiers.add(new Tier(game.getTierName(i)));
			}
			
			for (SongOrder song : songs) {
				tiers.get(song.getTier() - 1).songs.add((RbSong) song.getSong());
			}
		}
	}
	
	public class Tier {
		public final String name;
		public final List<RbSong> songs = new ArrayList<RbSong>();
		
		public Tier(String name) {
			this.name = name;
		}
		
		@Override
		public String toString() {
			return String.format("%s (%s songs)", name, songs.size());
		}
	}
	
	
	private final DataModel model;
	
	public RbSongDataTreeTableModel(
		final RbGame game,
		final List<SongOrder> songs) {
		
		super("ROOT");
		this.model = new DataModel(game, songs);
	}

	public void setParent(GUI gui, JXTreeTable parent) {
//		this.parent = parent;

		parent.setTreeCellRenderer(new GhMyScoresTreeCellRenderer());
	    parent.setHighlighters(
	    	HighlighterFactory.createSimpleStriping(),
	    	new TierHighlighter());
	    
	    parent.getColumnExt(1).setPrototypeValue("00000");
	    
//	    for (int i = 2; i <= 7; i++)
//	    	parent.getColumnExt(i).setPrototypeValue("00000000");
	    
	    parent.setAutoResizeMode(JXTreeTable.AUTO_RESIZE_OFF);
	    parent.packAll();
	}
	
	public void removeParent(JXTreeTable parent) {}
	
	@Override
	public int getColumnCount() {
		return 2;
	}

	private static final String[] COLUMN_NAMES = {
		"Song", "Platforms"
	};
	
	@Override
	public String getColumnName(int arg0) {
		return COLUMN_NAMES[arg0];
	}
	
	
//	private final DecimalFormat NUM_FMT = new DecimalFormat("#,###");
	
	@Override
	public Object getValueAt(Object node, int column) {
		if (node instanceof Tier) {
			if (column == 0) return node;
			return "";
		}
		
		if (!(node instanceof RbSong)) return "";
		
		RbSong song = (RbSong) node;
		
		Object ret = "";
		
		switch (column) {
			case 0: ret = "tehe " + song; break;
			case 1: ret = jshm.util.PhpUtil.implode(", ", song.getPlatforms().toArray()); break;

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
			if (!(child instanceof RbSong)) return -1;
			
			return ((Tier) parent).songs.indexOf(child);
		}
		
		return -1;
	}

	@Override
	public boolean isLeaf(Object node) {
		return node instanceof RbSong;
	}
}

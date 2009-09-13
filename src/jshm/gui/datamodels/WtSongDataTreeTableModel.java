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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.tree.TreePath;

import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.jdesktop.swingx.treetable.AbstractTreeTableModel;

import jshm.wt.*;
import jshm.Song.Sorting;
import jshm.gui.GUI;
import jshm.gui.renderers.ScoresTreeCellRenderer;
import jshm.gui.renderers.TierHighlighter;

/**
 *
 * @author Tim Mullin
 */
public class WtSongDataTreeTableModel extends AbstractTreeTableModel implements Parentable, SongSortable {
	private class DataModel {
		List<Tier> tiers = new ArrayList<Tier>();
		
		public DataModel() {
			for (int i = 1; i <= game.getTierCount(sorting); i++) {
				tiers.add(new Tier(game.getTierName(sorting, i)));
			}
			
			for (WtSong song : songs) {
				try {
					tiers.get(song.getTierLevel(sorting) - 1).songs.add(song);
				} catch (IndexOutOfBoundsException e) {
					GUI.LOG.log(java.util.logging.Level.WARNING,
						String.format(
							"Got song with higher tier (%s) than total tier count (%s)",
							song.getTierLevel(sorting), tiers.size()),
					e);
					GUI.LOG.warning("SongOrder: " + song.getSongOrder());
				}
			}
		}
	}
	
	public class Tier implements ModelTier {
		public final String name;
		public final List<WtSong> songs = new ArrayList<WtSong>();
		
		public Tier(String name) {
			this.name = name;
		}
		
		@Override
		public String toString() {
			return String.format("%s (%s song%s)", name, songs.size(), songs.size() == 1 ? "" : "s");
		}
	}
	
	
	private final WtGame game;
	private final List<WtSong> songs;
	private DataModel model;
	private Sorting sorting = null;
	
	public WtSongDataTreeTableModel(
		final WtGame game,
		final Sorting sorting,
		final List<WtSong> songs) {
		
		super("ROOT");
		this.game = game;
		this.songs = songs;
		setSorting(sorting);
	}
	
	public void setSorting(Sorting sorting) {
		Collections.sort(songs, game.getSortingComparator(sorting));

		TreePath tp = new TreePath(root); // ROOT
		int[] indices = null;
		Object[] children = null;
		
		if (null != this.sorting) {
			// remove all existing children
			indices = new int[getChildCount(root)];
			children = new Object[indices.length];
			
			for (int i = 0; i < indices.length; i++) {
				indices[i] = i;
				children[i] = getChild(root, i);
			}
			
			modelSupport.fireChildrenRemoved(
				tp, indices, children);
		}
		
		this.sorting = sorting;
		this.model = new DataModel();
		
		// add tiers for new sorting
		indices = new int[getChildCount(root)];
		children = new Object[indices.length];
		
		for (int i = 0; i < indices.length; i++) {
			indices[i] = i;
			children[i] = getChild(root, i);
		}
		
		modelSupport.fireChildrenAdded(
			tp, indices, children);
	}

	public void setParent(GUI gui, JXTreeTable parent) {
//		this.parent = parent;

		parent.setTreeCellRenderer(new ScoresTreeCellRenderer());
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
		
		if (!(node instanceof WtSong)) return "";
		
		WtSong song = (WtSong) node;
		
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
			if (!(child instanceof WtSong)) return -1;
			
			return ((Tier) parent).songs.indexOf(child);
		}
		
		return -1;
	}

	@Override
	public boolean isLeaf(Object node) {
		return node instanceof WtSong;
	}
}

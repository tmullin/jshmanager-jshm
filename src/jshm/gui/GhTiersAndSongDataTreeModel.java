package jshm.gui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import jshm.gh.*;

public class GhTiersAndSongDataTreeModel implements TreeModel {
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
	}
	
	private final String ROOT_NODE = "ROOT";
	
	private final DataModel model;
	
	public GhTiersAndSongDataTreeModel(
		final GhGame game,
		final List<GhSong> songs) {
		
		this.model = new DataModel(game, songs);
	}
	
	@Override
	public void addTreeModelListener(TreeModelListener l) {
		// TODO Auto-generated method stub

	}

	@Override
	public Object getChild(Object parent, int index) {
		if (parent.equals(ROOT_NODE)) {
			return model.tiers.get(index);
		}
		
		if (parent instanceof Tier) {
			return ((Tier) parent).songs.get(index);
		}
		
		return null;
	}

	@Override
	public int getChildCount(Object parent) {
		if (parent.equals(ROOT_NODE)) {
			return model.tiers.size();
		}
		
		if (parent instanceof Tier) {
			return ((Tier) parent).songs.size();
		}
		
		return 0;
	}

	@Override
	public int getIndexOfChild(Object parent, Object child) {
		if (parent.equals(ROOT_NODE)) {
			return model.tiers.indexOf(child);
		}
		
		if (parent instanceof Tier) {
			if (!(child instanceof GhSong)) return -1;
			
			return ((Tier) parent).songs.indexOf(child);
		}
		
		return -1;
	}

	@Override
	public Object getRoot() {
		return ROOT_NODE;
	}

	@Override
	public boolean isLeaf(Object node) {
		return node instanceof GhSong;
	}

	@Override
	public void removeTreeModelListener(TreeModelListener l) {
		// TODO Auto-generated method stub

	}

	@Override
	public void valueForPathChanged(TreePath path, Object newValue) {
		// TODO Auto-generated method stub

	}

}

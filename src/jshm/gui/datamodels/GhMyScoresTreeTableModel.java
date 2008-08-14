package jshm.gui.datamodels;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.jdesktop.swingx.treetable.AbstractTreeTableModel;

import jshm.gh.*;
import jshm.gui.GuiUtil;
import jshm.gui.renderers.GhMyScoresCellRenderer;
import jshm.gui.renderers.GhMyScoresFcHighlighter;
import jshm.gui.renderers.GhMyScoresNoCommentHighlighter;
import jshm.gui.renderers.GhTierHighlighter;

/**
 *
 * @author Tim Mullin
 */
public class GhMyScoresTreeTableModel extends AbstractTreeTableModel {
	/*
	 * + Tier Name 1
	 * | \- Song Name 1
	 * |    +  Score 1
	 * |    \- Score 2
	 * | \- Song Name 2
	 * ...
	 */
	private class DataModel {
		List<Tier> tiers = new ArrayList<Tier>();
		
		public DataModel(
			final GhGame game,
			final List<GhSong> songs,
			final List<GhScore> scores) {
	
			for (int i = 1; i <= game.getTierCount(); i++) {
				tiers.add(new Tier(game.getTierName(i)));
			}
			
			for (GhSong song : songs) {
				tiers.get(song.getTierLevel() - 1).songs.add(new SongScores(song));
			}
			
			for (GhScore score : scores) {
				tiers.get(((GhSong) score.getSong()).getTierLevel() - 1)
					.addScore(score);
			}
		}
	}
	
	public class Tier {
		public final String name;
		public final List<SongScores> songs = new ArrayList<SongScores>();
		
		public Tier(String name) {
			this.name = name;
		}
		
		public void addScore(GhScore score) {
			for (SongScores ss : songs) {
				if (ss.song.equals((GhSong) score.getSong())) {
					ss.scores.add(score);
					return;
				}
			}
		}
		
		public String toString() {
			return name;
		}
	}
	
	public class SongScores {
		public final GhSong song;
		public final List<GhScore> scores = new ArrayList<GhScore>();
		
		public SongScores(GhSong song) {
			this.song = song;
		}
		
		public String toString() {
			return song.getTitle();
		}
	}
	
	
//	private JXTreeTable parent;
	private final DataModel model;
	
	public GhMyScoresTreeTableModel(
		final GhGame game,
		final List<GhSong> songs,
		final List<GhScore> scores) {
		
		super("ROOT");
		this.model = new DataModel(game, songs, scores);
	}

	public void setParent(JXTreeTable parent) {
//		this.parent = parent;
		GhMyScoresCellRenderer renderer = new GhMyScoresCellRenderer();
	    parent.getColumn(2).setCellRenderer(renderer);
	    parent.getColumn(4).setCellRenderer(renderer);
	    
	    parent.addHighlighter(
	    	HighlighterFactory.createSimpleStriping());
	    parent.addHighlighter(
	    	new GhMyScoresFcHighlighter());
	    parent.addHighlighter(
	    	new GhTierHighlighter());
	    parent.addHighlighter(
	    	new GhMyScoresNoCommentHighlighter());
	    
	    GuiUtil.expandTreeFromDepth(parent, 2);
	    
	    parent.getColumnExt(1).setPrototypeValue("00000000000");
	    parent.getColumnExt(2).setPrototypeValue(new ImageIcon(getClass().getResource("/jshm/resources/images/ratings/gh/8.gif")));
	    parent.getColumnExt(3).setPrototypeValue("1000");
	    parent.getColumnExt(4).setPrototypeValue("99999");
	    parent.getColumnExt(5).setPrototypeValue("AAA9999999999");
	    parent.getColumnExt(5).setVisible(false);
	    parent.getColumnExt(6).setPrototypeValue("AAA9999999999");
	    
	    parent.setHorizontalScrollEnabled(false);
	    parent.setAutoResizeMode(JXTreeTable.AUTO_RESIZE_OFF);
	    parent.packAll();
	}
	
	@Override
	public int getColumnCount() {
		return 7;
	}

	@Override
    public Class<?> getColumnClass(int column) {
		switch (column) {
			case 5: case 6: return java.util.Date.class;
			default: return String.class;
		}
    }
	
	private static final String[] COLUMN_NAMES = {
		"Song/Comment", "Score", "Rating", "%",
		"Streak", "Date Created", "Date Submitted"
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
		
		if (node instanceof SongScores) {
			if (column == 0)
				return node;
			return "";
		}
		
		if (!(node instanceof GhScore)) return "";
		
		GhScore score = (GhScore) node;
		
		Object ret = "";
		
		switch (column) {
			case 0:
				ret = score.getComment();
				if (((String) ret).isEmpty())
					ret = "No Comment";
				break;
				
			case 1: ret = NUM_FMT.format(score.getScore()); break;
			
			case 2:
			case 4: ret = score; break;
				
			case 3:
				ret =
					score.getHitPercent() != 0.0f
					? (int) (score.getHitPercent() * 100)
					: "";
					break;
					
			case 5: ret = score.getCreationDate(); break;
			case 6: ret = score.getSubmissionDate(); break;
				
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
		
		if (parent instanceof SongScores) {
			return ((SongScores) parent).scores.get(index);
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
		
		if (parent instanceof SongScores) {
			return ((SongScores) parent).scores.size();
		}
		
		return 0;
	}

	@Override
	public int getIndexOfChild(Object parent, Object child) {
		if (parent.equals(root)) {
			return model.tiers.indexOf(child);
		}
		
		if (parent instanceof Tier) {
			return ((Tier) parent).songs.indexOf(child);
		}
		
		if (parent instanceof SongScores) {
			return ((SongScores) parent).scores.indexOf(child);
		}
		
		return -1;
	}

	@Override
	public boolean isLeaf(Object node) {
		return node instanceof GhScore;
	}
}

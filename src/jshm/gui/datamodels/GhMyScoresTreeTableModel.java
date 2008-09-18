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

import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;

import jshm.Game;
import jshm.Score;
import jshm.Song;
import jshm.gh.GhScore;
import jshm.gh.GhSong;
import jshm.gui.editors.GhMyScoresEditor;
import jshm.gui.editors.GhMyScoresRatingEditor;
import jshm.gui.renderers.GhMyScoresCellRenderer;
import jshm.gui.renderers.GhMyScoresFcHighlighter;
import jshm.gui.renderers.GhMyScoresNewScoreHighlighter;
import jshm.gui.renderers.GhMyScoresNoCommentHighlighter;
import jshm.gui.renderers.GhMyScoresPercentHighlighter;
import jshm.gui.renderers.GhMyScoresTreeCellRenderer;
import jshm.gui.renderers.TierHighlighter;
import jshm.hibernate.HibernateUtil;
import jshm.rb.RbScore;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.jdesktop.swingx.error.ErrorInfo;
import org.jdesktop.swingx.treetable.AbstractTreeTableModel;

/**
 * 
 * @author Tim Mullin
 */
public class GhMyScoresTreeTableModel extends AbstractTreeTableModel implements Parentable {
	static final Logger LOG = Logger.getLogger(GhMyScoresTreeTableModel.class.getName());
	
	/*
	 * + Tier Name 1 | \- Song Name 1 | + Score 1 | \- Score 2 | \- Song Name 2
	 * ...
	 */
	private class DataModel {
		List<Tier>	tiers	= new ArrayList<Tier>();

		public DataModel(final Game game, final List<? extends Song> songs,
				final List<? extends Score> scores) {

			for (int i = 1; i <= game.getTierCount(); i++) {
				tiers.add(new Tier(game.getTierName(i)));
			}
			
			for (Song song : songs) {
				tiers.get(song.getTierLevel() - 1).songs.add(
					new SongScores(song));
			}

			for (Score score : scores) {
				tiers.get(score.getSong().getTierLevel() - 1)
					.addScore(score);
			}

//			for (GhSong song : songs) {
//				tiers.get(song.getTierLevel() - 1).addScore(
//						GhScore.createNewScoreTemplate(song));
//			}
		}
	}

	public class Tier {
		public final String				name;
		public final List<SongScores>	songs = new ArrayList<SongScores>();

		public Tier(String name) {
			this.name = name;
		}

		public void addScore(Score score) {
			for (SongScores ss : songs) {
				if (ss.song.equals((GhSong) score.getSong())) {
					ss.scores.add(score);
					return;
				}
			}
		}
		
		public void removeScore(Score score) {
			for (SongScores ss : songs) {
				if (ss.song.equals((GhSong) score.getSong())) {
					ss.scores.remove(score);
					return;
				}
			}
		}

		public String toString() {
			int scores = 0;

			for (SongScores song : songs) {
				scores += song.scores.size();
			}

			return String.format("%s (%d %s)", name, scores,
					scores != 1 ? "scores" : "score");
		}
	}

	public class SongScores {
		public final Song			song;
		public final List<Score>	scores	= new ArrayList<Score>();

		public SongScores(Song song) {
			this.song = song;
		}

		public String toString() {
			return String.format("%s (%d %s)", song.getTitle(), scores.size(),
					scores.size() != 1 ? "scores" : "score");

		}
	}

	private static JXTreeTable parent;
	private final DataModel	model;
	private final Game game;

	public GhMyScoresTreeTableModel(final Game game,
			final List<? extends Song> songs, final List<? extends Score> scores) {

		super("ROOT");
		this.game = game;
		this.model = new DataModel(game, songs, scores);
	}
	
	public void createScoreTemplate(TreePath p) {
		if (null == p) return;
		
		Object o = p.getLastPathComponent();
//		System.out.println("Selected: " + o);
		
		if (o instanceof Score) {
			createScoreTemplate((Score) o);
			p = p.getParentPath();
		} else if (o instanceof SongScores) {
			createScoreTemplate((SongScores) o);
		}
		
		TreeModelEvent e = new TreeModelEvent(this, p);
		
		for (TreeModelListener l : getTreeModelListeners())
			l.treeStructureChanged(e);
	}
	
	private void createScoreTemplate(SongScores selectedSongScores) {
		// FIXME
//		selectedSongScores.scores.add(
//			GhScore.createNewScoreTemplate(selectedSongScores.song));
	}
	
	private void createScoreTemplate(Score selectedScore) {
		// FIXME
//		Song song = (Song) selectedScore.getSong();
//		
//		model.tiers.get(song.getTierLevel() - 1)
//			.addScore(GhScore.createNewScoreTemplate(song));
	}

	public void deleteScore(TreePath p) {
		deleteScore(p, false);
	}
	
	public void deleteScore(TreePath p, boolean deleteFromScoreHero) {
		if (!(p.getLastPathComponent() instanceof Score)) return;
		
		Score score = (Score) p.getLastPathComponent();
		
		switch (score.getStatus()) {
			case NEW:
			case SUBMITTED:
			case DELETED:
				Session sess = null;
				Transaction tx = null;
				
				try {
					sess = HibernateUtil.getCurrentSession();
					tx = sess.beginTransaction();
					sess.delete(score);
					sess.getTransaction().commit();
				} catch (Exception e) {
					if (null != tx) tx.rollback();
					LOG.log(Level.SEVERE, "Failed to delete score from database", e);
					ErrorInfo ei = new ErrorInfo("Error", "Failed to delete score from database", null, null, e, null, null);
					JXErrorPane.showDialog(null, ei);
					return;
				} finally {
					if (sess.isOpen()) sess.close();
				}
				
			case TEMPLATE:
				// FIXME
				model.tiers.get(((GhSong) score.getSong()).getTierLevel() - 1)
					.removeScore(score);
				
				TreeModelEvent e = new TreeModelEvent(this, p.getParentPath());
				for (TreeModelListener l : getTreeModelListeners())
					l.treeStructureChanged(e);
				
				break;
				
		}
	}
	
	/**
	 * 
	 * @return A new instance of {@link GhMyScoresTreeTableModel}
	 * that contains only the new scores that this model contains.
	 */
	public GhMyScoresTreeTableModel createNewScoresModel() {
		List<Song> songs = new ArrayList<Song>();
		List<Score> newScores = new ArrayList<Score>();
		
		for (Tier t : model.tiers) {
			for (SongScores ss : t.songs) {
				for (Score s : ss.scores) {
					if (s.getStatus() == Score.Status.NEW && s.isSubmittable()) {
						newScores.add(s);
						
						if (!songs.contains(s.getSong()))
							songs.add((GhSong) s.getSong());
					}
				}
			}
		}
		
		return new GhMyScoresTreeTableModel(this.game, songs, newScores);
	}
	
	/**
	 * 
	 * @return The total number of scores this model contains.
	 */
	public int getScoreCount() {
		int count = 0;
		
		for (Tier t : model.tiers) {
			for (SongScores ss : t.songs) {
				count += ss.scores.size();
			}
		}
		
		return count;
	}
	
	public List<Score> getScores() {
		List<Score> scores = new ArrayList<Score>();
		
		for (Tier t : model.tiers) {
			for (SongScores ss : t.songs) {
				scores.addAll(ss.scores);
			}
		}
		
		return scores;
	}
	
	private static MouseListener myParentMouseListener = new MouseAdapter() {
		// TODO figure out if it's possible to determine whether the
		// score was clicked for the image url or if the video icon
		// was clicked for the video url
		public void mouseClicked(MouseEvent e) {
//			System.out.println(e);
			if (e.getClickCount() != 1 || e.getButton() != MouseEvent.BUTTON1)
				return;
			
			int row = parent.rowAtPoint(e.getPoint());
			int column = parent.columnAtPoint(e.getPoint());
			
			if (1 != column) return;
			
			Object o = parent.getModel().getValueAt(row, column);
			
			if (!(o instanceof GhScore)) return;
			
			GhScore score = (GhScore) o;
			
			// can still double-click to edit score without opening
			// a browser window
			if (score.getStatus() == Score.Status.NEW) return;
			
			// preference is given to the video link
			// if both are present
			String url =
				!score.getVideoUrl().isEmpty()
				? score.getVideoUrl()
				: !score.getImageUrl().isEmpty()
				  ? score.getImageUrl()
				  : null;
				  
			if (null != url) {
				jshm.util.Util.openURL(url);
			}
		}
	};
	
	private static MouseMotionListener myParentMouseMotionListener = new MouseMotionAdapter() {
		public void mouseMoved(MouseEvent e) {
			int row = parent.rowAtPoint(e.getPoint());
			int column = parent.columnAtPoint(e.getPoint());
			
			Cursor newCursor = null; //Cursor.getDefaultCursor();
			
			Object o = parent.getModel().getValueAt(row, column);
			
			if (1 == column && o instanceof GhScore) {
				GhScore score = (GhScore) o;
				
				if (score.getStatus() != Score.Status.NEW && 
					(!score.getImageUrl().isEmpty() || !score.getVideoUrl().isEmpty())) {
					newCursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
				}
			}
			
			parent.setCursor(newCursor);
		}
	};
	
	// TODO some of the stuff in here might not necessary fit 
	// as part of the "data model"/mvc stuff...
	public void setParent(final JXTreeTable parent) {
		GhMyScoresTreeTableModel.parent = parent;
		
		GhMyScoresTreeCellRenderer treeRenderer = new GhMyScoresTreeCellRenderer();
		parent.setTreeCellRenderer(treeRenderer);
		
		GhMyScoresCellRenderer renderer = new GhMyScoresCellRenderer();
		
		for (int i = 0; i <= 6; i++)
			parent.getColumn(i).setCellRenderer(renderer);

		parent.addMouseListener(myParentMouseListener);
		parent.addMouseMotionListener(myParentMouseMotionListener);
		
		Highlighter[] highlighters = new Highlighter[] {
			HighlighterFactory.createSimpleStriping(),
			new GhMyScoresPercentHighlighter(),
			new GhMyScoresFcHighlighter(),
			new TierHighlighter(),
			new GhMyScoresNoCommentHighlighter(),
			new GhMyScoresNewScoreHighlighter()
		};
		
		parent.setHighlighters(highlighters);

		Object[] prototypes = new Object[] {
			"00000000000", GhScore.getRatingIcon(8),
			"1000", "99999", "AAA9999999999", "AAA9999999999"
		};
		
		for (int i = 1; i <= 6; i++)
			parent.getColumnExt(i).setPrototypeValue(prototypes[i - 1]);

		parent.getColumnExt(5).setVisible(false);

		parent.setEditable(true);	

		
//		parent.setHierarchicalEditor(
//			new GhMyScoresCommentEditor(   ));
		GhMyScoresEditor editor = new GhMyScoresEditor();
		parent.setDefaultEditor(Score.class, editor);
//		parent.getColumn(0).setCellEditor(editor); // has no effect....
//		parent.getColumn(1).setCellEditor(editor);
		parent.getColumn(2).setCellEditor(new GhMyScoresRatingEditor(game));
//		parent.getColumn(3).setCellEditor(editor);
//		parent.getColumn(4).setCellEditor(editor);
		
		parent.setHorizontalScrollEnabled(false);
		parent.setAutoResizeMode(JXTreeTable.AUTO_RESIZE_OFF);
		parent.packAll();
	}

	public void removeParent(JXTreeTable parent) {
		parent.removeMouseListener(myParentMouseListener);
		parent.removeMouseMotionListener(myParentMouseMotionListener);
		GhMyScoresTreeTableModel.parent = null;
	}
	
	
	@Override
	public int getColumnCount() {
		return 7;
	}

	@Override
	public Class<?> getColumnClass(int column) {
		switch (column) {
			case 0: return String.class;
			case 1:	case 2: case 3: case 4:
				return Score.class;
			case 5:
			case 6:
				return java.util.Date.class;
				
			default:
				assert false;
//				return Object.class;
		}
		
		return null;
	}

	private static final String[]	COLUMN_NAMES	= {
		"Song/Comment",
		"Score", "Rating", "%", "Streak", "Date Created", "Date Submitted" };

	@Override
	public String getColumnName(int arg0) {
		return COLUMN_NAMES[arg0];
	}

	@Override
	public Object getValueAt(Object node, int column) {
		if (node instanceof Tier) {
			if (column == 0)
				return node;
			return "";
		}

		if (node instanceof SongScores) {
			if (column == 0)
				return node;
			return "";
		}

		if (!(node instanceof Score))
			return "";

		Score score = (Score) node;

//		if (score.getStatus() == Score.Status.TEMPLATE) {
//			switch (column) {
//				case 0:
//					ret = "Enter new score...";
//					break;
//			}
//
//			return ret;
//		}

		switch (column) {
			case 0:
				return score.getComment();
			case 5:
				return score.getCreationDate();
			case 6:
				return score.getSubmissionDate();

			default:
				return score;
		}
	}

	public boolean isCellEditable(Object node, int column) {
		if (!(node instanceof Score)) return false;
		
		Score score = (Score) node;
		
		switch (score.getStatus()) {
			case NEW:
			case TEMPLATE:
				switch (column) {
					case 0: case 1: case 2: case 3: case 4:
						return true;
				}
				
				break;
		}

		return false;
	}

    public void setValueAt(Object value, Object node, int column) {
//    	System.out.println("setting col: " + column + " = " + value);
		if (!(node instanceof Score)) return;
		
		Score score = (Score) node;
		String s = value.toString();
		
		switch (score.getStatus()) {
			case NEW:
			case TEMPLATE:
				score.setStatus(Score.Status.NEW);
				
				switch (column) {
					case 0: score.setComment(s); break;
					case 1:
						try {
							score.setScore(
								s.isEmpty() ? 0 :
								Integer.parseInt(s));
						} catch (Exception e) {}
						break;
						
					case 2:
						score.setRating(0);
						
						if (score instanceof GhScore) {
							for (int i : new Integer[] {3, 4, 5})
								if (GhScore.getRatingIcon(i) == value) {
									score.setRating(i);
									break;
								}
						} else if (score instanceof RbScore) {
							for (int i : new Integer[] {1, 2, 3, 4, 5})
								if (RbScore.getRatingIcon(i) == value) {
									score.setRating(i);
									break;
								}
						}
						break;
						
					case 3:
						try {
							score.setPartHitPercent(1,
								s.isEmpty() ? 0f :
								Integer.parseInt(s) / 100.0f);
						} catch (Exception e) {}
						break;
						
					case 4:
						try {
							score.setPartStreak(1, 
								s.isEmpty() ? 0 :
								Integer.parseInt(s));
						} catch (Exception e) {}
						break;
						
					default:
						throw new IllegalStateException("trying to set valut at a non-editable column: " + column);
				}
				
				Session sess = null;
				Transaction tx = null;
				
				try {
					sess = HibernateUtil.getCurrentSession();
					tx = sess.beginTransaction();
					sess.saveOrUpdate(score);
					sess.getTransaction().commit();
				} catch (Exception e) {
					if (null != tx) tx.rollback();
					LOG.log(Level.SEVERE, "Failed to save to database", e);
					ErrorInfo ei = new ErrorInfo("Error", "Failed to save to database", null, null, e, null, null);
					JXErrorPane.showDialog(null, ei);
				} finally {
					if (sess.isOpen()) sess.close();
				}
				
				break;
				
			default:
				throw new IllegalStateException("setValueAt() called on a non NEW/TEMPLATE score");
		}
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
		return node instanceof Score;
	}
}

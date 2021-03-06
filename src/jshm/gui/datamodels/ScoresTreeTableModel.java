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
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;

import jshm.Difficulty;
import jshm.Game;
import jshm.Score;
import jshm.Song;
import jshm.Instrument.Group;
import jshm.Song.Sorting;
import jshm.exceptions.SongHiddenException;
import jshm.gh.GhScore;
import jshm.gui.GUI;
import jshm.gui.GuiUtil;
import jshm.gui.editors.ScoresEditor;
import jshm.gui.editors.ScoresRatingEditor;
import jshm.gui.renderers.ScoresCellRenderer;
import jshm.gui.renderers.ScoresFcHighlighter;
import jshm.gui.renderers.ScoresNewScoreHighlighter;
import jshm.gui.renderers.ScoresNoCommentHighlighter;
import jshm.gui.renderers.ScoresPercentHighlighter;
import jshm.gui.renderers.ScoresTreeCellRenderer;
import jshm.gui.renderers.TierHighlighter;
import jshm.hibernate.HibernateUtil;
import jshm.rb.RbScore;
import jshm.wt.WtScore;

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
public class ScoresTreeTableModel extends AbstractTreeTableModel implements Parentable, SongSortable {
	static final Logger LOG = Logger.getLogger(ScoresTreeTableModel.class.getName());
	
	/*
	 * + Tier Name 1 | \- Song Name 1 | + Score 1 | \- Score 2 | \- Song Name 2
	 * ...
	 */
	private class DataModel {
		List<Tier>	tiers	= new ArrayList<Tier>();

		public DataModel() {
			for (int i = 1; i <= game.getTierCount(sorting); i++) {
				tiers.add(new Tier(game.getTierName(sorting, i)));
			}
			
			for (Song song : songs) {
				try {
					tiers.get(song.getTierLevel(sorting) - 1).songs.add(
						new SongScores(song));
				} catch (IndexOutOfBoundsException e) {
					LOG.log(java.util.logging.Level.WARNING,
							String.format(
								"Got song with higher tier (%s) than total tier count (%s)",
								song.getTierLevel(sorting), tiers.size()),
						e);
						LOG.warning("SongOrder: " + song.getSongOrder());
					}
			}

			for (Score score : scores) {
				try {
					tiers.get(Math.max(score.getSong().getTierLevel(sorting) - 1, 0))
						.addScore(score);
				} catch (IndexOutOfBoundsException e) {
					LOG.log(java.util.logging.Level.WARNING,
						String.format(
							"Got score with higher tier (%s) than total tier count (%s)",
							score.getSong().getTierLevel(sorting), tiers.size()),
						e);
						LOG.warning("SongOrder: " + score.getSong().getSongOrder());
				}
			}
		}
	}

	public class Tier implements ModelTier {
		public final String				name;
		public final List<SongScores>	songs = new ArrayList<SongScores>();

		public Tier(String name) {
			this.name = name;
		}

		public SongScores getSongScores(Song song) {
			for (SongScores ss : songs) {
				if (ss.song.equals(song)) {
					return ss;
				}
			}
			
			return null;
		}
		
		/**
		 * 
		 * @param score
		 * @return <code>null</code> if the score was null or already in the list
		 * 			otherwise the {@link SongScores} it was inserted into is returned.
		 */
		public SongScores addScore(Score score) {
			SongScores ss = getSongScores(score.getSong());
			
			if (null == ss || ss.scores.contains(score)) {
				return null;
			}
					
			ss.scores.add(score);
			return ss;
		}
		
		public SongScores removeScore(Score score) {
			SongScores ss = getSongScores(score.getSong());
			
			if (null != ss) {
				ss.scores.remove(score);
				return ss;
			}
			
			return null;
		}
		
		public int getScoreCount() {
			int scores = 0;
			
			for (SongScores song : songs) {
				scores += song.scores.size();
			}
			
			return scores;
		}
		
		public boolean hasAnyScores() {
			for (SongScores ss : songs) {
				if (ss.hasAnyScores())
					return true;
			}
			
			return false;
		}

		public String toString() {
			int scores = getScoreCount();
			
			return String.format("%s (%d %s)", name, scores,
					scores != 1 ? "scores" : "score")
//					+ " @" + Integer.toHexString(hashCode())
					;
		}
		
		public boolean equals(Object o) {
			if (this == o) return true;
			if (!(o instanceof Tier)) return false;
			return this.name.equals(((Tier) o).name);
		}
	}

	public class SongScores {
		public final Song			song;
		public final List<Score>	scores	= new ArrayList<Score>();

		public SongScores(Song song) {
			this.song = song;
		}

		public int getScoreCount() {
			return scores.size();
		}
		
		public boolean hasAnyScores() {
			return scores.size() > 0;
		}
		
		public String toString() {
			return String.format("%s (%d %s)", song.getTitle(), scores.size(),
					scores.size() != 1 ? "scores" : "score")
//					+ " @" + Integer.toHexString(hashCode())
					;
		}
		
		public boolean equals(Object o) {
			if (this == o) return true;
			if (!(o instanceof SongScores)) return false;
			return this.song.equals(((SongScores) o).song);
		}
	}

	private JXTreeTable parent;
	private DataModel	model;
	private final Game game;
	private final Group	group;
	private final Difficulty diff;
	private Sorting sorting = null;
	private final List<? extends Song> songs;
	private final List<Score> scores;

	public ScoresTreeTableModel(
			final Game game, final Group group, final Difficulty diff, final Sorting sorting,
			final List<? extends Song> songs, final List<Score> scores) {

		super("ROOT");
		this.game = game;
		this.group = group;
		this.diff = diff;
		this.songs = songs;
		this.scores = scores;

		setSorting(sorting);
	}
	
	public boolean displayEmptyScores = true;
	
	public void setSorting(final Sorting sorting) {
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
		myParentTreeWillExpandListener.lastExpanded = null;
		model = new DataModel();
		
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
	
	public void setDisplayEmptyScores(boolean b) {
		if (displayEmptyScores == b) return;
		
		// TODO allow it to toggle in place without removing and re-adding everything

		TreePath tp = new TreePath(root); // ROOT
		int[] indices = null;
		Object[] children = null;
		
		// remove all existing children
		indices = new int[getChildCount(root)];
		children = new Object[indices.length];
		
		for (int i = 0; i < indices.length; i++) {
			indices[i] = i;
			children[i] = getChild(root, i);
		}
		
		modelSupport.fireChildrenRemoved(
			tp, indices, children);
		
		displayEmptyScores = b;
		myParentTreeWillExpandListener.lastExpanded = null;
		
		// add tiers
		indices = new int[getChildCount(root)];
		children = new Object[indices.length];
		
		for (int i = 0; i < indices.length; i++) {
			indices[i] = i;
			children[i] = getChild(root, i);
		}
		
		modelSupport.fireChildrenAdded(
			tp, indices, children);
	}
	
	public void createScoreTemplate(Game game, Group group, Difficulty difficulty, TreePath p) {
		if (null == p) return;
		
		Object o = p.getLastPathComponent();
//		System.out.println("Selected: " + p);
		
		if (o instanceof Score) {
			createScoreTemplate(game, group, difficulty, (Score) o);
			p = p.getParentPath();
		} else if (o instanceof SongScores) {
			createScoreTemplate(game, group, difficulty, (SongScores) o);
		}
		
//		modelSupport.fireTreeStructureChanged(p);
	}
	
	private void createScoreTemplate(Game game, Group group, Difficulty difficulty, SongScores selectedSongScores) {
		insertScore(
			game.createNewScoreTemplate(
				group, difficulty, selectedSongScores.song));
	}
	
	private void createScoreTemplate(Game game, Group group, Difficulty difficulty, Score selectedScore) {
		insertScore(
			game.createNewScoreTemplate(
				group, difficulty, selectedScore.getSong()));
	}
	
	public void insertScore(Score score) {
		Song song = score.getSong();
		Tier tier = model.tiers.get(song.getTierLevel(sorting) - 1);
		boolean isFirstScoreInTier = !tier.hasAnyScores();
		SongScores ss = tier.addScore(score);
		
		if (null != ss) {
			// inserting a new score
			scores.add(score);
			
			boolean isFirstScoreInSs = ss.getScoreCount() == 1;
			
			TreePath tp = new TreePath(root); // ROOT
			int tierIndex = getIndexOfChild(root, tier);
			
			if (!displayEmptyScores && isFirstScoreInTier) {
	//				System.out.println("FIRST score in tier");
				modelSupport.fireChildAdded(tp, tierIndex, tier);
			}
			
			tp = tp.pathByAddingChild(getChild(root, tierIndex)); // ROOT -> Tier
			int ssIndex = getIndexOfChild(tp.getLastPathComponent(), ss);
			
			if (!displayEmptyScores && isFirstScoreInSs) {
	//				System.out.println("FIRST score in ss");
				modelSupport.fireChildAdded(tp, ssIndex, ss);
			}
			
			tp = tp.pathByAddingChild(ss); // ROOT -> Tier -> Song
			int scoreIndex = getIndexOfChild(ss, score);
			modelSupport.fireChildAdded(tp, scoreIndex, score);
	
			tp = tp.pathByAddingChild(score); // ROOT -> Tier -> Song -> Score
			
			parent.packAll();
			expandAndScrollTo(tp, true);
		}
		// else we're actually updating an existing score
	}
	
	public void expandAndScrollTo(final Song song, final boolean selectRow) throws SongHiddenException {
		Tier tier = model.tiers.get(song.getTierLevel(sorting) - 1);
		SongScores ss = tier.getSongScores(song);
		
		assert null != ss;
		
		if (!isNodeVisible(ss)) {
			throw new SongHiddenException();
		}
		
		TreePath tp = new TreePath(new Object[] {
			root, tier, ss
		});
		
//        StringBuilder tempSpot = new StringBuilder("[");
//
//        for (int counter = 0, maxCounter = tp.getPathCount(); counter < maxCounter; counter++) {
//            if(counter > 0)
//                tempSpot.append(", ");
//            
//            tempSpot.append(tp.getPathComponent(counter));
//            tempSpot.append('@');
//            tempSpot.append(Integer.toHexString(tp.getPathComponent(counter).hashCode()));
//        }
//        tempSpot.append("]");
//		
//        System.out.println("ex+scroll to: " + tempSpot.toString());
        
		expandAndScrollTo(tp, selectRow);
	}
	
	private void expandAndScrollTo(final TreePath tp, final boolean selectRow) {
		int row, rowToSelect;
		int visibleRowCount = parent.getVisibleRowCount();
		
		if (tp.getLastPathComponent() instanceof Score) {
			TreePath parentPath = tp.getParentPath();
			parent.expandPath(parentPath);
			int scoreCount = ((SongScores) parentPath.getLastPathComponent()).getScoreCount();

			rowToSelect = parent.getRowForPath(tp);
			// if there's enough space make the song title visible
			if (scoreCount < visibleRowCount) {
				row = parent.getRowForPath(parentPath);
			} else {
				row = rowToSelect;
			}
		} else {
			parent.expandPath(tp);
			row = parent.getRowForPath(tp);
			rowToSelect = row;
		}
		
		
		// make it so tp appears near the top
		
		parent.scrollRowToVisible(
			Math.min(
				parent.getRowCount() - 1,
				row + visibleRowCount
		));
		
		parent.scrollRowToVisible(row);
		
		if (selectRow)
			parent.getSelectionModel()
			.setSelectionInterval(rowToSelect, rowToSelect);
		
		parent.packAll();
	}

	public void deleteScore(TreePath p) {
		deleteScore(p, false);
	}
	
	public void deleteScore(TreePath p, boolean deleteFromScoreHero) {
		if (!(p.getLastPathComponent() instanceof Score)) return;
		
		// p = ROOT -> Tier -> Song -> Score
		
		Score score = (Score) p.getLastPathComponent();
		
		LOG.finer("Going to delete score: " + score);
				
		Tier tier = model.tiers.get(score.getSong().getTierLevel(sorting) - 1);
		int tierIndex = getIndexOfChild(root, tier);
		SongScores ss = tier.getSongScores(score.getSong());
		int ssIndex = getIndexOfChild(tier, ss);
		int scoreIndex = getIndexOfChild(ss, score);
		tier.removeScore(score);
		boolean wasLastSsScore = !ss.hasAnyScores();
		boolean wasLastTierScore = !tier.hasAnyScores();
		
		scores.remove(score);
		
		assert null != ss;
		
		p = p.getParentPath(); // ROOT -> Tier -> Song
		modelSupport.fireChildRemoved(p, scoreIndex, score);

		p = p.getParentPath(); // ROOT -> Tier
		
		if (!displayEmptyScores && wasLastSsScore) {
//					System.out.println("LAST score in ss");
			modelSupport.fireChildRemoved(p, ssIndex, ss);
		}
		
		p = p.getParentPath(); // ROOT
		
		if (!displayEmptyScores && wasLastTierScore) {
//					System.out.println("LAST score in tier");
			modelSupport.fireChildRemoved(p, tierIndex, tier);
		}
		
		parent.packAll();
		
		if (Score.Status.TEMPLATE != score.getStatus()) {
			Session sess = null;
			Transaction tx = null;
			
			try {
				sess = HibernateUtil.getCurrentSession();
				tx = sess.beginTransaction();
				sess.delete(score);
				tx.commit();
				LOG.finer("Score deleted from DB");
			} catch (Exception e) {
				if (null != tx) tx.rollback();
				LOG.log(Level.SEVERE, "Failed to delete score from database", e);
				ErrorInfo ei = new ErrorInfo("Error", "Failed to delete score from database", null, null, e, null, null);
				JXErrorPane.showDialog(null, ei);
				return;
			} finally {
				if (sess.isOpen()) sess.close();
			}
		}
	}
	
	/**
	 * 
	 * @return A new instance of {@link ScoresTreeTableModel}
	 * that contains only the new scores that this model contains.
	 */
	public ScoresTreeTableModel createSubmittableScoresModel() {
		List<Song> songs = new ArrayList<Song>();
		List<Score> newScores = new ArrayList<Score>();
		
		for (Tier t : model.tiers) {
			for (SongScores ss : t.songs) {
				for (Score s : ss.scores) {
					if (s.isSubmittable()) {
						newScores.add(s);
						
						if (!songs.contains(s.getSong()))
							songs.add(s.getSong());
					}
				}
			}
		}
		
		ScoresTreeTableModel ret =
			new ScoresTreeTableModel(game, group, diff, sorting, songs, newScores);
		ret.setDisplayEmptyScores(false);
		return ret;
	}
	
	/**
	 * 
	 * @return The total number of scores this model contains.
	 */
	public int getScoreCount() {
		int count = 0;
		
		for (Tier t : model.tiers) {
			count += t.getScoreCount();
		}
		
		return count;
	}
	
	public int getSubmittableScoreCount() {
		int count = 0;
		
		for (Tier t : model.tiers) {
			for (SongScores ss : t.songs) {
				for (Score score : ss.scores) {
					if (score.isSubmittable())
						count++;
				}
			}
		}
		
		return count;
	}
	
	public boolean hasSubmittableScores() {
		for (Tier t : model.tiers) {
			for (SongScores ss : t.songs) {
				for (Score score : ss.scores) {
					if (score.isSubmittable())
						return true;
				}
			}
		}
		
		return false;
	}
	
	public List<? extends Score> getScores() {		
		return scores;
	}
	
	private MouseListener myParentMouseListener = new MouseAdapter() {
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
			
			if (!(o instanceof Score)) return;
			
			Score score = (Score) o;
			
			// can still double-click to edit score without opening
			// a browser window
			if (score.isEditable()) return;
			
			// preference is given to the video link
			// if both are present
			String url =
				!score.getVideoUrl().isEmpty()
				? score.getVideoUrl()
				: !score.getImageUrl().isEmpty()
				  ? score.getImageUrl()
				  : null;
				  
			boolean isVideo = !score.getVideoUrl().isEmpty();
			
			if (null != url) {
				if (isVideo || GUI.CTRL_MASK == (GUI.CTRL_MASK & e.getModifiers()))
					jshm.util.Util.openURL(url);
				else
					GuiUtil.openImageOrBrowser(null, url);
			}
		}
	};
	
	private MouseMotionListener myParentMouseMotionListener = new MouseMotionAdapter() {
		public void mouseMoved(MouseEvent e) {
			int row = parent.rowAtPoint(e.getPoint());
			int column = parent.columnAtPoint(e.getPoint());
			
			Cursor newCursor = null; //Cursor.getDefaultCursor();
			
			Object o = parent.getModel().getValueAt(row, column);
			
			if (1 == column && o instanceof Score) {
				Score score = (Score) o;
				
				if (!score.isEditable() && 
					(!score.getImageUrl().isEmpty() || !score.getVideoUrl().isEmpty())) {
					newCursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
				}
			}
			
			parent.setCursor(newCursor);
		}
	};
	
	private MyParentTreeWillExpandListener myParentTreeWillExpandListener =
		new MyParentTreeWillExpandListener();
	
	// need to make a non-anonymous class so i can null out lastExpanded
	private class MyParentTreeWillExpandListener implements TreeWillExpandListener {
		public void treeWillCollapse(TreeExpansionEvent event)
				throws ExpandVetoException {} // not interested

		Object lastExpanded = null;
		
		public void treeWillExpand(TreeExpansionEvent event)
				throws ExpandVetoException {
			Object o = event.getPath().getLastPathComponent();
//			System.out.println(event.getPath());
//			System.out.println(o.getClass().getCanonicalName() + ": " + o);
//			System.out.println();
			
			if (lastExpanded != o && o instanceof Tier) {
				lastExpanded = o;
				GuiUtil.expandTreeBelowNode(parent, event.getPath());
			}
		}
	};
	
	// TODO some of the stuff in here might not necessary fit 
	// as part of the "data model"/mvc stuff...
	public void setParent(final GUI gui, final JXTreeTable parent) {
		this.parent = parent;
		
		ScoresTreeCellRenderer treeRenderer = new ScoresTreeCellRenderer();
		parent.setTreeCellRenderer(treeRenderer);
		
		ScoresCellRenderer renderer = new ScoresCellRenderer();
		
		for (int i = 0; i < getColumnCount(); i++)
			parent.getColumn(i).setCellRenderer(renderer);

		parent.addMouseListener(myParentMouseListener);
		parent.addMouseMotionListener(myParentMouseMotionListener);
		parent.addTreeWillExpandListener(myParentTreeWillExpandListener);
		
		if (null != gui)
			displayEmptyScores = !gui.getHideEmptyScores();
		
		Highlighter[] highlighters = new Highlighter[] {
			HighlighterFactory.createSimpleStriping(),
			new ScoresPercentHighlighter(),
			new ScoresFcHighlighter(),
			new TierHighlighter(),
			new ScoresNoCommentHighlighter(),
			new ScoresNewScoreHighlighter()
		};
		
		parent.setHighlighters(highlighters);

		Object[] prototypes = new Object[] {
			"00000000000", GhScore.getRatingIcon(8),
			"00000", "99999", "AAA9999999999", "AAA9999999999"
		};
		
		for (int i = 1; i <= 6; i++)
			parent.getColumnExt(i).setPrototypeValue(prototypes[i - 1]);

		parent.getColumnExt(5).setVisible(false);

		parent.setEditable(true);	

		
//		parent.setHierarchicalEditor(
//			new GhMyScoresCommentEditor(   ));
		ScoresEditor editor = new ScoresEditor();
		parent.setDefaultEditor(Score.class, editor);
//		parent.getColumn(0).setCellEditor(editor); // has no effect....
//		parent.getColumn(1).setCellEditor(editor);
		parent.getColumn(2).setCellEditor(new ScoresRatingEditor(game));
//		parent.getColumn(3).setCellEditor(editor);
//		parent.getColumn(4).setCellEditor(editor);
		
		parent.setHorizontalScrollEnabled(false);
		parent.setAutoResizeMode(JXTreeTable.AUTO_RESIZE_OFF);
		parent.packAll();
	}

	public void removeParent(JXTreeTable parent) {
		parent.removeTreeWillExpandListener(myParentTreeWillExpandListener);
		parent.removeMouseListener(myParentMouseListener);
		parent.removeMouseMotionListener(myParentMouseMotionListener);
		this.parent = null;
	}
	
//	// TODO this shouldn't be necessary
//	public void resetParent() {
//		if (null == this.parent) return;
//		
//		JXTreeTable tmp = this.parent;
//		removeParent(tmp);
//		setParent(gui, tmp);
//	}

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
	public int getColumnCount() {
		return COLUMN_NAMES.length;
	}

	@Override
	public Object getValueAt(Object node, int column) {
//		if (!isNodeVisible(node))
//			throw new IllegalArgumentException("node not visible");
		
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
		
		if (score.isEditable()) {
			switch (column) {
				case 0: case 1: case 2: case 3: case 4:
					return true;
			}
		}

		return false;
	}

    public void setValueAt(Object value, Object node, int column) {
//    	System.out.println("setting col: " + column + " = " + value);
		if (!(node instanceof Score)) return;
		
		Score score = (Score) node;
		String s = value.toString();
		
		if (score.isEditable()) {
			if (score.getStatus() == Score.Status.TEMPLATE)
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
					
					if (score instanceof GhScore || score instanceof WtScore) {
						for (int i : new int[] {3, 4, 5})
							if (GhScore.getRatingIcon(i) == value) {
								score.setRating(i);
								break;
							}
					} else if (score instanceof RbScore) {
						for (int i : new int[] {1, 2, 3, 4, 5, 6})
							if (RbScore.getRatingIcon(i) == value) {
								score.setRating(i);
								break;
							}
					} else {
						assert false: "unimplemented score subclass";
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
		}
    }
	
    public boolean isNodeVisible(Object node) {
    	if (displayEmptyScores || node == root)
    		return true;
    	
    	if (node instanceof Tier) {
//    		System.out.println("TIER is visible? " + ((Tier) node).hasAnyScores() + " - " + node);
    		return ((Tier) node).hasAnyScores();
    	}
    	
    	if (node instanceof SongScores) {
    		return ((SongScores) node).hasAnyScores();
    	}
    	
    	return true;
    }
    
	@Override
	public Object getChild(Object parent, int index) {
		int count = 0;
		
		if (!isNodeVisible(parent))
			return null;
		
//		System.out.println("CHILD at " + index + " FOR - " + parent);
		
		if (parent.equals(root)) {
			if (displayEmptyScores)
				return model.tiers.get(index);
			
			for (Tier t : model.tiers) {
				if (!isNodeVisible(t))
					continue;
				if (count == index) {
//					System.out.println("ROOT child at " + index + " is " + t);
					return t;
				}
				
				count++;
			}
		}

		if (parent instanceof Tier) {
			if (displayEmptyScores)
				return ((Tier) parent).songs.get(index);
			
			for (SongScores ss : ((Tier) parent).songs) {
				if (!isNodeVisible(ss))
					continue;
				if (count == index)
					return ss;
				
				count++;
			}
		}

		// always shown, no index trickery needed
		if (parent instanceof SongScores) {
			return ((SongScores) parent).scores.get(index);
		}

		return null;
	}

	@Override
	public int getChildCount(Object parent) {
		int count = 0;
		
//		System.out.println("CHILD COUNT for " + parent);
		
		if (parent.equals(root)) {
			if (displayEmptyScores)
				return model.tiers.size();
			
			for (Tier t : model.tiers) {
				if (isNodeVisible(t)) {
					count++;
				}
			}
			
//			System.out.println("ROOT child count = " + count);
			return count;
		}

		if (parent instanceof Tier) {
			Tier tier = (Tier) parent;
			
			if (displayEmptyScores)
				return tier.songs.size();
			
			for (SongScores ss : tier.songs) {
				if (isNodeVisible(ss)) {
					count++;
				}
			}
			
			return count;
		}

		// always show
		if (parent instanceof SongScores) {
			return ((SongScores) parent).scores.size();
		}

		return 0;
	}

	@Override
	public int getIndexOfChild(Object parent, Object child) {
		int count = 0;
		
		if (!(isNodeVisible(parent) && isNodeVisible(child)))
			return -1;
		
		if (parent.equals(root)) {
			if (displayEmptyScores)
				return model.tiers.indexOf(child);
			
			for (Tier t : model.tiers) {
				if (!isNodeVisible(t))
					continue;
				if (t == child)
					return count;
					
				count++;
			}
		}

		if (parent instanceof Tier) {
			if (displayEmptyScores)
				return ((Tier) parent).songs.indexOf(child);
			
			for (SongScores ss : ((Tier) parent).songs) {
				if (!isNodeVisible(ss))
					continue;
				if (ss == child)
					return count;
				
				count++;
			}
		}

		// always shown
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

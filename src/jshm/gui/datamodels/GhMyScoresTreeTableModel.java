package jshm.gui.datamodels;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.*;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;

import jshm.Score;
import jshm.gh.GhGame;
import jshm.gh.GhScore;
import jshm.gh.GhSong;
import jshm.gui.GuiUtil;
import jshm.gui.editors.GhMyScoresEditor;
import jshm.gui.editors.GhMyScoresRatingEditor;
import jshm.gui.renderers.GhMyScoresCellRenderer;
import jshm.gui.renderers.GhMyScoresFcHighlighter;
import jshm.gui.renderers.GhMyScoresNewScoreHighlighter;
import jshm.gui.renderers.GhMyScoresNoCommentHighlighter;
import jshm.gui.renderers.GhMyScoresTreeCellRenderer;
import jshm.gui.renderers.GhTierHighlighter;
import jshm.hibernate.HibernateUtil;

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
public class GhMyScoresTreeTableModel extends AbstractTreeTableModel {
	static final Logger LOG = Logger.getLogger(GhMyScoresTreeTableModel.class.getName());
	
	/*
	 * + Tier Name 1 | \- Song Name 1 | + Score 1 | \- Score 2 | \- Song Name 2
	 * ...
	 */
	private class DataModel {
		List<Tier>	tiers	= new ArrayList<Tier>();

		public DataModel(final GhGame game, final List<GhSong> songs,
				final List<GhScore> scores) {

			for (int i = 1; i <= game.getTierCount(); i++) {
				tiers.add(new Tier(game.getTierName(i)));
			}

			for (GhSong song : songs) {
				tiers.get(song.getTierLevel() - 1).songs.add(new SongScores(
						song));
			}

			for (GhScore score : scores) {
				tiers.get(((GhSong) score.getSong()).getTierLevel() - 1)
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

		public final List<SongScores>	songs	= new ArrayList<SongScores>();

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
		
		public void removeScore(GhScore score) {
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
		public final GhSong			song;

		public final List<GhScore>	scores	= new ArrayList<GhScore>();

		public SongScores(GhSong song) {
			this.song = song;
		}

		public String toString() {
			return String.format("%s (%d %s)", song.getTitle(), scores.size(),
					scores.size() != 1 ? "scores" : "score");

		}
	}

	// private JXTreeTable parent;
	private final DataModel	model;
	private final GhGame game;

	public GhMyScoresTreeTableModel(final GhGame game,
			final List<GhSong> songs, final List<GhScore> scores) {

		super("ROOT");
		this.game = game;
		this.model = new DataModel(game, songs, scores);
	}
	
	public void createScoreTemplate(TreePath p) {
		if (null == p) return;
		
		Object o = p.getLastPathComponent();
//		System.out.println("Selected: " + o);
		
		if (o instanceof GhScore) {
			createScoreTemplate((GhScore) o);
			p = p.getParentPath();
		} else if (o instanceof SongScores) {
			createScoreTemplate((SongScores) o);
		}
		
		TreeModelEvent e = new TreeModelEvent(this, p);
		
		for (TreeModelListener l : getTreeModelListeners())
			l.treeStructureChanged(e);
	}
	
	private void createScoreTemplate(SongScores selectedSongScores) {
		selectedSongScores.scores.add(
			GhScore.createNewScoreTemplate(selectedSongScores.song));
	}
	
	private void createScoreTemplate(GhScore selectedScore) {
		GhSong song = (GhSong) selectedScore.getSong();
		
		model.tiers.get(song.getTierLevel() - 1)
			.addScore(GhScore.createNewScoreTemplate(song));
	}

	public void deleteScore(TreePath p) {
		if (!(p.getLastPathComponent() instanceof GhScore)) return;
		
		GhScore score = (GhScore) p.getLastPathComponent();
		
		switch (score.getStatus()) {
			case NEW:
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
				model.tiers.get(((GhSong) score.getSong()).getTierLevel() - 1)
					.removeScore(score);
				
				TreeModelEvent e = new TreeModelEvent(this, p.getParentPath());
				for (TreeModelListener l : getTreeModelListeners())
					l.treeStructureChanged(e);
				
				break;
				
			default:
				// ignore
		}
	}
	
	/**
	 * 
	 * @return A new instance of {@link GhMyScoresTreeTableModel}
	 * that contains only the new scores that this model contains.
	 */
	public GhMyScoresTreeTableModel createNewScoresModel() {
		List<GhSong> songs = new ArrayList<GhSong>();
		List<GhScore> newScores = new ArrayList<GhScore>();
		
		for (Tier t : model.tiers) {
			for (SongScores ss : t.songs) {
				for (GhScore s : ss.scores) {
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
	
	public List<GhScore> getScores() {
		List<GhScore> scores = new ArrayList<GhScore>();
		
		for (Tier t : model.tiers) {
			for (SongScores ss : t.songs) {
				scores.addAll(ss.scores);
			}
		}
		
		return scores;
	}
	
	public void setParent(JXTreeTable parent) {
		// this.parent = parent;
		
		GhMyScoresTreeCellRenderer treeRenderer = new GhMyScoresTreeCellRenderer();
		parent.setTreeCellRenderer(treeRenderer);
		
		GhMyScoresCellRenderer renderer = new GhMyScoresCellRenderer();
		
		for (int i = 0; i <= 6; i++)
			parent.getColumn(i).setCellRenderer(renderer);

		Highlighter[] highlighters = new Highlighter[] {
			HighlighterFactory.createSimpleStriping(),
			new GhMyScoresFcHighlighter(),
			new GhTierHighlighter(),
			new GhMyScoresNoCommentHighlighter(),
			new GhMyScoresNewScoreHighlighter()
		};
		
		parent.setHighlighters(highlighters);

		GuiUtil.expandTreeFromDepth(parent, 2);

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
		parent.setDefaultEditor(GhScore.class, editor);
//		parent.getColumn(0).setCellEditor(editor);
//		parent.getColumn(1).setCellEditor(editor);
		parent.getColumn(2).setCellEditor(new GhMyScoresRatingEditor());
//		parent.getColumn(3).setCellEditor(editor);
//		parent.getColumn(4).setCellEditor(editor);
		
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
			case 0: return String.class;
			case 1:	case 2: case 3: case 4:
				return GhScore.class;
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
		"Score", "Rating (Calc)", "%", "Streak", "Date Created", "Date Submitted" };

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

		if (!(node instanceof GhScore))
			return "";

		GhScore score = (GhScore) node;

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
		if (!(node instanceof GhScore)) return false;
		
		GhScore score = (GhScore) node;
		
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
		if (!(node instanceof GhScore)) return;
		
		GhScore score = (GhScore) node;
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
						
						for (int i : new Integer[] {3, 4, 5})
							if (GhScore.getRatingIcon(i) == value) {
								score.setRating(i);
								break;
							}
						break;
						
					case 3:
						try {
							score.getPart(1).setHitPercent(
								s.isEmpty() ? 0f :
								Integer.parseInt(s) / 100.0f);
						} catch (Exception e) {}
						break;
						
					case 4:
						try {
							score.setStreak(
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
		return node instanceof GhScore;
	}
}

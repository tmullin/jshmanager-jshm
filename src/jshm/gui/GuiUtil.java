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
package jshm.gui;

import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.ListCellRenderer;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;
import javax.swing.tree.TreePath;

import jshm.Config;
import jshm.Song;
import jshm.gui.components.SpInfoViewer;
import jshm.gui.renderers.ScoreEditorSongComboRenderer;
import jshm.util.Util;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.methods.HeadMethod;
import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import org.jdesktop.swingx.autocomplete.ObjectToStringConverter;
import org.jdesktop.swingx.error.ErrorInfo;
import org.jdesktop.swingx.treetable.TreeTableModel;

public class GuiUtil {
	/**
	 * This sets the default L&F as well as sets some icons
	 */
	public static void init() {
		try {
			// Set the Look & Feel to match the current system
			UIManager.setLookAndFeel(
				UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {}
		
		// http://www.java2s.com/Tutorial/Java/0240__Swing/CustomizingaJOptionPaneLookandFeel.htm
		UIManager.put("OptionPane.errorIcon", new ImageIcon(GuiUtil.class.getResource("/jshm/resources/images/toolbar/delete.png")));
		UIManager.put("OptionPane.informationIcon", new ImageIcon(GuiUtil.class.getResource("/jshm/resources/images/toolbar/infoabout.png")));
		UIManager.put("OptionPane.questionIcon", new ImageIcon(GuiUtil.class.getResource("/jshm/resources/images/toolbar/help.png")));
		UIManager.put("OptionPane.warningIcon", new ImageIcon(GuiUtil.class.getResource("/jshm/resources/images/toolbar/pause.png")));
		UIManager.put("OptionPane.yesIcon", new ImageIcon(GuiUtil.class.getResource("/jshm/resources/images/toolbar/accept32.png")));
		UIManager.put("OptionPane.noIcon", new ImageIcon(GuiUtil.class.getResource("/jshm/resources/images/toolbar/delete32.png")));
		

		float scale = Config.getFloat("font.scale"); 
		
		if (1f != scale) {
			Enumeration<Object> keys = UIManager.getDefaults().keys();
			while (keys.hasMoreElements()) {
				Object key = keys.nextElement();
				Object value = UIManager.get(key);
				
				if (value instanceof FontUIResource) {
					FontUIResource f = (FontUIResource) value;
					f = new FontUIResource(f.deriveFont(scale * f.getSize2D()));
					UIManager.put(key, f);
				}
			}
		}
	}
	
	/**
	 * Expands all nodes of the supplied tree starting at startDepth.
	 * This should only be used when there is a known maximum depth.
	 * @param tree
	 * @param startDepth
	 */
	public static void expandTreeFromDepth(JXTreeTable tree, int startDepth) {
		TreeTableModel model = tree.getTreeTableModel();
		Object root = model.getRoot();
		
		tree.expandAll();
//		expandTreeFromDepth(tree, model, root, null, 0, startDepth);
		collapseTreeToDepth(tree, model, root, new TreePath(root), 0, startDepth);
	}
	
//	private static void expandTreeFromDepth(JXTreeTable tree, TreeTableModel model, Object parent, TreePath path, int curDepth, int startDepth) {		
//		if (curDepth >= startDepth && model.isLeaf(parent)) {
//			System.out.println("expanding: " + path);
//			tree.expandPath(path);
//			return;
//		} else {
//			path = 
//				null != path
//				? path.pathByAddingChild(parent)
//				: new TreePath(parent);
//		}
//		
//		for (int i = 0; i < model.getChildCount(parent); i++) {
//			Object child = model.getChild(parent, i);
//			
//			expandTreeFromDepth(tree, model, child,
//				path,
//				curDepth + 1, startDepth);
//			
//			if (model.isLeaf(child)) {
//				continue;
//			}
//		}
//	}
	
	private static void collapseTreeToDepth(JXTreeTable tree, TreeTableModel model, Object parent, TreePath path, int curDepth, int stopDepth) {
		curDepth++;
		
		if (curDepth == stopDepth) {
//			System.out.println("collapsing: " + path);
			tree.collapsePath(path);
			return;
		}
		
		for (int i = 0; i < model.getChildCount(parent); i++) {
			Object child = model.getChild(parent, i);
			
			if (model.isLeaf(child)) {
				continue;
			}
			
			collapseTreeToDepth(tree, model, child,
				path.pathByAddingChild(child),
				curDepth, stopDepth);
		}
	}
	
	
	public static List<TreePath> getExpandedPaths(final JXTreeTable tree) {
		final TreeTableModel model = tree.getTreeTableModel();
		final List<TreePath> paths = new ArrayList<TreePath>();
		
		getExpandedPaths(tree, model, new TreePath(model.getRoot()), paths);
		
		for (TreePath p : paths)
			System.out.println(p);
		
		return paths;
	}
	
	
	private static void getExpandedPaths(JXTreeTable tree, TreeTableModel model, TreePath path, List<TreePath> paths) {
//		System.out.println("Checking: " + path);
		
		Object parent = path.getLastPathComponent();
		final int childCount = model.getChildCount(parent);
		
		for (int i = 0; i < childCount; i++) {
			Object curNode = model.getChild(parent, i);
			TreePath curPath = path.pathByAddingChild(curNode);
			
			if (model.isLeaf(curNode)) continue;
			
			if (tree.isExpanded(curPath))
				paths.add(curPath);
			
			getExpandedPaths(tree, model, curPath, paths);	
		}
	}
	
	public static void restoreExpandedPaths(final JXTreeTable tree, final List<TreePath> paths) {
//		System.out.println("Restoring " + paths.size());
		
		for (TreePath p : paths) {
			tree.expandPath(p);
		}
	}
	

	public static final String SELECT_A_SONG = "Type a song name...";
	private static final ListCellRenderer SONG_COMBO_RENDERER = new ScoreEditorSongComboRenderer();
	private static final ObjectToStringConverter SONG_COMBO_CONVERTER = new ObjectToStringConverter() {
		@Override
		public String getPreferredStringForItem(Object item) {
			if (null == item) return null;
			if (item instanceof Song)
				return ((Song) item).getTitle();
			return item.toString();
		}
	};
	
	public static void createSongCombo(JComboBox cb, List<? extends Song> songs) {
		cb.setRenderer(SONG_COMBO_RENDERER);
		DefaultComboBoxModel model = (DefaultComboBoxModel) cb.getModel();
		model.removeAllElements();
		
		model.addElement(SELECT_A_SONG);
		for (Song s : songs)
			model.addElement(s);
		
		AutoCompleteDecorator.decorate(cb, SONG_COMBO_CONVERTER);
	}
	
	
	// icon stuff
	
	public final static javax.swing.ImageIcon resizeIcon(ImageIcon icon, float scale) {
		return resizeIcon(icon, (int) (scale * icon.getIconWidth()), (int) (scale * icon.getIconHeight()));
	}
	
	/**
	 * Resize an ImageIcon to the specified size.
	 * @param icon The ImageIcon to resize
	 * @param width The new width
	 * @param height The new height
	 */
	public final static javax.swing.ImageIcon resizeIcon(ImageIcon icon, int width, int height) {
		System.out.println("Resizing to " + width + "x" + height);
		Image img = icon.getImage();
		BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		
		Graphics g = bi.createGraphics();
		g.drawImage(img, 0, 0, width, height, null);
		
		return new javax.swing.ImageIcon(bi);
	}
	
	
	public static void openImageOrBrowser(final Frame owner, final String urlStr) {
		new SwingWorker<Void, Void>() {
			Image image = null;
			URL url = null;
			Throwable t = null;
			
			protected Void doInBackground() throws Exception {
				try {
					url = new URL(urlStr);

					// try to see if it's an image before downloading the
					// whole thing
					HeadMethod method = new HeadMethod(url.toExternalForm());
					jshm.sh.Client.getHttpClient().executeMethod(method);
					Header h = method.getResponseHeader("Content-type");
					method.releaseConnection();

					if (h.getValue().toLowerCase().startsWith("image")) {
						image = ImageIO.read(url);
					}
				} catch (Exception e) {
					t = e;
				}
				
				return null;
			}
			
			public void done() {
				if (null == image) {
					if (null == t) {
						// no error, just the url wasn't an image, so launch the browser
						Util.openURL(url.toExternalForm());
					} else {
						ErrorInfo ei = new ErrorInfo("Error", "Error opening image URL", null, null, t, null, null);
						JXErrorPane.showDialog(owner, ei);
					}
				} else {
					SpInfoViewer viewer = new SpInfoViewer();
					viewer.setTitle(url.toExternalForm());
					viewer.setImage(image, true);
					viewer.setLocationRelativeTo(null);
					viewer.setVisible(true);
				}
			}
		}.execute();
	}
}

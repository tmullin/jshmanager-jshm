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

import java.util.ArrayList;
import java.util.List;

import javax.swing.tree.TreePath;

import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.treetable.TreeTableModel;

public class GuiUtil {
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
}

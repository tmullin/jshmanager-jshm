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
package jshm.sh.links;

import java.util.ArrayList;
import java.util.List;

import jshm.sh.GhSongStat;
import jshm.sh.URLs;

public class ScoreDatabaseLink {
	public static final Link GH_ROOT;
	public static final Link RB_ROOT;
	
	static {
		GH_ROOT = new Link("Score Database")
		.add("Browse Scores", URLs.BASE + "/scores.php");
		initGh(GH_ROOT, Type.createGhValues());
		
		RB_ROOT = new Link("Score Database")
		.add("Browse Scores", URLs.rb.BASE + "/scores.php");
		initRb(RB_ROOT, Type.createRbValues());
	}
	
	private static void initGh(Link parent, List<Type> types) {
		for (Type t : types) {
			if (t.children.size() == 0) {
				parent.add(new GhGamesTemplate(t.name, t.urlFmt));
			} else {
				Link cur = new Link(t.name);
				initGh(cur, t.children);
				parent.add(cur);
			}
		}
	}
	
	private static void initRb(Link parent, List<Type> types) {
		for (Type t : types) {
			if (t.children.size() == 0) {
				parent.add(new RbTemplate(t.name, t.urlFmt));
			} else {
				Link cur = new Link(t.name);
				initRb(cur, t.children);
				parent.add(cur);
			}
		}
	}
	
	
	static class Type {		
		public static List<Type> createGhValues() {
			List<Type> values = new ArrayList<Type>();
			
			values.add(new Type("Rankings", "rankings.php?"));
			values.add(new Type("Top Scores", "top_scores.php?"));
			
			
			Type cur = new Type("Compare Users", "compare.php?user=");
			cur.children.add(new Type("Top Scores", cur.urlFmt +"top&"));
			
			for (int i = 4; i <= 9; i++) {
				cur.children.add(new Type(i + "* Cuttoffs", cur.urlFmt + i + "star&"));
			}
			
			values.add(cur);
			
			
			cur = new Type("Song Stats", "songstats.php?stat=");
			
			for (GhSongStat s : GhSongStat.values()) {
				cur.children.add(new Type(s.toString(), cur.urlFmt + s.id + "&"));
			}
			
			values.add(cur);
			
			return values;
		}
		
		public static List<Type> createRbValues() {
			List<Type> values = new ArrayList<Type>();
			
			values.add(new Type("Rankings", "rankings.php?"));
			values.add(new Type("Top Scores", "top_scores.php?"));
			
			Type cur = new Type("Compare Users", "compare.php?user=");
			cur.children.add(new Type("Top Scores", cur.urlFmt +"top&"));
			values.add(cur);
			
			return values;
		}

		
		public final String name;
		public final String urlFmt;
		public final List<Type> children = new ArrayList<Type>();
		
		private Type(String name, String urlFmt) {
			this.name = name;
			this.urlFmt = urlFmt;
		}
	}
}

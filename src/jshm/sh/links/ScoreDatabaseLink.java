package jshm.sh.links;

import java.util.ArrayList;
import java.util.List;

import jshm.sh.GhSongStat;
import jshm.sh.URLs;

public class ScoreDatabaseLink {
	public static final Link GH_ROOT = new Link("Score Database");
	
	static {
		GH_ROOT.add("Browse Scores", URLs.BASE + "/scores.php");
		init(GH_ROOT, Type.createGhValues());
	}
	
	private static void init(Link parent, List<Type> types) {
		for (Type t : types) {
			if (t.children.size() == 0) {
				parent.add(new GhGamesTemplate(t.name, t.urlFmt));
			} else {
				Link cur = new Link(t.name);
				init(cur, t.children);
				parent.add(cur);
			}
		}
	}
	
	
	private static class Type {		
		public static List<Type> createGhValues() {
			List<Type> values = new ArrayList<Type>();
			
			values.add(new Type("Rankings", "rankings.php?game=%s&diff=%s"));
			values.add(new Type("Top Scores", "top_scores.php?game=%s&diff=%s"));
			
			
			Type cur = new Type("Compare Users", "compare.php?user=");
			cur.children.add(new Type("Top Scores", cur.urlFmt +"top&game=%s&diff=%s"));
			
			for (int i = 4; i <= 9; i++) {
				cur.children.add(new Type(i + "* Cuttoffs", cur.urlFmt + i + "star&game=%s&diff=%s"));
			}
			
			values.add(cur);
			
			
			cur = new Type("Song Stats", "songstats.php?stat=");
			
			for (GhSongStat s : GhSongStat.values()) {
				cur.children.add(new Type(s.toString(), cur.urlFmt + s.id + "&game=%s&diff=%s"));
			}
			
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

package jshm.sh.links;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;

import jshm.sh.URLs;

/**
 * This represents the tree-like structure of a menu containing
 * links, like rckr's YUI menus.
 * @author Tim Mullin
 *
 */
public class Link {
	public static final Link GH_ROOT = new Link("GH_ROOT")
		.add("Home", URLs.BASE)
		.add(ForumLink.GH_ROOT)
		.add(new GhGamesTemplate("Manage Scores", "manage_scores.php?")
			.add("Manage Teams", URLs.BASE + "/teams.php"))
		.add(ScoreDatabaseLink.GH_ROOT)
		.add("Leagues", URLs.BASE + "/leagues/")
		.add("Custom Songs", URLs.BASE + "/custom_songs.php")
		.add("Store", URLs.BASE + "/store.php");
	
	public static final Link RB_ROOT = new Link("RB_ROOT")
		.add("Home", URLs.rb.BASE)
		.add(ForumLink.RB_ROOT)
		.add(new Link("Manage Scores", URLs.rb.BASE + "/manage_scores.php")
			.add(new RbTemplate("Manage Scores", "manage_scores.php?"))
			.add("Manage Teams", URLs.rb.BASE + "/teams.php"))
		.add(ScoreDatabaseLink.RB_ROOT);
	
	
	public final String name;
	private String url;
	Icon icon = null;
	
	protected final List<Link> children = new ArrayList<Link>();
	
	public Link(String name) {
		this(name, null, null);
	}
	
	public Link(String name, String url) {
		this(name, url, null);
	}
	
	public Link(String name, Icon icon) {
		this(name, null, icon);
	}
	
	public Link(String name, String url, Icon icon) {
		this.name = name;
		this.url = url;
		this.icon = icon;
	}
	
	public String getUrl() {
		return url;
	}
	
	public Icon getIcon() {
		return icon;
	}
	
	public final boolean hasChildren() {
		return getChildCount() > 0;
	}
	
	public final int getChildCount() {
		return children.size();
	}
	
	public final List<Link> getChildren() {
		return children;
	}
	
	public Link add(String title) {
		return add(title, null);
	}
	
	public Link add(String name, String url) {
		return add(new Link(name, url));
	}
	
	public Link add(Link link) {
		children.add(link);
		return this;		
	}
	
	public String toString() {
		return toString("");
	}
	
	private String toString(String indent) {
		StringBuilder sb = new StringBuilder();
		
		sb.append(indent);
		sb.append(name);
		sb.append(" -> ");
		sb.append(getUrl());
		sb.append('\n');
		
		for (Link f : children) {
			if (null == f) {
				sb.append("(null)\n");
				continue;
			}
			
			sb.append(f.toString(indent + "  "));
		}
		
		return sb.toString();
	}
}
package jshm.sh.links;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;

/**
 * This represents the tree-like structure of a menu containing
 * links, like rckr's YUI menus.
 * @author Tim Mullin
 *
 */
public class Link {
	public static final Link GH_ROOT = new Link("GH_ROOT")
		.add(ForumLink.GH_ROOT)
		.add(ManageScoreLink.GH_ROOT)
		.add(ScoreDatabaseLink.GH_ROOT);
	
	public static final Link RB_ROOT = new Link("RB_ROOT")
		.add(ForumLink.RB_ROOT)
		.add(ManageScoreLink.RB_ROOT);
	
	
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

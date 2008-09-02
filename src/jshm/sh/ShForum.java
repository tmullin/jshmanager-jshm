package jshm.sh;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This represents the tree-like structure of SH's forum
 * categories and sub-forums.
 * @author Tim Mullin
 *
 */
public class ShForum {
	public static final ShForum GH_ROOT = new ShForum("ROOT")
		.add(new ShForum("ScoreHero")
			.add("Website Discussion", 1)
			.add("Bug Reports", 10)
		)
		.add(new ShForum("GuitarHero")
			.add("Official Guitar Hero News", 47)
			.add("Technique, Style, and Gameplay", 3)
			.add("*****", 4)
			.add("Rivalries", 15)
			.add(new ShForum("Leagues", 32)
				.add("Past Seasons", 35)
			)
			.add(new ShForum("Tournaments and Events", 18)
				.add("Get-togethers", 59)
			)
			.add("Misc GH Game Discussion", 5)
		)
		.add(new ShForum("Homebrew Projects")
			.add("Hardware", 33)
			.add(new ShForum("Software", 16)
				.add("Custom Songs", 34)
			)	
		)
		.add(new ShForum("Star Power FAQs")
			.add(new ShForum("Guitar Hero", 26)
				.add("Easy", 11)
				.add("Medium", 12)
				.add("Hard", 13)
				.add("Expert", 14)
			)
			.add(new ShForum("Guitar Hero II (PS2)", 24)
				.add(new ShForum("Single Player")
					.add("Easy", 20)
					.add("Medium", 21)
					.add("Hard", 22)
					.add("Expert", 23)
				)
				.add(new ShForum("Co-op")
					.add("Easy", 27)
					.add("Medium", 28)
					.add("Hard", 29)
					.add("Expert", 30)
				)
			)
			.add(new ShForum("Guitar Hero II (360)", 25)
				.add(new ShForum("Single Player")
					.add("Easy", 37)
					.add("Medium", 38)
					.add("Hard", 39)
					.add("Expert", 40)
				)
				.add(new ShForum("Co-op")
					.add("Easy", 41)
					.add("Medium", 42)
					.add("Hard", 43)
					.add("Expert", 44)
				)
			)
			.add(new ShForum("Guitar Hero Encore: Rocks The 80s", 48)
				.add(new ShForum("Single Player")
					.add("Easy", 50)
					.add("Medium", 51)
					.add("Hard", 52)
					.add("Expert", 53)
				)
				.add(new ShForum("Co-op")
					.add("Easy", 54)
					.add("Medium", 55)
					.add("Hard", 56)
					.add("Expert", 57)
				)
			)
			.add(new ShForum("Guitar Hero III", 62)
				.add(new ShForum("Single Player")
					.add("Easy", 63)
					.add("Medium", 64)
					.add("Hard", 65)
					.add("Expert", 66)
				)
				.add(new ShForum("Co-op")
					.add("Easy", 67)
					.add("Medium", 68)
					.add("Hard", 69)
					.add("Expert", 70)
				)
			)
		)
		.add(new ShForum("General")
			.add("Gaming Discussion (Non-Music Games)", 19)
			.add(new ShForum("Other Music Games", 9)
				.add("Dance Dance Revolution", 7)
				.add("Popn'Music and Beatmania", 8)
			)
			.add("General Chat", 2)
			.add("Thread Hall of Fame", 31)
		);
	
	public static final ShForum RB_ROOT = new ShForum("ROOT", true)
		.add(new ShForum("ScoreHero")
			.add("Website Discussion", 1009)
			.add("Bug Reports", 1010)
		)
		.add(new ShForum("Rock Band")
			.add("Official Rock Band News", 1012)
			.add(new ShForum("Technique, Style, and Gameplay", 1001)
				.add("Guitar/Bass", 1002)
				.add("Drums", 1003)
				.add("Vocals", 1004)
				.add("Multitasking", 1042)
			)
			.add("Accomplishments", 1005)
			.add("Rivalries", 1006)
			.add(new ShForum("Leagues", 1041)
				.add("Past Seasons", 1043)
			)
			.add("Tournaments and Events", 1007)
			.add("Will Rock For Food", 1040)
			.add("Misc RB Game Discussion", 1008)
		)
		.add(new ShForum("Homebrew Projects")
			.add("Hardware", 1038)
			.add("Software", 1039)
		)
		.add(new ShForum("Overdrive FAQs")
			.add(new ShForum("Guitar", 1013)
				.add("Easy", 1018)
				.add("Medium", 1019)
				.add("Hard", 1020)
				.add("Expert", 1021)
			)
			.add(new ShForum("Bass", 1014)
				.add("Easy", 1022)
				.add("Medium", 1023)
				.add("Hard", 1024)
				.add("Expert", 1025)
			)
			.add(new ShForum("Drums", 1015)
				.add("Easy", 1026)
				.add("Medium", 1027)
				.add("Hard", 1028)
				.add("Expert", 1029)
			)
			.add("Vocals", 1016)
			.add(new ShForum("Full Band", 1017)
				.add("Easy", 1034)
				.add("Medium", 1035)
				.add("Hard", 1036)
				.add("Expert", 1037)
			)
		)
	;
	
//	static {
//		System.out.println(GH_ROOT);
//	}
	
	public static final String
		GH_BASE = "http://www.scorehero.com/forum/",
		RB_BASE = "http://rockband.scorehero.com/forum/",
		FORUM_FORMAT = "%sviewforum.php?f=%s";
	
	public final String name;
	public final int forumId;
	private String baseUrl;
	
	private List<ShForum> children = new ArrayList<ShForum>();
	
	public ShForum(String name) {
		this(name, false);
	}
	
	public ShForum(String name, boolean isRb) {
		this(name, -1, isRb);
	}
	
	public ShForum(String name, int forumId) {
		this(name, forumId, false, new ShForum[0]);
	}
	
	public ShForum(String name, int forumId, ShForum ... children) {
		this(name, forumId, false, children);
	}

	public ShForum(String name, int forumId, boolean isRb) {
		this(name, forumId, isRb, new ShForum[0]);
	}
	
	public ShForum(String name, int forumId, boolean isRb, ShForum ... children) {
		this(name, forumId, isRb ? RB_BASE : GH_BASE, children);
	}
	
	public ShForum(String name, int forumId, String baseUrl, ShForum ... children) {
		this.name = name;
		this.forumId = forumId;
		this.baseUrl = baseUrl;
		
		if (null != children)
			this.children.addAll(Arrays.asList(children));
	}
	
	public String getUrl() {
		String fmt = null;
		int id = -1;
		
		if (-1 == forumId) { 
			return null;
		} else {
			fmt = FORUM_FORMAT;
			id = forumId;
		}
		
		return String.format(fmt, baseUrl, id);
	}
	
	public boolean hasChildren() {
		return getChildCount() > 0;
	}
	
	public int getChildCount() {
		return children.size();
	}
	
	public List<ShForum> getChildren() {
		return children;
	}
	
	/**
	 * Convenience method to create a new ShForum with a forumId
	 * and add it to our children.
	 * @param name
	 * @param forumId
	 * @return
	 */
	public ShForum add(String name, int forumId) {
		return add(new ShForum(name, forumId));
	}
	
	public ShForum add(ShForum f) {
		f.setBaseUrlRecursively(this.baseUrl);
		children.add(f);
		return this;
	}
	
	private void setBaseUrlRecursively(String newUrl) {
		this.baseUrl = newUrl;
		
		for (ShForum f : children)
			f.setBaseUrlRecursively(newUrl);
	}
	
	public String toString() {
		return toString("");
	}
	
	protected String toString(String indent) {
		StringBuilder sb = new StringBuilder();
		
		sb.append(indent);
		sb.append(name);
		sb.append(" f=");
		sb.append(forumId);
		sb.append('\n');
		
		for (ShForum f : children) {
			if (null == f) {
				sb.append("(null)\n");
				continue;
			}
			
			sb.append(f.toString(indent + "  "));
		}
		
		return sb.toString();
	}
}

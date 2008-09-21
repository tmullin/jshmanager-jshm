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

import javax.swing.Icon;

import jshm.Difficulty;
import jshm.Instrument;
import jshm.gh.GhGameTitle;
import jshm.sh.URLs;

/**
 * This represents the tree-like structure of SH's forum
 * categories and sub-forums.
 * @author Tim Mullin
 *
 */
public class ForumLink extends Link {
	public static final String
		GH_BASE = URLs.BASE + "/forum/",
		RB_BASE = URLs.rb.BASE + "/forum/",
		FORUM_FORMAT = "%sviewforum.php?f=%s";
	
	public static final Link GH_ROOT = new ForumLink("Forums")
		.add(new ForumLink("ScoreHero")
			.add("Website Discussion", 1)
			.add("Bug Reports", 10)
		)
		.add(new ForumLink("Guitar Hero")
			.add("Official Guitar Hero News", 47)
			.add("Technique, Style, and Gameplay", 3)
			.add("Accomplishments", 4)
			.add("Rivalries", 15)
			.add(new ForumLink("Leagues", 32)
				.add("Past Seasons", 35)
			)
			.add(new ForumLink("Tournaments and Events", 18)
				.add("Get-togethers", 59)
			)
			.add("Misc GH Game Discussion", 5)
		)
		.add(new ForumLink("Homebrew Projects")
			.add("Hardware", 33)
			.add(new ForumLink("Software", 16)
				.add("Custom Songs", 34)
			)	
		)
		.add(new ForumLink("Star Power FAQs")
			.add(new ForumLink("Guitar Hero", 26, GhGameTitle.GH1.getIcon())
				.add("Easy", 11, Difficulty.EASY.getIcon())
				.add("Medium", 12, Difficulty.MEDIUM.getIcon())
				.add("Hard", 13, Difficulty.HARD.getIcon())
				.add("Expert", 14, Difficulty.EXPERT.getIcon())
			)
			.add(new ForumLink("Guitar Hero II (PS2)", 24, GhGameTitle.GH2.getIcon())
				.add(new ForumLink("Single Player")
					.add("Easy", 20, Difficulty.EASY.getIcon())
					.add("Medium", 21, Difficulty.MEDIUM.getIcon())
					.add("Hard", 22, Difficulty.HARD.getIcon())
					.add("Expert", 23, Difficulty.EXPERT.getIcon())
				)
				.add(new ForumLink("Co-op")
					.add("Easy", 27, Difficulty.EASY.getIcon())
					.add("Medium", 28, Difficulty.MEDIUM.getIcon())
					.add("Hard", 29, Difficulty.HARD.getIcon())
					.add("Expert", 30, Difficulty.EXPERT.getIcon())
				)
			)
			.add(new ForumLink("Guitar Hero II (360)", 25, GhGameTitle.GH2.getIcon())
				.add(new ForumLink("Single Player")
					.add("Easy", 37, Difficulty.EASY.getIcon())
					.add("Medium", 38, Difficulty.MEDIUM.getIcon())
					.add("Hard", 39, Difficulty.HARD.getIcon())
					.add("Expert", 40, Difficulty.EXPERT.getIcon())
				)
				.add(new ForumLink("Co-op")
					.add("Easy", 41, Difficulty.EASY.getIcon())
					.add("Medium", 42, Difficulty.MEDIUM.getIcon())
					.add("Hard", 43, Difficulty.HARD.getIcon())
					.add("Expert", 44, Difficulty.EXPERT.getIcon())
				)
			)
			.add(new ForumLink("Guitar Hero Encore: Rocks The 80s", 48, GhGameTitle.GH80.getIcon())
				.add(new ForumLink("Single Player")
					.add("Easy", 50, Difficulty.EASY.getIcon())
					.add("Medium", 51, Difficulty.MEDIUM.getIcon())
					.add("Hard", 52, Difficulty.HARD.getIcon())
					.add("Expert", 53, Difficulty.EXPERT.getIcon())
				)
				.add(new ForumLink("Co-op")
					.add("Easy", 54, Difficulty.EASY.getIcon())
					.add("Medium", 55, Difficulty.MEDIUM.getIcon())
					.add("Hard", 56, Difficulty.HARD.getIcon())
					.add("Expert", 57, Difficulty.EXPERT.getIcon())
				)
			)
			.add(new ForumLink("Guitar Hero III", 62, GhGameTitle.GH3.getIcon())
				.add(new ForumLink("Single Player")
					.add("Easy", 63, Difficulty.EASY.getIcon())
					.add("Medium", 64, Difficulty.MEDIUM.getIcon())
					.add("Hard", 65, Difficulty.HARD.getIcon())
					.add("Expert", 66, Difficulty.EXPERT.getIcon())
				)
				.add(new ForumLink("Co-op")
					.add("Easy", 67, Difficulty.EASY.getIcon())
					.add("Medium", 68, Difficulty.MEDIUM.getIcon())
					.add("Hard", 69, Difficulty.HARD.getIcon())
					.add("Expert", 70, Difficulty.EXPERT.getIcon())
				)
			)
		)
		.add(new ForumLink("General")
			.add("Gaming Discussion (Non-Music Games)", 19)
			.add(new ForumLink("Other Music Games", 9)
				.add("Dance Dance Revolution", 7)
				.add("Popn'Music and Beatmania", 8)
			)
			.add("General Chat", 2)
			.add("Thread Hall of Fame", 31)
		)
		.add("JSHManager Thread", GH_BASE + "viewtopic.php?t=74670");
	
	
	public static final Link RB_ROOT = new ForumLink("Forums", true)
		.add(new ForumLink("ScoreHero")
			.add("Website Discussion", 1009)
			.add("Bug Reports", 1010)
		)
		.add(new ForumLink("Rock Band")
			.add("Official Rock Band News", 1012)
			.add(new ForumLink("Technique, Style, and Gameplay", 1001)
				.add("Guitar/Bass", 1002, Instrument.GUITAR.getIcon())
				.add("Drums", 1003, Instrument.DRUMS.getIcon())
				.add("Vocals", 1004, Instrument.VOCALS.getIcon())
				.add("Multitasking", 1042)
			)
			.add("Accomplishments", 1005)
			.add("Rivalries", 1006)
			.add(new ForumLink("Leagues", 1041)
				.add("Past Seasons", 1043)
			)
			.add("Tournaments and Events", 1007)
			.add("Will Rock For Food", 1040)
			.add("Misc RB Game Discussion", 1008)
		)
		.add(new ForumLink("Homebrew Projects")
			.add("Hardware", 1038)
			.add("Software", 1039)
		)
		.add(new ForumLink("Overdrive FAQs")
			.add(new ForumLink("Guitar", 1013, Instrument.GUITAR.getIcon())
				.add("Easy", 1018, Difficulty.EASY.getIcon())
				.add("Medium", 1019, Difficulty.MEDIUM.getIcon())
				.add("Hard", 1020, Difficulty.HARD.getIcon())
				.add("Expert", 1021, Difficulty.EXPERT.getIcon())
			)
			.add(new ForumLink("Bass", 1014, Instrument.BASS.getIcon())
				.add("Easy", 1022, Difficulty.EASY.getIcon())
				.add("Medium", 1023, Difficulty.MEDIUM.getIcon())
				.add("Hard", 1024, Difficulty.HARD.getIcon())
				.add("Expert", 1025, Difficulty.EXPERT.getIcon())
			)
			.add(new ForumLink("Drums", 1015, Instrument.DRUMS.getIcon())
				.add("Easy", 1026, Difficulty.EASY.getIcon())
				.add("Medium", 1027, Difficulty.MEDIUM.getIcon())
				.add("Hard", 1028, Difficulty.HARD.getIcon())
				.add("Expert", 1029, Difficulty.EXPERT.getIcon())
			)
			.add("Vocals", 1016, Instrument.VOCALS.getIcon())
			.add(new ForumLink("Full Band", 1017)
				.add("Easy", 1034, Difficulty.EASY.getIcon())
				.add("Medium", 1035, Difficulty.MEDIUM.getIcon())
				.add("Hard", 1036, Difficulty.HARD.getIcon())
				.add("Expert", 1037, Difficulty.EXPERT.getIcon())
			)
		)
		.add("JSHManager Thread", RB_BASE + "viewtopic.php?t=15464")
	;
	
//	static {
//		System.out.println(GH_ROOT);
//	}
	
	public final int forumId;
	private String baseUrl;

	public ForumLink(String name) {
		this(name, null);
	}

	public ForumLink(String name, Icon icon) {
		this(name, false, icon);
	}
	
	public ForumLink(String name, boolean isRb) {
		this(name, isRb, null);
	}
	
	public ForumLink(String name, boolean isRb, Icon icon) {
		this(name, -1, isRb, icon);
	}
	
	public ForumLink(String name, int forumId) {
		this(name, forumId, null);
	}
	
	public ForumLink(String name, int forumId, Icon icon) {
		this(name, forumId, false, icon);
	}
	
	public ForumLink(String name, int forumId, boolean isRb) {
		this(name, forumId, isRb, null);
	}
	
	public ForumLink(String name, int forumId, boolean isRb, Icon icon) {
		this(name, forumId, isRb ? RB_BASE : GH_BASE, icon);
	}
	
	public ForumLink(String name, int forumId, String baseUrl, Icon icon) {
		super(name, icon);
		this.forumId = forumId;
		this.baseUrl = baseUrl;
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
	
	/**
	 * Convenience method to create a new ShForum with a forumId
	 * and add it to our children.
	 * @param name
	 * @param forumId
	 * @return
	 */
	public ForumLink add(String name, int forumId) {
		return add(name, forumId, null);
	}
	
	public ForumLink add(String name, int forumId, Icon icon) {
		return add(new ForumLink(name, forumId, icon));
	}
	
	public ForumLink add(ForumLink f) {
		f.setBaseUrlRecursively(this.baseUrl);
		super.add(f);
		return this;
	}
	
	private void setBaseUrlRecursively(String newUrl) {
		this.baseUrl = newUrl;
		
		for (Link f : children)
			((ForumLink) f).setBaseUrlRecursively(newUrl);
	}
}

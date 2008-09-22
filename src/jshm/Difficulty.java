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
package jshm;

/**
 * This enum represents a song difficulty from
 * ScoreHero's point of view.
 * @author Tim Mullin
 *
 */
public enum Difficulty {
	EASY(1, "E"),
	MEDIUM(2, "M"),
	HARD(3, "H"),
	EXPERT(4, "X"),
	CO_OP(5, "C");
	
	public  final int	 scoreHeroId;
	private final String shortName;
	
	private transient javax.swing.ImageIcon icon = null;
	
	private Difficulty(final int scoreHeroId, final String shortName) {
		this.scoreHeroId = scoreHeroId;
		this.shortName = shortName;
	}
	
	public String toShortString() {
		return shortName;
	}
	
	public javax.swing.ImageIcon getIcon() {
		if (null == icon) {
			try {
				icon = new javax.swing.ImageIcon(
					Difficulty.class.getResource("/jshm/resources/images/difficulties/" + this + "_32.png"));
			} catch (Exception e) {}
		}
		
		return icon;
	}
	
	public static Difficulty smartValueOf(final String value) {
		String value2 = value.toUpperCase();
		boolean atLeastTwoChars = value.length() >= 2;
		
		for (Difficulty d : values()) {
			if (d.toShortString().equals(value2) ||
				(atLeastTwoChars && d.name().startsWith(value2)))
				return d;
		}
		
		return null;
	}
	
	public static Difficulty getByScoreHeroId(final int scoreHeroId) {
		for (Difficulty d : Difficulty.values())
			if (d.scoreHeroId == scoreHeroId)
				return d;
		
		throw new IllegalArgumentException("invalid difficulty scoreHeroId: " + scoreHeroId);
	}
	
	/**
	 * This represents where in the data model difficulty
	 * information is stored. GH is BY_SONG, RB is BY_SCORE.
	 * 
	 * ScoreHero gives each GH song a separate id for each
	 * difficulty level whereas each RB song always has the
	 * same id.
	 * 
	 * @author Tim Mullin
	 *
	 */
	public static enum Strategy {
		BY_SONG, BY_SCORE
	}
}

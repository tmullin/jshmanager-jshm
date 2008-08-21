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

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.*;

/**
 * Represents a part played for a particular Score.
 * @author Tim Mullin
 *
 */
@Entity
public class Part implements Comparable<Part> {
	private int			id;
	
	private float 		hitPercent;
	private int 		streak;
	
	private Difficulty	difficulty;
	private Instrument 	instrument;
	
	@Id
	@GeneratedValue(generator="part-id")
	@GenericGenerator(name="part-id", strategy = "native")
	public int getId() {
		return id;
	}

	@SuppressWarnings("unused")
	private void setId(int id) {
		this.id = id;
	}
	
	@Range(min=0, max=1)
	public float getHitPercent() {
		return hitPercent;
	}
	
	public void setHitPercent(float hitPercent) {
		if (hitPercent < 0.0f || 1.0f < hitPercent)
			throw new IllegalArgumentException("hitPercent must be between 0.0f and 1.0f inclusive");
		this.hitPercent = hitPercent;
	}
	
	@Min(0)
	public int getStreak() {
		return streak;
	}
	
	public void setStreak(int streak) {
		if (streak < 0)
			throw new IllegalArgumentException("streak must be >= 0");
		this.streak = streak;
	}
	
	@Enumerated(EnumType.STRING)
	public Difficulty getDifficulty() {
		return difficulty;
	}

	public void setDifficulty(Difficulty difficulty) {
		this.difficulty = difficulty;
	}
	
	@Enumerated(EnumType.STRING)
	public Instrument getInstrument() {
		return instrument;
	}
	
	public void setInstrument(Instrument instrument) {
		if (null == instrument)
			throw new IllegalArgumentException("instrument cannot be null");
		this.instrument = instrument;
	}

	
	@Transient
	public boolean isSubmittable() {
		return instrument != null && difficulty != null;
	}
	
	// override object methods
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(instrument);
		sb.append(',');
		sb.append(difficulty.toShortString());
		sb.append(',');
		sb.append(hitPercent);
		sb.append(',');
		sb.append(streak);
		
		return sb.toString();
	}
	
	// comparable
	@Override
	public int compareTo(Part o) {
		return this.instrument.compareTo(o.instrument);
	}
}

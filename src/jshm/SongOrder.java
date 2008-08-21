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

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

/*

GH3_XBOX360	GUITAR	"Slow Ride"	1	1
GH3_XBOX360	GUITAR	"Talk Dirty..."	1	2

GH3_XBOX360	GUITAR_BASS	"Barracuda"	1	1
GH3_XBOX360	GUITAR_BASS	"When You Were Young"	1	2

RB1_XBOX360	GUITAR	"Should I Stay"	1	1
RB1_XBOX360	GUITAR	"In Bloom"	1	2

RB1_XBOX360	VOCALS	"Blitzkrieg Bop"	1	1
RB1_XBOX360	VOCALS	"In Bloom"	1	2

RB1_XBOX360	DRUMS	"29 Fingers"	1	1
RB1_XBOX360	DRUMS	"Say It Ain't So"	1	2

RB1_XBOX360	GUITAR_BASS	"Say It Ain't So"	1	1
RB1_XBOX360	GUITAR_BASS	"In Bloom"	1	2

RB1_XBOX360	GUITAR_BASS_DRUMS_VOCALS	"29 Fingers"	2	1
RB1_XBOX360	GUITAR_BASS_DRUMS_VOCALS	"Creep"	2	2

 */

/**
 * For the different tier orderings, we need to track the combination
 * of song, tier #, Instrument.Group, and ordering.
 * @author Tim Mullin
 *
 */
@Entity
public class SongOrder implements Comparable<SongOrder> {
	private int id = 0;
	
	private Instrument.Group group;
	private Song song;
	private int tier;
	private int order;
	
	@Id
	@GeneratedValue(generator="songorder-id")
	@GenericGenerator(name="songorder-id", strategy = "native")
	public int getId() {
		return this.id;
	}

	@SuppressWarnings("unused")
	private void setId(int id) {
		this.id = id;
	}

	@Transient
	public Game getGame() {
		return getSong().getGame();
	}

	public void setGroup(Instrument.Group group) {
		this.group = group;
	}

	@Enumerated(EnumType.STRING)
	public Instrument.Group getGroup() {
		return group;
	}

	public void setSong(Song song) {
		this.song = song;
	}

	@ManyToOne
	public Song getSong() {
		return song;
	}

	public void setTier(int tier) {
		this.tier = tier;
	}

	public int getTier() {
		return tier;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public int getOrder() {
		return order;
	}

	@Override
	public int compareTo(SongOrder o) {
		if (this.order < o.order) return -1;
		if (this.order > o.order) return 1;
		return 0;
	}
}

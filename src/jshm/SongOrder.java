package jshm;

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
public class SongOrder {
	public Game game;
	public Instrument.Group group;
	public Song song;
	public int tier;
	public int order;
}

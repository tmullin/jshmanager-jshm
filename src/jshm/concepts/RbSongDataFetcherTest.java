/*
 * -----LICENSE START-----
 * JSHManager - A Java-based tool for managing one's ScoreHero account.
 * Copyright (C) 2008, 2009 Tim Mullin
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
package jshm.concepts;

import jshm.rb.RbGameTitle;
import jshm.xml.*;

@SuppressWarnings("unused")
public class RbSongDataFetcherTest {
	public static void main(String[] args) throws Exception {
//		RbSongDataFetcher fetcher = new RbSongDataFetcher();
//		fetcher.fetch(RbGameTitle.RB2);
		
		RbSongInfoFetcher fetcher = new RbSongInfoFetcher();
		fetcher.fetch();
		
		for (Object key : fetcher.songMap.keySet())
			System.out.println(fetcher.songMap.get(key));
		
//		for (jshm.SongOrder o : fetcher.orders)
//			System.out.println(o);
	}
}

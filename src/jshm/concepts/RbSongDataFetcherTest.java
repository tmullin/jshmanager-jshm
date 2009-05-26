package jshm.concepts;

import jshm.rb.RbGameTitle;
import jshm.xml.RbSongDataFetcher;

public class RbSongDataFetcherTest {
	public static void main(String[] args) throws Exception {
		RbSongDataFetcher fetcher = new RbSongDataFetcher();
		fetcher.fetch(RbGameTitle.RB2);
		
		for (Integer key : fetcher.songMap.keySet())
			System.out.println(fetcher.songMap.get(key));
		
//		for (jshm.SongOrder o : fetcher.orders)
//			System.out.println(o);
	}
}

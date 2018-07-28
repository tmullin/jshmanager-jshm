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
package jshm.xml;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import jshm.GameTitle;
import jshm.Song;
import jshm.Song.RecordingType;
import jshm.gh.GhGameTitle;
import jshm.wt.WtGameTitle;

public class GhSongInfoFetcher {
	public static final String XML_URL =
		"http://jshm-s3.tmullin.net/songdata/%s_meta.xml";

	public Map<String, SongInfo> songMap = null;
	
	public void fetch(GameTitle ttl) throws ParserConfigurationException, SAXException, IOException {
		if (!(ttl instanceof GhGameTitle || ttl instanceof WtGameTitle))
			throw new IllegalArgumentException("ttl must be Gh or WtGameTitle");
		
		DocumentBuilderFactory f 
			= DocumentBuilderFactory.newInstance();
		// TODO need to add new platform/game enum values to DTD.
		f.setValidating(false);
		f.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		DocumentBuilder b = f.newDocumentBuilder();
		b.setErrorHandler(jshm.xml.ErrorHandler.getInstance());
		Document d = b.parse(String.format(XML_URL, ttl.title));
		d.getDocumentElement().normalize();
		
		songMap = new HashMap<String, SongInfo>();
		
		NodeList songNodes = d.getElementsByTagName("song");
		
		for (int i = songNodes.getLength() - 1; i >= 0; i--) {
			Element songEl = (Element) songNodes.item(i);
			
			SongInfo si = new SongInfo();
			
			si.genre = songEl.getAttribute("genre");
			if (si.genre.isEmpty()) si.genre = null;
			
			si.title = songEl.getAttribute("title");
			if (si.title.isEmpty()) si.title = null;
			si.artist = songEl.getAttribute("artist");
			if (si.artist.isEmpty()) si.artist = null;
			si.album = songEl.getAttribute("album");
			if (si.album.isEmpty()) si.album = null;
			
			si.songPack = songEl.getAttribute("songPack");
			if (si.songPack.isEmpty()) si.songPack = null;
			
			try { si.trackNum = Integer.parseInt(songEl.getAttribute("trackNum"));
			} catch (NumberFormatException e) {}
			
			try { si.year = Integer.parseInt(songEl.getAttribute("year"));
			} catch (NumberFormatException e) {}
			
			si.recording = RecordingType.smartValueOf(songEl.getAttribute("recording"));
			
//			songMap.put(si.title.toLowerCase(), si);
			songMap.put(si.title, si);
		}
	}
	
	public static class SongInfo {
		public String
			title,
			artist,
			album,
			genre,
			songPack,
			songSource;
			
		public int
			trackNum = 0,
			year = 0;
		
		public Song.RecordingType recording;
		
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("t=");
			sb.append(title);
			sb.append(",ar=");
			sb.append(artist);
			sb.append(",al=");
			sb.append(album);
			sb.append(",gn=");
			sb.append(genre);
			sb.append(",p=");
			sb.append(songPack);
			sb.append(",#=");
			sb.append(trackNum);
			sb.append(",y=");
			sb.append(year);
			sb.append(",rt=");
			sb.append(recording.getAbbrChar());
			
			return sb.toString();
		}
	}
}

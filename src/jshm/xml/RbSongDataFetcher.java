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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import jshm.GameTitle;
import jshm.Instrument;
import jshm.Platform;
import jshm.SongOrder;
import jshm.rb.RbGameTitle;
import jshm.rb.RbSong;
import jshm.util.IsoDateParser;

public class RbSongDataFetcher {
	public static final String
	XML_URL = "http://jshm.sourceforge.net/songdata/%s.xml"
	;
	
	public Date updated = null;
	public List<RbSong> songs = null;
	public Map<Integer, RbSong> songMap = null;
	public List<SongOrder> orders = null;
	
	public void fetch(final RbGameTitle ttl) throws ParserConfigurationException, SAXException, IOException {
//		File in = new File(ttl.toString() + ".xml");
		String in = String.format(XML_URL, ttl);
		
		DocumentBuilderFactory f 
			= DocumentBuilderFactory.newInstance();
		f.setValidating(true); // Default is false
		DocumentBuilder b = f.newDocumentBuilder();
		b.setErrorHandler(jshm.xml.ErrorHandler.getInstance());
		Document d = b.parse(in);
		d.getDocumentElement().normalize();
		
		GameTitle gameTitle = GameTitle.valueOf(
			d.getElementsByTagName("gameTitle")
			.item(0).getAttributes()
			.getNamedItem("id").getTextContent());
		
		updated = IsoDateParser.parse(
			d.getElementsByTagName("date")
			.item(0).getFirstChild().getTextContent()
		);
		
		NodeList songNodes = d.getElementsByTagName("song");
		songs = new ArrayList<RbSong>(songNodes.getLength());
		songMap = new HashMap<Integer, RbSong>(songNodes.getLength());
		
		for (int i = songNodes.getLength() - 1; i >= 0; i--) {
			Element songEl = (Element) songNodes.item(i);
			
			RbSong song = new RbSong();
			song.setGameTitle(gameTitle);
			song.setTitle(songEl.getAttribute("title"));
			song.setScoreHeroId(Integer.parseInt(songEl.getAttribute("id")));
			
			
			NodeList platformNodes = songEl.getChildNodes().item(1).getChildNodes();
			
			for (int j = platformNodes.getLength() - 1; j >= 0; j--) {
				Node platformNode = platformNodes.item(j);
				
				if (!(platformNode instanceof Element)) continue;
				
				Element platformEl = (Element) platformNode;
				
				song.addPlatform(Platform.valueOf(
					platformEl.getAttribute("id")));
			}
			
			songs.add(song);
			songMap.put(song.getScoreHeroId(), song);
		}
		
		
		NodeList orderNodes = d.getElementsByTagName("songOrder");
		orders = new ArrayList<SongOrder>(orderNodes.getLength());
		
		for (int i = orderNodes.getLength() - 1; i >= 0; i--) {
			Element orderEl = (Element) orderNodes.item(i);
			
			SongOrder order = new SongOrder();
			order.setGroup(Instrument.Group.valueOf(
				orderEl.getAttribute("group")));
			order.setPlatform(Platform.valueOf(
				orderEl.getAttribute("platform")));
			order.setTier(Integer.parseInt(
				orderEl.getAttribute("tier")));
			order.setOrder(Integer.parseInt(
				orderEl.getAttribute("order")));
			order.setSong(songMap.get(Integer.parseInt(
				orderEl.getAttribute("song"))));
			
			orders.add(order);
		}
	}
}

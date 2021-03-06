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
package jshm.internal.xml;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.netbeans.spi.wizard.ResultProgressHandle;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import jshm.Game;
import jshm.GameSeries;
import jshm.GameTitle;
import jshm.Instrument;
import jshm.Platform;
import jshm.SongOrder;
import jshm.Tiers;
import jshm.internal.ConsoleProgressHandle;
import jshm.rb.RbGame;
import jshm.rb.RbGameTitle;
import jshm.rb.RbSong;
import jshm.sh.scraper.RbSongScraper;
import jshm.util.IsoDateParser;
import jshm.util.PhpUtil;

public class RbSongDataGenerator {
	public static final String DTD_URL = "http://jshm-s3.tmullin.net/songdata/rb_songdata.dtd";
	
	private static void usage() {
		List<GameTitle> titles = GameTitle.getBySeries(GameSeries.ROCKBAND);
		System.out.printf(
			"Usage: java %s <ALL|%s> [GROUP1[,GROUP2[,..]]]\n",
			RbSongDataGenerator.class.getName(),
			PhpUtil.implode("|", titles));
		System.exit(-1);
	}
	
	static ResultProgressHandle progress = ConsoleProgressHandle.getInstance();
	
	public static void main(String[] args) throws Exception {
		if (args.length < 1) usage();
		
		final String ttlString = args[0];
		
		List<GameTitle> titles = new ArrayList<GameTitle>();
		
		if ("ALL".equalsIgnoreCase(ttlString)) {
			titles.addAll(GameTitle.getBySeries(GameSeries.ROCKBAND));
		} else {
			GameTitle ttl = GameTitle.valueOf(ttlString);
			
			if (!(ttl instanceof RbGameTitle)) usage();
			
			titles.add(ttl);
		}
		
		for (GameTitle title : titles) {
			doTitle(args, (RbGameTitle) title);
		}
		
		System.out.println("Current working directory: " + new File(".").getAbsolutePath());
	}
	
	private static Instrument.Group[] defaultGroupsForTitle(RbGameTitle ttl) {
		if (RbGameTitle.RBN.equals(ttl)
				|| RbGameTitle.RBNRB3.equals(ttl)) {
			return new Instrument.Group[] {
					Instrument.Group.GUITAR,
					Instrument.Group.BASS,
					Instrument.Group.DRUMS,
					Instrument.Group.VOCALS,
					Instrument.Group.GUITAR_BASS
			};
		}
		
		return RbSongScraper.DEFAULT_GROUPS;
	}
	
	private static void doTitle(String[] args, RbGameTitle ttl) throws Exception {
		Instrument.Group[] groups = defaultGroupsForTitle(ttl);
		
		if (args.length >= 2) {
			String[] parts = args[1].split(",");
			groups = new Instrument.Group[parts.length];
			
			for (int i = 0; i < parts.length; i++) {
				groups[i] = Instrument.Group.valueOf(parts[i]);
			}
		}
		
		jshm.util.TestTimer.start();
		
		Map<Integer, RbSong> songMap = new HashMap<Integer, RbSong>();
		List<SongOrder> allOrders = new ArrayList<SongOrder>();
		Map<String, String> tierMap = new HashMap<String, String>();
		
		for (Game g : Game.getByTitle(ttl)) {
//			if (g.platform != Platform.XBOX360) continue;
			
			progress.setBusy("Downloading song list for " + g);
			List<RbSong> songs = RbSongScraper.scrape((RbGame) g, groups);
			
			assert null != RbSongScraper.lastScrapedTiers;
			tierMap.put(g.toString(), new Tiers(RbSongScraper.lastScrapedTiers).getPacked());
			
			progress.setBusy(String.format("Processing %s songs", songs.size()));
			for (RbSong s : songs) {
				if (songMap.containsKey(s.getScoreHeroId())) {
					songMap.get(s.getScoreHeroId()).addPlatform(g.platform);
				} else {
					songMap.put(s.getScoreHeroId(), s);
				}
			}
			
			progress.setBusy("Downloading song order lists for " + g);
			List<SongOrder> orders = RbSongScraper.scrapeOrders(progress, (RbGame) g, songMap);
			
			progress.setBusy(String.format("Processing %s song orders", orders.size()));
			allOrders.addAll(orders);
		}
		
		
		progress.setBusy("Creating XML file");
		createXml(ttl, songMap, allOrders, tierMap);
		
		jshm.util.TestTimer.stop();
		progress.setBusy("All done");
	}
	
	private static void createXml(GameTitle ttl, Map<Integer, RbSong> songMap, List<SongOrder> orders, Map<String, String> tierMap) {
		Document xml = null;
		DocumentBuilderFactory factory = 
			DocumentBuilderFactory.newInstance();
		
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			xml = builder.newDocument();  // Create from whole cloth

			Element root = CE(xml, "songData");
			AC(xml, root);
			
			Element tmp = CE(xml, "gameTitle");
			tmp.setAttribute("id", ttl.toString());
			AC(root, tmp);
			
			_(xml, root, "date", IsoDateParser.getIsoDate(new Date()));
			
			// create tiers section
			Element tiers = CE(xml, "tiers");
			AC(root, tiers);
			
			for (String key : tierMap.keySet()) {
				Element el = CE(xml, "tier");
				AC(tiers, el);
				
				el.setAttribute("game", key);
				el.setAttribute("packed", tierMap.get(key));
			}
			
			
			// create songs section
			Element songs = CE(xml, "songs");
			AC(root, songs);
			
			for (Integer key : songMap.keySet()) {
				RbSong s = songMap.get(key);
				Element el = CE(xml, "song");
				AC(songs, el);
				
				el.setAttribute("id", String.valueOf(s.getScoreHeroId()));
				el.setAttribute("title", s.getTitle());
				
				Element platforms = CE(xml, "platforms");
				AC(el, platforms);
				
				for (Platform p : s.getPlatforms()) {
					tmp = CE(xml, "platform");
					tmp.setAttribute("id", p.name());
					AC(platforms, tmp);
				}
			}
			
			
			// create song orders section
			Element ordersEl = CE(xml, "songOrders");
			AC(root, ordersEl);
			
			for (SongOrder o : orders) {
				Element el = CE(xml, "songOrder");
				AC(ordersEl, el);
				el.setAttribute("group", o.getGroup().name());
				el.setAttribute("platform", o.getPlatform().name());
				el.setAttribute("song", String.valueOf(o.getSong().getScoreHeroId()));
				el.setAttribute("tier", String.valueOf(o.getTier()));
				el.setAttribute("order", String.valueOf(o.getOrder()));
			}
			
			
			// finally write out
			
			// prepare for serialization
			xml.setXmlStandalone(true);
			xml.getDocumentElement().normalize();
			
			FileOutputStream out = new FileOutputStream(new File(ttl.title + ".xml"));
			DOMSource domSource = new DOMSource(xml);
			StreamResult streamResult = new StreamResult(out);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer serializer = tf.newTransformer();
			serializer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, DTD_URL);
			serializer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			serializer.setOutputProperty(OutputKeys.INDENT, "yes");
			serializer.transform(domSource, streamResult);
			
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-2);
		}
	}
	
	
	/**
	 * One liner to make a new tag with a text node and append
	 * it to parent when you won't need to modify it later.
	 */
	private static void _(Document xml, Element parent, String tag, Object contents) {
		Element tmp = CE(xml, tag);
		AC(tmp, CTN(xml, contents.toString()));
		AC(parent, tmp);
	}
	
	/**
	 * createElement shortcut
	 */
	private static Element CE(Document xml, String name) {
		return xml.createElement(name);
	}
	
	/**
	 * createTextNode shortcut
	 * @param xml
	 * @param contents
	 * @return
	 */
	private static Text CTN(Document xml, String contents) {
		return xml.createTextNode(contents);
	}
	
	/**
	 * appendChild shortcut
	 * @param parent
	 * @param child
	 */
	private static void AC(Node parent, Node child) {
		parent.appendChild(child);
	}
}

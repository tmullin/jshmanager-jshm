package jshm.internal.xml;

import java.awt.Container;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
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
import jshm.GameTitle;
import jshm.Platform;
import jshm.SongOrder;
import jshm.rb.RbGame;
import jshm.rb.RbGameTitle;
import jshm.rb.RbSong;
import jshm.sh.scraper.RbSongScraper;
import jshm.xml.RbSongDataFetcher;

public class RbSongDataGenerator {
	private static void usage() {
		System.out.println("Usage: java " + RbSongDataGenerator.class.getName() + " <RB1|RB2>");
		System.exit(-1);
	}
	
	public static void main(String[] args) throws Exception {
		// if (args.length != 1) usage();
		final String ttlString = "RB2"; // = args[0];
		GameTitle ttl = GameTitle.valueOf(ttlString);
		
		if (!(ttl instanceof RbGameTitle)) usage();
		
		jshm.util.TestTimer.start();
		
		Map<Integer, RbSong> songMap = new HashMap<Integer, RbSong>();
		List<SongOrder> allOrders = new ArrayList<SongOrder>();
		
		for (Game g : Game.getByTitle(ttl)) {
//			if (g.platform != Platform.XBOX360) continue;
			
			consoleProgHandle.setBusy("Downloading song list for " + g);
			List<RbSong> songs = RbSongScraper.scrape((RbGame) g);
			
			consoleProgHandle.setBusy(String.format("Processing %s songs", songs.size()));
			for (RbSong s : songs) {
				if (songMap.containsKey(s.getScoreHeroId())) {
					songMap.get(s.getScoreHeroId()).addPlatform(g.platform);
				} else {
					songMap.put(s.getScoreHeroId(), s);
				}
			}
			
			consoleProgHandle.setBusy("Downloading song order lists for " + g);
			List<SongOrder> orders = RbSongScraper.scrapeOrders(consoleProgHandle, (RbGame) g, songMap);
			
			consoleProgHandle.setBusy(String.format("Processing %s song orders", orders.size()));
			allOrders.addAll(orders);
		}
		
		
		consoleProgHandle.setBusy("Creating XML file");
		createXml(ttl, songMap, allOrders);
		
		jshm.util.TestTimer.stop();
		consoleProgHandle.setBusy("All done");
	}
	
	private static void createXml(GameTitle ttl, Map<Integer, RbSong> songMap, List<SongOrder> orders) {
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
			
			_(xml, root, "date", new java.util.Date());
			
			
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
			
			FileOutputStream out = new FileOutputStream(new File(ttl.toString() + ".xml"));
			DOMSource domSource = new DOMSource(xml);
			StreamResult streamResult = new StreamResult(out);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer serializer = tf.newTransformer();
			serializer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM,
				RbSongDataFetcher.DTD_URL);
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
	
	static ResultProgressHandle consoleProgHandle = new ResultProgressHandle() {
		private boolean isRunning = true;
		
		@Override public void addProgressComponents(Container panel) {}
		
		@Override
		public void failed(String message, boolean canNavigateBack) {
			isRunning = false;
			System.out.println(message);
		}
		
		@Override
		public void finished(Object result) {
			isRunning = false;
		}
		
		@Override
		public boolean isRunning() {
			return isRunning;
		}

		@Override
		public void setBusy(String description) {
			System.out.println(description);
		}

		@Override public void setProgress(int currentStep, int totalSteps) {}

		@Override
		public void setProgress(String description, int currentStep,
				int totalSteps) {
			System.out.println(description);
		}
	};
}

package jshm.internal.xml;

import java.io.File;
import java.io.FileOutputStream;
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

import jshm.Difficulty;
import jshm.Game;
import jshm.GameSeries;
import jshm.GameTitle;
import jshm.Song;
import jshm.gh.GhGame;
import jshm.gh.GhGameTitle;
import jshm.gh.GhSong;
import jshm.internal.ConsoleProgressHandle;
import jshm.sh.URLs;
import jshm.sh.scraper.GhSongScraper;
import jshm.sh.scraper.WtSongScraper;
import jshm.sh.scraper.wiki.Action;
import jshm.sh.scraper.wiki.ActionsScraper;
import jshm.util.IsoDateParser;
import jshm.util.PhpUtil;
import jshm.wt.WtGame;
import jshm.wt.WtGameTitle;
import jshm.wt.WtSong;

public class GhSongMetaDataGenerator {
	public static final String DTD_URL =
		"http://jshm.sourceforge.net/songdata/gh_songmetadata.dtd";
	
	static final ResultProgressHandle progress = ConsoleProgressHandle.getInstance();
	
	static final List<GameTitle> validTitles;
	
	static {
		validTitles = GameTitle.getBySeries(GameSeries.GUITAR_HERO);
		validTitles.addAll(GameTitle.getBySeries(GameSeries.WORLD_TOUR));
	}
	
	private static void usage() {
		System.out.printf("Usage: java %s <%s>%n",
			GhSongMetaDataGenerator.class.getName(),
			PhpUtil.implode("|", validTitles));
		System.exit(-1);
	}
	
	public static void main(String[] args) throws Exception {
//		if (args.length < 1)
//			usage();
		
		GameTitle ttl = GameTitle.valueOf(
//			args[0]
			"GH3"
		);
		
		if (!validTitles.contains(ttl))
			usage();
		
		// first get the list of songs
		
		Map<String, Song> songMap = new HashMap<String, Song>();
		
		if (ttl instanceof GhGameTitle) {
			for (Game g : Game.getByTitle(ttl)) {
				progress.setBusy("Getting song list for " + g);
				List<GhSong> songs =
					GhSongScraper.scrape((GhGame) g, Difficulty.EXPERT);
				
				for (GhSong s : songs) {
					if (!songMap.containsKey(s.getTitle()))
						songMap.put(s.getTitle(), s);
				}
			}
		} else if (ttl instanceof WtGameTitle) {
			for (Game g : Game.getByTitle(ttl)) {
				progress.setBusy("Getting song list for " + g);
				List<WtSong> songs =
					WtSongScraper.scrape((WtGame) g);
				
				for (WtSong s : songs) {
					if (!songMap.containsKey(s.getTitle()))
						songMap.put(s.getTitle(), s);
				}
			}
		} else {
			assert false: "invalid GameTitle subclass";
		}
		
		
		// then scrape the genre/album/year/artist from the wiki and set the values
		
		Map<String, List<Action>> actionMap = new HashMap<String, List<Action>>();
		
		for (String key : songMap.keySet()) {
			Song song = songMap.get(key);
			
			progress.setBusy("Getting meta data for " + song);
			
			actionMap.clear();
			ActionsScraper.scrape(
				URLs.wiki.getSongUrl(song), actionMap);
			
			Action songInfo = null != actionMap.get("songinfo")
			? actionMap.get("songinfo").get(0)
			: null;
			
			if (null != songInfo) {
				String s = songInfo.get("genre");
				
				if (null != s) {
					song.setGenre(s);
				}
				
				s = songInfo.get("album");
				
				if (null != s) {
					song.setAlbum(s);
				}
				
				s = songInfo.get("year");
				
				if (null != s) {
					try {
						song.setYear(Integer.parseInt(s));
					} catch (NumberFormatException e) {}
				}
				
				s = songInfo.get("artist");
				
				if (null != s) {
					song.setArtist(s);
				}
			}
		}
		
		
		// finally output the XML
		progress.setBusy("Creating XML File");
		createXml(ttl, songMap);
		progress.setBusy("All done");
	}
	
	private static void createXml(GameTitle ttl, Map<String, Song> songMap) {
		Document xml = null;
		DocumentBuilderFactory factory = 
			DocumentBuilderFactory.newInstance();
		
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			xml = builder.newDocument();  // Create from whole cloth

			Element root = CE(xml, "songMetaData");
			AC(xml, root);
			
			Element tmp = CE(xml, "gameTitle");
			tmp.setAttribute("id", ttl.toString());
			AC(root, tmp);
			
			_(xml, root, "date", IsoDateParser.getIsoDate(new Date()));
			
			
			// create songs section
			Element songs = CE(xml, "songs");
			AC(root, songs);
			
			for (String key : songMap.keySet()) {
				Song s = songMap.get(key);
				Element el = CE(xml, "song");
				boolean hadAnyInfo = false;
				
				el.setAttribute("title", s.getTitle());

				if (null != s.getGenre()) {
					el.setAttribute("genre", s.getGenre());
					hadAnyInfo = true;
				}
				
				if (null != s.getAlbum()) {
					el.setAttribute("album", s.getAlbum());
					hadAnyInfo = true;
				}
				
				if (0 != s.getYear()) {
					el.setAttribute("year", String.valueOf(s.getYear()));
					hadAnyInfo = true;
				}
				
				if (null != s.getArtist()) {
					String str = s.getArtist();
					
					// put "The" in back for better sorting
					if (str.startsWith("The "))
						str = str.substring(4) + ", The";
					
					el.setAttribute("artist", str);
					hadAnyInfo = true;
				}
				
				if (hadAnyInfo)
					AC(songs, el);
			}
			
			// finally write out
			
			// prepare for serialization
			xml.setXmlStandalone(true);
			xml.getDocumentElement().normalize();
			
			FileOutputStream out = new FileOutputStream(new File(ttl.title + "_meta.xml"));
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

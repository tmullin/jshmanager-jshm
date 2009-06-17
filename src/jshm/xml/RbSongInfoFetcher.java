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

import jshm.Song;
import jshm.Song.RecordingType;

public class RbSongInfoFetcher {
	public static final String XML_URL =
		"http://pksage.com/xml.php";

	public Map<String, SongInfo> songMap = null;
	
	public void fetch() throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory f 
			= DocumentBuilderFactory.newInstance();
		f.setValidating(false);
		DocumentBuilder b = f.newDocumentBuilder();
		b.setErrorHandler(jshm.xml.ErrorHandler.getInstance());
		Document d = b.parse(XML_URL);
		d.getDocumentElement().normalize();
		
		songMap = new HashMap<String, SongInfo>();
		
		NodeList songNodes = d.getElementsByTagName("song");
		
		for (int i = songNodes.getLength() - 1; i >= 0; i--) {
			Element songEl = (Element) songNodes.item(i);
			
			SongInfo si = new SongInfo();
			
			si.genre = songEl.getElementsByTagName("genre").item(0).getTextContent();
			
//			if ("Unreleased".equals(si.genre)) continue;
			
			si.title = songEl.getElementsByTagName("songtitle").item(0).getTextContent();
			si.artist = songEl.getElementsByTagName("artist").item(0).getTextContent();
			si.album = songEl.getElementsByTagName("album").item(0).getTextContent();
			
			si.game = songEl.getElementsByTagName("game").item(0).getTextContent();
			si.pack = songEl.getElementsByTagName("pack").item(0).getTextContent();
			
			try { si.trackNum = Integer.parseInt(songEl.getElementsByTagName("tracknum").item(0).getTextContent());
			} catch (NumberFormatException e) {}
			
			try { si.year = Integer.parseInt(songEl.getElementsByTagName("year").item(0).getTextContent());
			} catch (NumberFormatException e) {}
			try { si.guitar = Integer.parseInt(songEl.getElementsByTagName("guitar").item(0).getTextContent());
			} catch (NumberFormatException e) {}
			try { si.bass = Integer.parseInt(songEl.getElementsByTagName("bass").item(0).getTextContent());
			} catch (NumberFormatException e) {}
			try { si.vocals = Integer.parseInt(songEl.getElementsByTagName("vocals").item(0).getTextContent());
			} catch (NumberFormatException e) {}
			try { si.drums = Integer.parseInt(songEl.getElementsByTagName("drums").item(0).getTextContent());
			} catch (NumberFormatException e) {}
			try { si.band = Integer.parseInt(songEl.getElementsByTagName("fullband").item(0).getTextContent());
			} catch (NumberFormatException e) {}
			
			try { si.guitarRb2 = Integer.parseInt(songEl.getElementsByTagName("guitarrb2").item(0).getTextContent());
			} catch (NumberFormatException e) {}
			try { si.bassRb2 = Integer.parseInt(songEl.getElementsByTagName("bassrb2").item(0).getTextContent());
			} catch (NumberFormatException e) {}
			try { si.vocalsRb2 = Integer.parseInt(songEl.getElementsByTagName("vocalsrb2").item(0).getTextContent());
			} catch (NumberFormatException e) {}
			try { si.drumsRb2 = Integer.parseInt(songEl.getElementsByTagName("drumsrb2").item(0).getTextContent());
			} catch (NumberFormatException e) {}
			try { si.bandRb2 = Integer.parseInt(songEl.getElementsByTagName("fullbandrb2").item(0).getTextContent());
			} catch (NumberFormatException e) {}
			
			si.recording = RecordingType.smartValueOf(songEl.getElementsByTagName("master").item(0).getTextContent());
			
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
			game,
			pack;
			
		public int
			trackNum = 0,
			year = 0,
			
			guitar = 0,
			bass = 0,
			vocals = 0,
			drums = 0,
			band = 0,
			
			guitarRb2 = 0,
			bassRb2 = 0,
			vocalsRb2 = 0,
			drumsRb2 = 0,
			bandRb2 = 0;
		
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
			sb.append(",src=");
			sb.append(game);
			sb.append(",p=");
			sb.append(pack);
			sb.append(",#=");
			sb.append(trackNum);
			sb.append(",y=");
			sb.append(year);
			sb.append(',');
			sb.append("[g=");
			sb.append(guitar);
			sb.append(',');
			sb.append(guitarRb2);
			sb.append(";b=");
			sb.append(bass);
			sb.append(',');
			sb.append(bassRb2);
			sb.append(";v=");
			sb.append(vocals);
			sb.append(',');
			sb.append(vocalsRb2);
			sb.append(";d=");
			sb.append(drums);
			sb.append(',');
			sb.append(drumsRb2);
			sb.append(";n=");
			sb.append(band);
			sb.append(',');
			sb.append(bandRb2);
			sb.append("],rt=");
			sb.append(recording.getAbbrChar());
			
			return sb.toString();
		}
	}
}

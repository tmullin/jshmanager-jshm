package jshm;

import java.awt.image.BufferedImage;
import java.net.URL;

import javax.imageio.ImageIO;

/**
 * This class holds information about a particular sp/od path for 
 * a particular song.
 * @author Tim Mullin
 *
 */
public class SpInfo {
	public Song song;
	public Difficulty difficulty;
	public Instrument.Group group;
	public String title;
	public String referenceUrl;
	public String imageUrl;
	public String description;
	public BufferedImage image;

	public void setImage(BufferedImage image) {
		this.image = image;
	}
	
	public void setImage(String url) throws Exception {
		setImage(new URL(url));
	}
	
	public void setImage(URL url) throws Exception {
		image = ImageIO.read(url);
	}
}

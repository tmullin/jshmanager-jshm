/*
 * -----LICENSE START-----
 * JSHManager - A Java-based tool for managing one's ScoreHero account.
 * Copyright (C) 2008 Tim Mullin
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

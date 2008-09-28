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
import java.io.*;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

//import org.hibernate.annotations.Type;

/**
 * Serves as a wrapper for {@link BufferedImage}. Contains a
 * byte array that holds the image as a PNG.
 * @author Tim Mullin
 *
 */
@Entity
public class Image {
	int id = 0;
	
	byte[] rawImage;
	transient BufferedImage image;

	public Image() throws IOException {
		this(null);
	}
	
	public Image(BufferedImage image) throws IOException {
		setImage(image);
	}
	
	@Id
	public int getId() {
		return id;
	}
	
	@SuppressWarnings("unused")
	private void setId(int id) {
		this.id = id;
	}
	
//	@Type(type="blob")
	public byte[] getRawImage() {
		return rawImage;
	}
	
	public void setRawImage(byte[] rawImage) throws IOException {
		if (null == rawImage)
			rawImage = new byte[0];
		
		this.rawImage = rawImage;
		
		if (rawImage.length == 0) {
			this.image = null;
		} else {
			ByteArrayInputStream in = new ByteArrayInputStream(rawImage);
			this.image = ImageIO.read(in);
		}
	}
	
	@Transient
	public BufferedImage getImage() {
		return image;
	}
	
	public void setImage(BufferedImage image) throws IOException {
		this.image = image;
		
		if (null == image) {
			rawImage = new byte[0];
		} else {
			ByteArrayOutputStream out = new ByteArrayOutputStream(
				image.getWidth() * image.getHeight() * 4);
			
			if (!ImageIO.write(image, "png", out))
				throw new IOException("unable to write png image");
			
			rawImage = out.toByteArray();
		}
	}
	
	public void setImage(String url) throws Exception {
		setImage(new URL(url));
	}
	
	public void setImage(URL url) throws Exception {
		setImage(ImageIO.read(url));
	}
	
	public void saveImage(String format, File dest) throws IOException {
		if ("png".equalsIgnoreCase(format)) {
			FileOutputStream out = null;
			
			try {
				out = new FileOutputStream(dest);
				out.write(rawImage);
			} finally {
				if (null != out) out.close();
			}
		} else {
			if (!ImageIO.write(image, format, dest))
				throw new IOException("invalid image format: " + format);
		}
	}
}

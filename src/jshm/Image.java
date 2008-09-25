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

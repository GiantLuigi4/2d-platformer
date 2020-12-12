package com.tfc.platformer.utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;

public class ResourceManager {
	public static InputStream getStream(String name) {
		return ResourceManager.class.getClassLoader().getResourceAsStream(name);
	}
	
	public static BufferedImage getImage(String name) {
		try {
			return ImageIO.read(getStream(name));
		} catch (Throwable ignored) {
			return null;
		}
	}
	
	public static PropertiesReader getPropertiesFromCL(String name) {
		try {
			InputStream stream = getStream(name);
			byte[] bytes = new byte[stream.available()];
			stream.read(bytes);
			String text = new String(bytes);
			stream.close();
			return new PropertiesReader(text);
		} catch (Throwable ignored) {
			return null;
		}
	}
}

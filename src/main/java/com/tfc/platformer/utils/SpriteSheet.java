package com.tfc.platformer.utils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;

public class SpriteSheet {
	private final BufferedImage source;
	private final PropertiesReader properties;
	
	private final int resX;
	private final int resY;
	
	private final HashMap<String, Image> sections = new HashMap<>();
	
	public SpriteSheet(String assetsSource) {
		this.source = (ResourceManager.getImage(assetsSource + "/sprite_sheet.png"));
		this.properties = (ResourceManager.getPropertiesFromCL(assetsSource + "/pallet.properties"));
		
		this.resX = getCoordX(properties.getValue("res"));
		this.resY = getCoordY(properties.getValue("res"));
	}
	
	public Image getAsset(String piece) {
		if (!sections.containsKey(piece)) {
			String val = properties.getValue(piece);
			sections.put(piece, source.getSubimage(getCoordX(val), getCoordY(val), resX, resY));
		}
		return sections.get(piece);
	}
	
	private static int getCoordX(String coords) {
		if (coords.contains(",")) return Integer.parseInt(coords.split(",")[0]);
		else return Integer.parseInt(coords.split("x")[0]);
	}
	
	private static int getCoordY(String coords) {
		if (coords.contains(",")) return Integer.parseInt(coords.split(",")[1]);
		else return Integer.parseInt(coords.split("x")[1]);
	}
}

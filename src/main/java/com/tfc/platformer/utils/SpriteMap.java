package com.tfc.platformer.utils;

import java.awt.*;
import java.util.HashMap;

public class SpriteMap {
	private final HashMap<String, Image> images = new HashMap<>();
	private final PropertiesReader properties;
	
	public SpriteMap(String name) {
		this.properties = (ResourceManager.getPropertiesFromCL(name + "/asset_list.properties"));
		
		properties.getEntries().forEach((entry)->{
			images.put(entry,ResourceManager.getImage(name+"/"+properties.getValue(entry)));
		});
	}
	
	public Image get(String entry) {
		return images.get(entry);
	}
}

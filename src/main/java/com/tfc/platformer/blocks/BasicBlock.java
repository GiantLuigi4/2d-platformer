package com.tfc.platformer.blocks;

import com.tfc.platformer.display.GraphicsWorld;
import com.tfc.platformer.logic.colliders.TexturedBoxCollider;
import com.tfc.platformer.logic.gui.GuiComponent;
import com.tfc.platformer.logic.gui.StringFieldComponent;

import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class BasicBlock extends TexturedBoxCollider {
	public AtomicReference<String> sheet = new AtomicReference<>("legacy");
	public AtomicReference<String> texture = new AtomicReference<>("brick");
	
	public BasicBlock(int width, int height, Supplier<Boolean> mirrorIn) {
		super(width, height, null, mirrorIn);
		setTextureGetter(() -> GraphicsWorld.getSheet(sheet.get()).getAsset(texture.get()));
	}
	
	public static BasicBlock fromString(String[] text) {
		BasicBlock block = new BasicBlock(4, 4, () -> false);
		block.setImmovable();
		block.sheet.set(text[0]);
		block.texture.set(text[1]);
		System.out.println(text[2] + "," + text[3]);
		block.move(Integer.parseInt(text[2]) * 8, Integer.parseInt(text[3]) * 8);
		return block;
	}
	
	public void addProperties(ArrayList<GuiComponent> components) {
		components.add(new StringFieldComponent(sheet, 0, 0, 0.25f, 0.1f, new Color(128, 128, 128), () -> {
		}));
		components.add(new StringFieldComponent(texture, 0, 0, 0.25f, 0.1f, new Color(128, 128, 128), () -> {
		}));
	}
}

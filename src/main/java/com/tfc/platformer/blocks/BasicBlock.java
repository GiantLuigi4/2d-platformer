package com.tfc.platformer.blocks;

import com.tfc.platformer.logic.colliders.TexturedBoxCollider;

import java.awt.*;
import java.util.function.Supplier;

public class BasicBlock extends TexturedBoxCollider {
	public String sheet = "legacy";
	public String texture = "brick";
	
	public BasicBlock(int width, int height, Supplier<Image> textureGetter, Supplier<Boolean> mirrorIn) {
		super(width, height, textureGetter, mirrorIn);
	}
}

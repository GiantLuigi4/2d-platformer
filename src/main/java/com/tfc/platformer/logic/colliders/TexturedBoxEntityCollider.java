package com.tfc.platformer.logic.colliders;

import java.awt.*;
import java.util.function.Supplier;

public class TexturedBoxEntityCollider extends TexturedBoxCollider implements EntityCollider {
	public TexturedBoxEntityCollider(int width, int height, Supplier<Image> textureGetter, Supplier<Boolean> mirrorIn) {
		super(width, height, textureGetter, mirrorIn);
	}
}

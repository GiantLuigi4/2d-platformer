package com.tfc.platformer.logic;

import com.tfc.physics.wrapper.common.colliders.BoxCollider;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.function.Supplier;

public class TexturedBoxCollider extends BoxCollider {
	private final Supplier<Image> textureGetter;
	private final Supplier<Boolean> mirrorIn;
	
	public TexturedBoxCollider(int width, int height, Supplier<Image> textureGetter, Supplier<Boolean> mirrorIn) {
		super(width, height);
		this.textureGetter = textureGetter;
		this.mirrorIn = mirrorIn;
	}
	
	@Override
	public void draw(Graphics2D g2d) {
		AffineTransform transform = g2d.getTransform();
		g2d.translate(this.getX(), this.getY());
		g2d.rotate((this.getAngle()));
		g2d.translate((isMirrored()?width:-width),-height);
		if (isMirrored()) g2d.scale(-1, 1);
		g2d.drawImage(this.getTexture(), 0, 0, width*2, height*2, null);
		g2d.setTransform(transform);
		
		super.draw(g2d);
	}
	
	public Image getTexture() {
		return this.textureGetter.get();
	}
	
	public boolean isMirrored() {
		return this.mirrorIn.get();
	}
}

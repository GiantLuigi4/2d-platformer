package com.tfc.platformer.logic.colliders;

import com.tfc.physics.wrapper.common.API.colliders.BoxCollider;
import com.tfc.platformer.Main;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.function.Supplier;

public class TexturedBoxCollider extends BoxCollider {
	private Supplier<Image> textureGetter;
	private final Supplier<Boolean> mirrorIn;
	
	public TexturedBoxCollider(int width, int height, Supplier<Image> textureGetter, Supplier<Boolean> mirrorIn) {
		super(width, height);
		this.textureGetter = textureGetter;
		this.mirrorIn = mirrorIn;
	}
	
	protected void setTextureGetter(Supplier<Image> textureGetter) {
		this.textureGetter = textureGetter;
	}
	
	@Override
	public void draw(Graphics2D g2d) {
		AffineTransform source = g2d.getTransform();
		g2d.translate(this.getX(), this.getY());
		g2d.rotate((this.getAngle()));
		AffineTransform transform = g2d.getTransform();
		g2d.translate((isMirrored() ? width : -width), -height);
		if (isMirrored()) g2d.scale(-1, 1);
		g2d.drawImage(this.getTexture(), 0, 0, width * 2, height * 2, null);
		g2d.setTransform(transform);
		if (Main.inEditor) {
			g2d.scale(0.1f,0.1f);
			g2d.drawRect(-width*10,-height*10,width*20,height*20);
		}
		g2d.setTransform(source);
		
//		super.draw(g2d);
	}
	
	public Image getTexture() {
		return this.textureGetter.get();
	}
	
	public boolean isMirrored() {
		return this.mirrorIn.get();
	}
}

package com.tfc.platformer.logic.gui;

import com.tfc.platformer.logic.helpers.Box2D;
import com.tfc.platformer.utils.ResourceManager;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class ImageButtonComponent extends GuiComponent {
	private float x,y;
	private final float width,height;
	private final Runnable onClick;
	private final Image image;
	
	public ImageButtonComponent(String image, float x, float y, float width, float height, Runnable onClick) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.image = ResourceManager.getImage(image);
		this.onClick = onClick;
	}
	
	@Override
	public void draw(Graphics2D g2d, boolean isHovered) {
		AffineTransform source = g2d.getTransform();
		g2d.translate(x,y);
		g2d.scale(width,height);
		g2d.drawImage(image,0,0,1,1,null);
		g2d.setTransform(source);
	}
	
	@Override
	public void onClick() {
		onClick.run();
	}
	
	@Override
	public void setX(float newX) {
		this.x = newX;
	}
	
	@Override
	public void setY(float newY) {
		this.y = newY;
	}
	
	@Override
	public boolean isInBounds(float x, float y) {
		return Box2D.create(this.x,this.y,width,height).collides(Box2D.create(x,y,0.001f,0.001f));
	}
}

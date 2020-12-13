package com.tfc.platformer.logic.gui;

import com.tfc.platformer.Main;
import com.tfc.platformer.logic.helpers.Box2D;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.concurrent.atomic.AtomicReference;

public class StringFieldComponent extends GuiComponent implements IKeyListenerComponent {
	private final float width, height;
	private final Runnable onClick;
	private final Color color;
	private final AtomicReference<String> text;
	private float x, y;
	
	public StringFieldComponent(AtomicReference<String> text, float x, float y, float width, float height, Color color, Runnable onClick) {
		this.text = text;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.color = color;
		this.onClick = onClick;
	}
	
	@Override
	public void draw(Graphics2D g2d, boolean isHovered, boolean isFocused) {
		Color color = this.color;
		if (isHovered) color = color.darker();
		if (isFocused) color = color.darker();
		AffineTransform source = g2d.getTransform();
		g2d.setColor(color);
		g2d.translate(x, y);
		AffineTransform transform = g2d.getTransform();
		g2d.scale(width, height);
		g2d.fillRect(0, 0, 1, 1);
		g2d.scale(height / width, width / height);
		g2d.scale(1f / 16, 1f / 16);
		AffineTransform outline = g2d.getTransform();
		g2d.setColor(new Color(255, 255, 255));
		g2d.setTransform(transform);
		g2d.scale(width, width);
		g2d.translate(0, (height * 3f));
		g2d.scale(0.025f / 2, 0.025f);
		float width = g2d.getFontMetrics().stringWidth(text.get());
		g2d.translate(width / 6f, 0);
		g2d.drawString(text.get(), 0, 0);
		g2d.setTransform(outline);
		g2d.setColor(color.darker());
		g2d.drawRect(0, 0, 20 * 2, 3 * 2);
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
		return Box2D.create(this.x, this.y, width, height).collides(Box2D.create(x, y, 0.001f, 0.001f));
	}
	
	@Override
	public void onKeyPressed(int key) {
		if (key == 8) this.text.set(this.text.get().substring(0, this.text.get().length() - 1));
		else if (key == 16) ;
		else if (Main.inputController.isKeyPressed(16)) this.text.set(this.text.get() + ((char) key));
		else this.text.set(this.text.get() + ((((char) key) + "").toLowerCase()));
	}
}

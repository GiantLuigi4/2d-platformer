package com.tfc.platformer.logic.gui;

import java.awt.*;

public abstract class GuiComponent {
	public abstract void draw(Graphics2D g2d, boolean isHovered);
	public abstract void onClick();
	
	public abstract void setX(float newX);
	public abstract void setY(float newY);
	
	public abstract boolean isInBounds(float x, float y);
}

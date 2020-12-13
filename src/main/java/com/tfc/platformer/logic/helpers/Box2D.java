package com.tfc.platformer.logic.helpers;

import javax.swing.*;

public class Box2D {
	private Line2D box;
	
	public Box2D(Line2D box) {
		this.box = box;
	}
	
	public boolean collides(Box2D other) {
		double startX = box.getStart().x;
		double endX = box.getEnd().x;
		double startY = box.getStart().y;
		double endY = box.getEnd().y;
		
		//Calc the correct starts and ends
		double tempVar1 = startX;
		double tempVar2 = endX;
		startX = Math.min(tempVar1, tempVar2);
		endX = Math.max(tempVar1, tempVar2);
		tempVar1 = startY;
		tempVar2 = endY;
		startY = Math.min(tempVar1, tempVar2);
		endY = Math.max(tempVar1, tempVar2);
		
		double startX2 = other.box.getStart().x;
		double endX2 = other.box.getEnd().x;
		double startY2 = other.box.getStart().y;
		double endY2 = other.box.getEnd().y;
		
		//Calc the correct starts and ends
		tempVar1 = startX2;
		tempVar2 = endX2;
		startX2 = Math.min(tempVar1, tempVar2);
		endX2 = Math.max(tempVar1, tempVar2);
		tempVar1 = startY2;
		tempVar2 = endY2;
		startY2 = Math.min(tempVar1, tempVar2);
		endY2 = Math.max(tempVar1, tempVar2);
		
		return (
				endX2 > startX &&
						endY2 > startY &&
						startX2 < endX &&
						startY2 < endY
		);
	}
	
	public Vector2D getCenter() {
		return box.getPoint(0.5);
	}
	
	public static Box2D create(float x, float y, float width, float height) {
		return new Box2D(
				new Line2D(
						new Vector2D(x,y),
						new Vector2D(width,height)
				)
		);
	}
}

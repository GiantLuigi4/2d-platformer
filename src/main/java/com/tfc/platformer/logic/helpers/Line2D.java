package com.tfc.platformer.logic.helpers;

public class Line2D {
	private Vector2D start,ray;
	
	public Line2D(Vector2D start, Vector2D ray) {
		this.start = start;
		this.ray = ray;
	}
	
	public Line2D(Vector2D start, double v, double v1) {
		this.start = start;
		this.ray = new Vector2D(start.x-v,start.y-v1);
	}
	
	public Line2D(float v, float v1, float v2, float v3) {
		this.start = new Vector2D(v,v1);
		this.ray = new Vector2D(v2,v3).subtract(v,v1);
	}
	
	public Vector2D getStart() {
		return start;
	}
	
	public Vector2D getEnd() {
		return start.add(ray);
	}
	
	//https://stackoverflow.com/questions/563198/how-do-you-detect-where-two-line-segments-intersect
	public boolean lineIntersects(Line2D other) {
		Vector2D end = getEnd();
		Vector2D otherStart = other.start;
		Vector2D otherEnd = other.getEnd();
		if (start.x == end.x) {
			return !(otherStart.x == otherEnd.x && start.x != otherStart.x);
		} else if (otherStart.x == otherEnd.x) {
			return true;
		} else {
			double m1 = (start.y-end.y)/(start.x-end.x);
			double m2 = (otherStart.y-otherEnd.y)/(otherStart.x-otherEnd.x);
			return m1 != m2;
		}
	}
	
	public boolean intersects(Line2D other) {
		return
				new Box2D(this).collides(new Box2D(other)) &&
						//TODO:stop using java.awt
						new java.awt.geom.Line2D.Double(start.x, start.y, getEnd().x, getEnd().y).intersects(other.start.x, other.start.y, other.getEnd().x, other.getEnd().y) &&
						new java.awt.geom.Line2D.Double(start.x, start.y, getEnd().x, getEnd().y).intersectsLine(other.start.x, other.start.y, other.getEnd().x, other.getEnd().y) &&
						lineIntersects(other)
				;
	}
	
	public Vector2D getPoint(double dist) {
		return start.lerp(dist,getEnd());
	}
}
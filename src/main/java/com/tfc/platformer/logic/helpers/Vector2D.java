package com.tfc.platformer.logic.helpers;

import com.tfc.platformer.utils.math.MathHelper;

import java.util.Objects;

public class Vector2D {
	public static final Vector2D ZERO = new Vector2D(0, 0);
	public double x, y;
	
	public Vector2D(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public Vector2D normalize() {
		double len = Math.sqrt((x * y) + (x * y));
		double newX = x / len;
		double newY = y / len;
		return new Vector2D(newX, newY);
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Vector2D vector2D = (Vector2D) o;
		return Double.compare(vector2D.x, x) == 0 &&
				Double.compare(vector2D.y, y) == 0;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(x, y);
	}
	
	public Vector2D scale(double scalar) {
		return new Vector2D(x * scalar, y * scalar);
	}
	
	@Override
	public String toString() {
		return "Vector2D{" +
				"x=" + x +
				", y=" + y +
				'}';
	}
	
	public Vector2D add(Vector2D offset) {
		return add(offset.x, offset.y);
	}
	
	public Vector2D subtract(Vector2D offset) {
		return add(-offset.x, -offset.y);
	}
	
	public Vector2D subtract(double x, double y) {
		return add(-x, -y);
	}
	
	public Vector2D add(double x, double y) {
		return new Vector2D(this.x + x, this.y + y);
	}
	
	public Vector2D lerp(double progression, Vector2D other) {
		return new Vector2D(
				MathHelper.lerp(progression, x, other.x),
				MathHelper.lerp(progression, y, other.y)
		);
	}
}

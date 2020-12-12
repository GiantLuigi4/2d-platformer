package com.tfc.platformer.utils.math;

public class MathHelper {
	public static float lerp(float pct, float start, float end) {
		return (((1-pct)*start)+((pct)*end));
	}
	
	public static double lerp(double pct, double start, double end) {
		return (((1-pct)*start)+((pct)*end));
	}
}

package com.example.atest;

public class Vector {
	public double x, y;
	private static double dx;
	private static double dy;
	
	public Vector(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public static Vector mult(Vector v, double val) {
		return new Vector(v.x * val, v.y * val);
	}
	
	public static Vector div(Vector v, double val) {
		return new Vector(v.x / val, v.y / val);
	}
	
	public static Vector add(Vector v1, Vector v2) {
		return new Vector(v1.x + v2.x, v1.y + v2.y);
	}
	
	public static double distance(Vector v1, Vector v2) {
		dx = v1.x - v2.x;
		dy = v1.y - v2.y;
		return Math.sqrt(dx * dx + dy * dy);
	}
}

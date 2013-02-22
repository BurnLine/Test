package com.example.atest;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Star {
	public double weight;
	private double diameter;
	private Paint paint;
	private Paint black;
	private Vector speed;
	public Vector position;
	public boolean active;
	
	private double gravity = 0, d = 0;
	private Vector v = new Vector(0, 0);
	
	public Star(double x, double y, double weight, Vector speed) {
		active = true;
		this.speed = speed;
		position = new Vector(x, y);
		this.weight = weight;
		paint = new Paint();
		paint.setColor(Color.GREEN);
		black = new Paint();
		black.setColor(Color.WHITE);
		recalcDiameter();
	}
	
	public void draw(Canvas canvas, Vector o) {
		if (!active)
			return;
		
		canvas.drawCircle((float)(o.x + position.x), (float)(o.y + position.y), (float)diameter, paint);
		canvas.drawLine((float)(o.x + position.x),
				(float)(o.y + position.y),
				(float)(o.x + position.x + speed.x * 10),
				(float)(o.y + position.y + speed.y * 10), black);
	}
	
	public void updateSpeedVector(Star star2) {
		if (!active || !star2.active)
			return;
		
		d = Vector.distance(position, star2.position);
		
		if (d < diameter + star2.diameter) {
			if (star2.weight < weight) {
				weight += star2.weight;
				star2.active = false;
				speed = Vector.div(Vector.add(Vector.mult(speed, weight), Vector.mult(star2.speed, star2.weight)),
						weight + star2.weight);
				recalcDiameter();
				return;
			} else {
				star2.weight += weight;
				active = false;
				star2.speed = Vector.div(Vector.add(Vector.mult(speed, weight), Vector.mult(star2.speed, star2.weight)),
						weight + star2.weight);
				star2.recalcDiameter();
				return;
			}
		}
		
		v.x = (star2.position.x - position.x) / d;
		v.y = (star2.position.y - position.y) / d;
		gravity = ((weight * star2.weight) / (d * d)) * (1 / weight);
		
		//speed = Vector.add(speed, new Vector(v.x * gravity, v.y * gravity));
		speed.x += v.x * gravity;
		speed.y += v.y * gravity;
	}
	
	public void updatePosition() {
		//position = Vector.add(position, speed);
		position.x += speed.x;
		position.y += speed.y;
	}
	
	public void recalcDiameter() {
		diameter = Math.pow(4.0 / 3.0 * weight, 1 / 3.0);
	}
}

package gameEngine;

import java.awt.Point;

public class PhysicalCircle {
	/*
	 * Class for a basic 2D ball / circle shaped object; Position, Velocity,
	 * Radius, General purpose timer t, Basic physics routines
	 */
	// Attributes are public for C-Like access...
	public double x;
	public double y;
	public double vx = 0;
	public double vy = 0;
	public double rad;
	public long t = 0;
	
	/**
	 * Converts to Java awt Point
	 * @return point
	 */

	public Point toPoint() {
		return new Point((int) x, (int) y);
	}
	
	/**
	 * C'tor using coordinates
	 * 
	 * @param x		x position
	 * @param y		y position
	 * @param rad	radius of the circle
	 */

	public PhysicalCircle(double x, double y, double rad) {
		this.x = x;
		this.y = y;
		this.rad = rad;
	}
	
	/**
	 * C'tor using Java awt point, no radius specified
	 * 
	 * @param p		equivalent x,y point
	 */

	public PhysicalCircle(Point p) {
		this.x = p.x;
		this.y = p.y;
	}

	/**
	 * Calculates physics collision width 4 walls / borders
	 * Speeds are slowed down by 10%
	 * 
	 * @param xmin	left border
	 * @param ymin	top border
	 * @param xmax	right border
	 * @param ymax	bottom border
	 */
	
	public void collideWall(double xmin, double ymin, double xmax, double ymax) {
		if (x - rad < xmin) {
			x = xmin + rad;
			vx = -vx * .9;
		}
		if (x + rad > xmax) {
			x = xmax - rad;
			vx = -vx * .9;
		}
		if (y - rad < ymin) {
			y = ymin + rad;
			vy = -vy * .9;
		}
		if (y + rad > ymax) {
			y = ymax - rad;
			vy = -vy * .9;
		}
	}
	/**
	 * Limits the x and y speed components of the ball and slows it down (not the magnitude!)
	 * 
	 * @param maxspeed	maximum speed for one speed component
	 * @param fadeout	damping factor
	 */
	public void constrainSpeed(double maxspeed, double fadeout) {
		vx *= fadeout;
		vy *= fadeout;
		if (Math.abs(vx) < .001)
			vx = 0;
		if (Math.abs(vy) < .001)
			vy = 0;
		if (vx > maxspeed)
			vx = maxspeed;
		if (vx < -maxspeed)
			vx = -maxspeed;
		if (vy > maxspeed)
			vy = maxspeed;
		if (vy < -maxspeed)
			vy = -maxspeed;
	}
	/**
	 * updates the position based on current speed
	 */
	public void updatePosition() {
		x += vx;
		y += vy;
	}
	
	/**
	 * calculates collision with another PhysicalCircle
	 * forbids intersection
	 * 
	 * @param o		other PhysicalCircle
	 */

	public void collideStatic(PhysicalCircle o) {
		if (this == o)
			return;
		double s = this.rad + o.rad;
		double d = Math.sqrt((this.x - o.x) * (this.x - o.x) + (this.y - o.y) * (this.y - o.y));
		double a = Math.atan2(this.y - o.y, this.x - o.x);

		if (d < s) {
			this.x = o.x + s * Math.cos(a);
			this.y = o.y + s * Math.sin(a);
		}

	}
	
	/**
	 * calculates collision with another PhysicalCircle
	 * makes them bounce apart
	 * 
	 * @param o		other PhysicalCircle
	 * @param speed	value to limit speed due to numerical error
	 */
	public void collideBouncy(PhysicalCircle o, double speed) {
		if (this == o)
			return;
		double s = this.rad + o.rad;
		double d = Math.sqrt((this.x - o.x) * (this.x - o.x) + (this.y - o.y) * (this.y - o.y));
		double a = Math.atan2(this.y - o.y, this.x - o.x);

		if (d < s) {
			this.x = o.x + s * Math.cos(a);
			this.y = o.y + s * Math.sin(a);
			this.vx -= (o.x - this.x) * 2 / d * speed / 5;
			this.vy -= (o.y - this.y) * 2 / d * speed / 5;
		}

	}

	/**
	 * Makes this circle follow another circle but retaining inertia
	 * 
	 * @param o the other circle
	 */
	public void followBouncy(PhysicalCircle o) {
		if (this == o)
			return;
		double s = this.rad + o.rad;
		double a = Math.atan2(this.y - o.y, this.x - o.x);
		this.vx += (o.x + s * Math.cos(a) - this.x) / s / 32;
		this.vy += (o.y + s * Math.sin(a) - this.y) / s / 32;
		this.x += (o.x + s * Math.cos(a) - this.x) / s * 24 + o.vx * .24;
		this.y += (o.y + s * Math.sin(a) - this.y) / s * 24 + o.vy * .24;
	}
	/**
	 * Makes this circle follow another circle so they always contact each other
	 * 
	 * @param o the other circle
	 */
	public void followStatic(PhysicalCircle o) {
		if (this == o)
			return;
		double s = this.rad + o.rad;
		double a = Math.atan2(this.y - o.y, this.x - o.x);
		this.x = (o.x + s * Math.cos(a));
		this.y = (o.y + s * Math.sin(a));
	}
	/**
	 * checks whether this circle is closer than the threshold to another
	 * 
	 * @param o	other circle
	 * @param thresholdDistance		minimum distance between the outlines of the circles
	 * @return	true when they're close
	 */
	public boolean isColliding(PhysicalCircle o, double thresholdDistance) {
		double d = Math.sqrt((this.x - o.x) * (this.x - o.x) + (this.y - o.y) * (this.y - o.y));
		double s = this.rad + o.rad;
		return d < s + thresholdDistance;
	}

	public double getAbsoluteVelocity() {
		return Math.sqrt(vx * vx + vy * vy);
	}

	public double getDistanceTo(PhysicalCircle o) {
		return Math.sqrt((this.x - o.x) * (this.x - o.x) + (this.y - o.y) * (this.y - o.y)) - (this.rad - o.rad) / 2;
	}

	public double getAngleTo(PhysicalCircle o) {
		return Math.atan2(o.y - this.y, o.x - this.x);
	}
}

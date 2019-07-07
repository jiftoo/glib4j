package net.x666c.glib.math.vector;

import java.awt.geom.Point2D;

public class Point extends Point2D.Float {
	public Point() {
		x = 0;
		y = 0;
	}
	
	public Point(float x, float y) {
		super(x, y);
	}
	
	public static Point fromawt(java.awt.Point point) {
		if(point == null)
			return null;
		return new Point(point.x, point.y);
	}
	
	public static Point zero() {
		return new Point(0, 0);
	}
	
	public static Point inf() {
		return new Point(java.lang.Float.POSITIVE_INFINITY, java.lang.Float.POSITIVE_INFINITY);
	}
	
	public Vector tovec() {
		return Vector.create(x, y);
	}
}

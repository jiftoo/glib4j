package net.x666c.glib.math.vector;

import java.awt.geom.Line2D;

public class Line extends Line2D.Float {
	
	public Line(float x1, float y1, float x2, float y2) {
		super(x1, y1, x2, y2);
	}
	
	public Line(int x1, int y1, int x2, int y2) {
		super(x1, y1, x2, y2);
	}
	
	public Line(Vector v1, Vector v2) {
		this(v1.x, v1.y, v2.x, v2.y);
	}
	
	public Line(Point v1, Point v2) {
		this(v1.x, v1.y, v2.x, v2.y);
	}

}

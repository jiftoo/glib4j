package io.x666c.glib4j.math.phys;

import java.awt.geom.Rectangle2D;

public class Collider extends Rectangle2D.Double {
	
	// Just a wrapper class with better naming :)
	
	public Collider(float x, float y, float w, float h) {
		super(x, y, w, h);
	}
	public Collider(int x, int y, float w, float h) {
		super(x, y, w, h);
	}
	public Collider(float x, float y, int w, int h) {
		super(x, y, w, h);
	}
	public Collider(int x, int y, int w, int h) {
		super(x, y, w, h);
	}
	
	public float width() {
		return (float) width;
	}
	
	public float height() {
		return (float) height;
	}
	
	public float x() {
		return (float) x;
	}
	
	public float y() {
		return (float) y;
	}
	
	@Override
	public String toString() {
		return String.format("Collider[x=%.2f, y=%.2f, width=%.2f, height=%.2f]", x, y, width, height);
	}
	
}
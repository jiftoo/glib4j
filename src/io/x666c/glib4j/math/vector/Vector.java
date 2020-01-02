package io.x666c.glib4j.math.vector;

public class Vector implements Cloneable {
	
	public static final Vector ZERO = Vector.zero();

	public float x;
	public float y;

	// Constructor methods ....

	public Vector() {
		x = y = 0.0f;
	}

	public Vector(float dX, float dY) {
		this.x = dX;
		this.y = dY;
	}

	// Convert vector to a string ...

	public String toString() {
		return "Vector(" + x + ", " + y + ")";
	}

	// Compute magnitude of vector ....

	public double magnitude() {
		return Math.sqrt((x * x) + (y * y));
	}

	// Sum of two vectors ....

	public Vector add(Vector v1) {
		this.x += v1.x;
		this.y += v1.y;
		return this;
	}

	// Subtract vector v1 from v .....

	public Vector sub(Vector v1) {
		this.x -= v1.x;
		this.y -= v1.y;
		return this;
	}
	
	// Multiply
	
	public Vector mul(Vector v1) {
		this.x *= v1.x;
		this.y *= v1.y;
		return this;
	}
	
	// Divide

	public Vector div(Vector v1) {
		this.x /= v1.x;
		this.y /= v1.y;
		return this;
	}

	// Scale vector by a constant ...

	public Vector scale(float scaleFactor) {
		this.x *= scaleFactor;
		this.y *= scaleFactor;
		return this;
	}
	
	// Distance
	
	public float distance(Vector other) {
		return (float) (Math.sqrt( ((x - other.x) * (x - other.x)) + ((y - other.y) * (y - other.y)) ));
	}
	
	public static float distanceBetween(Vector v1, Vector v2) {
		return (float) (Math.sqrt( ((v1.x - v2.x) * (v1.x - v2.x)) + ((v1.y - v2.y) * (v1.y - v2.y)) ));
	}
	
	// Middle point of a circle
	
	public Vector middle(float radius) {
		return Vector.create(x + radius, y + radius);
	}
	
	// Angle between
	
	public float angleBetween(Vector v2) {
		//return (float) Math.acos(dotProduct(v2) / (magnitude() * v2.magnitude()));
		return (float) (Math.atan2(y,x) - Math.atan2(v2.y,v2.x));
	}
	
	// Rotation angle (in radians)
	
	public float heading() {
		return (float)Math.atan2(y, x);
	}
	
	// Inverse
	
	public Vector inverse() {
		x = -x;
		y = -y;
		return this;
	}

	// Normalize a vectors length....

	public Vector normalize() {
		float length = (float) Math.sqrt(this.x * this.x + this.y * this.y);
		if (length != 0) {
			this.x = this.x / length;
			this.y = this.y / length;
		}

		return this;
	}
	
	public void limit(float to) {
		limit(to, to);
	}
	
	public void limit(float toX, float toY) {
		x = (Math.abs(x) > toX) ? toX * Math.signum(x) : x;
		y = (Math.abs(y) > toY) ? toY * Math.signum(y) : y;
	}
	
	// Overrides
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Vector) {
			Vector cmp = (Vector)obj;
			return ((x == cmp.x) && (y == cmp.y));
		}
		return false;
	}
	@Override
	public int hashCode() {
		return (Float.hashCode(x) | Float.hashCode(y));
	}
	@Override
	public Vector clone() {
		return new Vector(x, y);
	}
	
	//
	
	public void set(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public void set(Vector val) {
		set(val.x, val.y);
	}
	
	public void set(Point val) {
		set(val.x, val.y);
	}
	
	// Dot product of two vectors .....

	public float dotProduct(Vector v1) {
		return this.x * v1.x + this.y * v1.y;
	}
	
	// Cross product of two vectors .....
	
	/*public float crossProduct(Vector v2) {
		
	}*/
	
	public Point toPoint() {
		return new Point(x, y);
	}
	
	// Static //
	
	public static Vector fromAngle(float angle) {
		angle = (float)Math.toRadians(angle);
		return Vector.create((float)Math.cos(angle), (float)Math.sin(angle));
	}
	
	public static Vector create(float x, float y) {
		return new Vector(x, y);
	}
	
	public static Vector zero() {
		return new Vector(0, 0);
	}
	
	public static Vector up() {
		return new Vector(0, -1);
	}
	
	public static Vector down() {
		return new Vector(0, 1);
	}
	
	public static Vector left() {
		return new Vector(-1, 0);
	}
	
	public static Vector right() {
		return new Vector(1, 0);
	}
	
	public static Vector random() {
		return new Vector((float)Math.random() * 2 - 1, (float)Math.random() * 2 - 1);
	}
}
# glib4j
A simple graphics library based on Swing

Example usage:

![alt text](https://raw.githubusercontent.com/0x666c/glib4j/master/e73fbc4404be5bcb04ba40d07ae1bf3d.png)

```java
static List<Vector> vecs;
	static Vector middle = new Vector(400, 400);

	static volatile int dir = 1;
	static volatile float maxDist = 10;

	public static void main(String[] args) {
		GFrame f = new GFrame(60, 60, Example::draw, Example::update);
		f.setSize(800, 800);
		f.background(0);
		f.synchronize(true); // draw() and update() will be executed on single thread

		f.onKeyPress(' ', () -> f.pause(!f.isPaused()));

		f.addSlider("Dots amount:", 10, 500, 10, val -> generateList(val));

		generateList(200);

		f.start(); // Start
	}

	static void generateList(int amt) {
		vecs = Stream.generate(middle::clone).limit(amt).collect(Collectors.toList());
	}

	static void update() {
		float i = 0;
		final float inc = 360f / vecs.size();

		for (Vector v : vecs) {
			v.add(Vector.fromAngle(i).scale(MathUtil.random(3f, 6f) * dir));
			i += inc;

			if (v.distance(middle) > maxDist) {
				dir = -dir;
				maxDist += 10f;
				break;
			}
		}
	}

	static void draw(Renderer r) {
		for (Vector v : vecs) {
			r.color(Color.HSBtoRGB(MathUtil.map(v.distance(middle), 0, 400, 0, 1), 1f, 1f));
			r.alpha(MathUtil.map(v.distance(middle), 0, 400, 1, 0));

			r.point(v, 2);
		}
	}
```

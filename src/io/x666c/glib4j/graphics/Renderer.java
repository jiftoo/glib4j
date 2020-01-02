package io.x666c.glib4j.graphics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

import io.x666c.glib4j.GFrame;
import io.x666c.glib4j.input.Input;
import io.x666c.glib4j.math.phys.Collider;
import io.x666c.glib4j.math.vector.Line;
import io.x666c.glib4j.math.vector.Point;
import io.x666c.glib4j.math.vector.Vector;
import io.x666c.glib4j.util.FixedStack;

public class Renderer {
	
	protected static final BasicStroke NOSTROKE = new BasicStroke(1);
	
	protected Graphics2D g;
	private final GFrame gFrame;
	
	protected final FixedStack<Graphics2D> graphic_stack;
	protected final FixedStack<double[]> matrix_stack;
	
	protected volatile boolean fill = false; // Mode
	
	public Renderer(Graphics2D g, GFrame gFrame) {
		this.g = g;
		this.gFrame = gFrame;
		
		this.graphic_stack = new FixedStack<>(10);
		this.matrix_stack = new FixedStack<>(10);
		
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	}
	
	// Color
	
	public void color(Color color) {
		g.setColor(color);
	}
	
	public void color(int r, int g, int b) {
		this.g.setColor(new Color(r, g, b));
	}
	
	public void color(float r, float g, float b) {
		this.g.setColor(new Color(r, g, b));
	}
	
	public void color(int r, int g, int b, int a) {
		this.g.setColor(new Color(r, g, b, a));
	}
	
	public void color(float r, float g, float b, float a) {
		this.g.setColor(new Color(r, g, b, a));
	}
	
	public void color(int rgb) {
		g.setColor(new Color(rgb));
	}
	
	// Alpha
	
	/**
	 * @param a zero to one range (0 - 1)
	 */
	public void alpha(float a) {
		if(a < 0f) {
			a = 0;
		} else if(a > 1f) {
			a = 1f;
		} // Quick fix
		final Color cur = g.getColor();
		color(cur.getRed(), cur.getGreen(), cur.getBlue(), (int)(a * 255));
	}
	
	/**
	 * @param int in 0 - 255 range
	 */
	public void alpha(int a) {
		if(a < 0) {
			a = 0;
		} else if(a > 255) {
			a = 255;
		} // Quick fix
		final Color cur = g.getColor();
		color(cur.getRed(), cur.getGreen(), cur.getBlue(), a);
	}
	
	// Circle
	
	public void circle(float x, float y, float radius) {
		if(fill)
			g.fill(new Ellipse2D.Float(x, y, radius * 2f, radius * 2f));
		else
			g.draw(new Ellipse2D.Float(x, y, radius * 2f, radius * 2f));
	}
	public void circle(int x, int y, int radius) {
		if(fill)
			g.fillOval(x, y, radius * 2, radius * 2);
		else
			g.drawOval(x, y, radius * 2, radius * 2);
	}
	
	// Oval
	
	public void oval(float x, float y, float width, float height) {
		if (fill)
			g.fill(new Ellipse2D.Float(x, y, width, height));
		else
			g.draw(new Ellipse2D.Float(x, y, width, height));
	}
	public void oval(int x, int y, int width, int height) {
		if (fill)
			g.fillOval(x, y, width, height);
		else
			g.drawOval(x, y, width, height);
	}

	// Rectangle
	
	public void rectangle(float x, float y, float width, float height) {
		if (fill)
			g.fill(new Rectangle2D.Float(x, y, width, height));
		else
			g.draw(new Rectangle2D.Float(x, y, width, height));
	}
	public void rectangle(int x, int y, int width, int height) {
		if (fill)
			g.fillRect(x, y, width, height);
		else
			g.drawRect(x, y, width, height);
	}
	public void rectangle(Collider c) {
		if (fill)
			g.fill(c);
		else
			g.draw(c);
	}
	
	// Polygon
	

	public void polygon(Vector p1, Vector p2, Vector p3) {
		if (fill)
			g.fillPolygon(new int[] {(int) p1.x, (int) p2.x, (int) p3.x}, new int[] {(int) p1.y, (int) p2.y, (int) p3.y}, 3);
		else
			g.drawPolygon(new int[] {(int) p1.x, (int) p2.x, (int) p3.x}, new int[] {(int) p1.y, (int) p2.y, (int) p3.y}, 3);
	}
	public void polygon(int x1, int y1, int x2, int y2, int x3, int y3) {
		if (fill)
			g.fillPolygon(new int[] {x1, x2, x3}, new int[] {y1, y2, y3}, 3);
		else
			g.drawPolygon(new int[] {x1, x2, x3}, new int[] {y1, y2, y3}, 3);
	}
	public void polygon(float x1, float y1, float x2, float y2, float x3, float y3) {
		if (fill)
			g.fillPolygon(new int[] {(int) x1, (int) x2, (int) x3}, new int[] {(int) y1, (int) y2, (int) y3}, 3);
		else
			g.drawPolygon(new int[] {(int) x1, (int) x2, (int) x3}, new int[] {(int) y1, (int) y2, (int) y3}, 3);
	}
	
	// Square
	
	public void square(int x, int y, int size) {
		if(fill)
			g.fillRect(x, y, size, size);
		else
			g.drawRect(x, y, size, size);
	}
	public void square(float x, float y, float size) {
		if(fill)
			g.fill(new Rectangle2D.Float(x, y, size, size));
		else
			g.draw(new Rectangle2D.Float(x, y, size, size));
	}
	public void square(Collider c, float size) {
		square(c.x(), c.y(), size);
	}
	
	// Point
	
	public void point(int x, int y) {
		if(pointSize == 0)
			return;
		fill();
		if(pointSize == 1)
			g.drawLine(x, y, x, y);
		else
			circle(x - pointSize/2f, y - pointSize/2f, pointSize);
		draw();
	}
	
	public void point(float x, float y) {
		if(pointSize == 0)
			return;
		fill();
		if(pointSize == 1)
			g.drawLine((int)x, (int)y, (int)x, (int)y);
		else
			circle(x - pointSize/2f, y - pointSize/2f, pointSize);
		draw();
	}
	public void point(Point p) {
		fill();
		circle(p.x - pointSize/2f, p.y - pointSize/2f, pointSize);
		draw();
	}
	public void point(Vector v) {
		fill();
		circle(v.x - pointSize/2f, v.y - pointSize/2f, pointSize);
		draw();
	}
	
	public void point(float x, float y, float size) {
		fill();
		circle(x - size/2f, y - size/2f, size);
		draw();
	}
	public void point(Point p, float size) {
		fill();
		circle(p.x - size/2f, p.y - size/2f, size);
		draw();
	}
	public void point(Vector v, float size) {
		fill();
		circle(v.x - size/2f, v.y - size/2f, size);
		draw();
	}
	
	// Point size
	
	volatile float pointSize = 1;
	
	public void pointSize(float size) {
		if(size < 1f) {
			size = 1f;
		} // Avoid negative size
		pointSize = size;
	}
	
	public void pointSize(int size) {
		if(size < 1) {
			size = 1;
		} // Avoid negative size
		pointSize = (float)size;
	}
	
	// Line
	
	public void line(int x1, int y1, int x2, int y2) {
		g.drawLine(x1, y1, x2, y2);
	}
	public void line(float x1, float y1, float x2, float y2) {
		g.draw(new Line2D.Float(x1, y1, x2, y2));
	}
	public void line(Line line) {
		g.draw(line);
	}
	public void line(Vector v1, Vector v2) {
		g.drawLine((int)v1.x, (int)v1.y, (int)v2.x, (int)v2.y);
	}
	public void line(Point v1, Point v2) {
		g.drawLine((int)v1.x, (int)v1.y, (int)v2.x, (int)v2.y);
	}
	
	// Image
	
	public void image(BufferedImage image, float x, float y, float width, float height) {
		g.drawImage(image, (int)x, (int)y, (int)width, (int)height, null);
	}
	public void image(BufferedImage image, int x, int y, int width, int height) {
		g.drawImage(image, x, y, width, height, null);
	}
	
	public void image(BufferedImage image, float x, float y) {
		image(image, x, y, image.getWidth(), image.getHeight());
	}
	public void image(BufferedImage image, int x, int y) {
		image(image, x, y, image.getWidth(), image.getHeight());
	}
	
	// Text
	
	public void text(String text, int x, int y) {
		g.drawString(text, x, y);
	}
	public void text(String text, float x, float y) {
		g.drawString(text, x, y);
	}
	
	public void text(String text, float x, float y, int size) {
		Font current = g.getFont();
		font(current.deriveFont((float) size));
		g.drawString(text, x, y);
		font(current);
	}
	public void text(String text, int x, int y, int size) {
		Font current = g.getFont();
		font(current.deriveFont((float) size));
		g.drawString(text, x, y);
		font(current);
	}
	
	public void textLeading(String text, int x, int y) {
		FontMetrics metric = g.getFontMetrics(g.getFont());
		text(text, x, y + metric.getAscent() - metric.getDescent() - metric.getLeading());
	}
	public void textLeading(String text, float x, float y) {
		FontMetrics metric = g.getFontMetrics(g.getFont());
		text(text, x, y + metric.getAscent() - metric.getDescent() - metric.getLeading());
	}
	
	public void textLeading(String text, float x, float y, int size) {
		FontMetrics metric = g.getFontMetrics(g.getFont());
		text(text, x, y + metric.getAscent() - metric.getDescent() - metric.getLeading(), size);
	}
	public void textLeading(String text, int x, int y, int size) {
		FontMetrics metric = g.getFontMetrics(g.getFont());
		text(text, x, y + metric.getAscent() - metric.getDescent() - metric.getLeading(), size);
	}
	
	// Font
	
	public void font(Font font) {
		g.setFont(font);
	}
	
	public void font(String font) {
		Font current = g.getFont();
		g.setFont(new Font(font, current.getStyle(), current.getSize()));
	}
	
	public void font(String font, int size) {
		Font current = g.getFont();
		g.setFont(new Font(font, current.getStyle(), size));
	}
	
	public void font(String font, int style, int size) {
		g.setFont(new Font(font, style, size));
	}
	
	// Mode
	
	public void fill() {
		fill = true;
	}
	public void draw() {
		fill = false;
	}
	
	public void stroke(int width) {
		g.setStroke(new BasicStroke(width));
	}
	public void stroke(float width) {
		g.setStroke(new BasicStroke(width));
	}
	public void nostroke() {
		g.setStroke(NOSTROKE);
	}
	
	// Misc
	
	public void push() {
		graphic_stack.push(g);
		g = (Graphics2D) g.create();
	}
	public void pop() {
		Graphics2D gdisp = g;
		g = graphic_stack.pop();
		gdisp.dispose();
	}
	
	public void push_matrix() {
		double[] mat = new double[6];
		AffineTransform at = g.getTransform();
		at.getMatrix(mat);
		at.setToIdentity();
		g.setTransform(at);
		matrix_stack.push(mat);
	}
	public void pop_matrix() {
		double[] mat = matrix_stack.pop();
		AffineTransform at = g.getTransform();
		at.setTransform(mat[0], mat[1], mat[2], mat[3], mat[4], mat[5]);
		g.setTransform(at);
	}
	
	public void translate(int x, int y) {
		g.translate(x, y);
	}
	public void translate(float x, float y) {
		g.translate((double)x, (double)y);
	}
	
	// Library links
	
	public Graphics2D g2d() {
		return g;
	}
	
	public GFrame gframe() {
		return gFrame;
	}
	
	public JFrame jframe() {
		return gFrame.getInternal_frame();
	}
	
	public Input.MouseInput mouse() {
		return Input.mouse;
	}
	
	public Input.KeyboardInput keyboard() {
		return Input.keyboard;
	}
	
	// Idk
	
	public int width() {
		return gFrame.width();
	}
	
	public int height() {
		return gFrame.height();
	}
	
	// C to I
	
	public BufferedImage toImage() {
		BufferedImage i = new BufferedImage(gFrame.getDrawCanvas().getSize().width, gFrame.getDrawCanvas().getSize().height, BufferedImage.TYPE_INT_RGB);
		
		Graphics2D imageGraphics = i.createGraphics();
		
		imageGraphics.setColor(Color.GRAY);
		imageGraphics.fillRect(0, 0, 100, 100);
		gFrame.getDrawCanvas().paint(imageGraphics);
		imageGraphics.dispose();
		
		return i;
	}
}

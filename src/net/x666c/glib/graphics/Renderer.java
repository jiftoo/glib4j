package net.x666c.glib.graphics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

import net.x666c.glib.GFrame;
import net.x666c.glib.input.Input;
import net.x666c.glib.math.phys.Collider;
import net.x666c.glib.math.vector.Line;
import net.x666c.glib.math.vector.Point;
import net.x666c.glib.math.vector.Vector;
import net.x666c.glib.util.FixedStack;

public final class Renderer {
	
	private static final BasicStroke NOSTROKE = new BasicStroke(1);
	
	private Graphics2D g;
	private final GFrame gFrame;
	
	private final FixedStack<Graphics2D> graphic_stack;
	private final FixedStack<double[]> matrix_stack;
	
	private boolean fill = false; // Mode
	
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
		final Color cur = g.getColor();
		color(cur.getRed(), cur.getGreen(), cur.getBlue(), (int)(a * 255));
	}
	
	/**
	 * @param int in 0 - 255 range
	 */
	public void alpha(int a) {
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
		g.drawRect(x, y, 1, 1);
	}
	
	public void point(float x, float y) {
		g.drawRect((int)x, (int)y, 1, 1);
	}
	public void point(Point p) {
		g.drawRect((int)p.x, (int)p.y, 1, 1);
	}
	public void point(Vector v) {
		g.drawRect((int)v.x, (int)v.y, 1, 1);
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
	
	// Image
	
	public void image(BufferedImage image, float x, float y, float width, float height) {
		g.drawImage(image, (int)x, (int)y, (int)width, (int)height, null);
	}
	public void image(BufferedImage image, int x, int y, int width, int height) {
		g.drawImage(image, x, y, width, height, null);
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
}

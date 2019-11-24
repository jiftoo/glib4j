package io.x666c.glib4j.graphics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

import io.x666c.glib4j.GDisplay;
import io.x666c.glib4j.GFrame;

public class DisplayRenderer extends Renderer {
	
	private final GDisplay display;
	
	public DisplayRenderer(Graphics2D g, GDisplay display) {
		super(g, null);
		this.display = display;
	}
	
	@Override
	public GFrame gframe() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public JFrame jframe() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public int width() {
		return super.width();
	}
	
	@Override
	public int height() {
		return super.height();
	}
	
	@Override
	public BufferedImage toImage() {
		BufferedImage i = new BufferedImage(display.getSize().width, display.getSize().height, BufferedImage.TYPE_INT_RGB);
		
		Graphics2D imageGraphics = i.createGraphics();
		
		imageGraphics.setColor(Color.GRAY);
		imageGraphics.fillRect(0, 0, 100, 100);
		display.paint(imageGraphics);
		imageGraphics.dispose();
		
		return i;
	}
	
}
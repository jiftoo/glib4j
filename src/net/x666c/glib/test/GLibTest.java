package net.x666c.glib.test;

import java.awt.Color;

import net.x666c.glib.GFrame;
import net.x666c.glib.components.GButton;
import net.x666c.glib.graphics.Renderer;

public class GLibTest {

	public static void main(String[] args) {
		
		GFrame gFrame = new GFrame(60, 1, GLibTest::render, GLibTest::update);
		gFrame.setSize(800, 800);
		
//		gFrame.addSlider("test slider", 0, 100, 0, i -> System.out.println("Slider value: " + i));
//		gFrame.addButtons(new GButton(() -> System.out.println("button pressed")), new GButton("Button 2", null), new GButton("button 3", Color.RED, null));
		
		gFrame.start();
	}
	
	private static int i = 0;
	
	private static void render(Renderer g) {
		g.color(255, 0, 0);
		g.fill();
		
		g.text("Hello", 0, 10, i);
		
		g.push_matrix();
		{
			g.translate(50, 50);
			g.square(0, 0, 100);
		}
		g.pop_matrix();
		
		g.color(0, 0, 0);
		g.draw();
		g.square(i, 0, 100);
		i += 1;
	}
	
	private static void update() {
		System.out.println("Once per second");
	}
}

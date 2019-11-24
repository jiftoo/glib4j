package io.x666c.glib4j.test;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import io.x666c.glib4j.GFrame;
import io.x666c.glib4j.components.GCustomPane;
import io.x666c.glib4j.components.GTextField;
import io.x666c.glib4j.graphics.Renderer;
import io.x666c.glib4j.input.Input;

public class GLibTest {
	
	public static void main(String[] args) throws Exception {
		GFrame gFrame = new GFrame(60, 60, GLibTest::render, GLibTest::update);
		gFrame.setSize(600, 600);
//		gFrame.addSlider("test slider", 0, 100, 0, i -> System.out.println("Slider value: " + i));
//		gFrame.addButtons(new GButton(() -> System.out.println("button pressed")), new GButton("Button 2", null), new GButton("button 3", Color.RED, null));
		gFrame.addField(new GTextField("GField", s -> System.out.println(s)));
		
		GCustomPane cp = new GCustomPane(100, (g, c) -> {
			g.setColor(c.getClickableArea("btn").pressed() ? Color.RED : Color.GREEN);
			g.drawRect(10, 10, 100, 100);
		});
		cp.addClickableArea("btn", 10, 10, 10, 10, new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				System.out.println("released!");
			};
		});
		
		gFrame.addCustomPane(cp);
		
		gFrame.start();
		gFrame.addShutdownRoutine(() -> System.out.println("Shutdown!"));
		//System.exit(0);
	}
	
	private static int i = 0;
	
	private static void render(Renderer g) {
		g.color(255, 0, 0);
		g.fill();
		
		g.text("Hello", 0, 10);
		
		g.push_matrix();
		{
			g.translate(50, 50);
			g.square(0, 0, 100);
		}
		g.pop_matrix();
		
		g.color(0, 0, 0);
		g.draw();
		g.square(i, 0, 100);
	}
	
	private static void update() {
		if(Input.mouse.mouse4())
			System.out.println("m4");
		if(Input.mouse.mouse5())
			System.out.println("m5");
		
		if(Input.keyboard.key('d')) {
			i += 1;
		}
		
	}
}

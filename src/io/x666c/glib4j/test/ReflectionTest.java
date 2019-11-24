package io.x666c.glib4j.test;

import io.x666c.glib4j.GFrame;
import io.x666c.glib4j.graphics.Renderer;

public class ReflectionTest {

	public static void main(String[] args) {
		new ReflectionTest();
	}
	
	public ReflectionTest() {
		GFrame g = GFrame.auto(this);
		g.start();
	}

	private void render(Renderer r) {
		System.out.println("render");
	}

	private void update() {
		System.out.println("update");
	}

}
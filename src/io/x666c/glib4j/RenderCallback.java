package io.x666c.glib4j;

import io.x666c.glib4j.graphics.Renderer;

public interface RenderCallback {
	
	public void render(Renderer g);
	
	// Null render callback
	public static final RenderCallback NOP = (r) -> {};
	
}
package io.x666c.glib4j;

import io.x666c.glib4j.graphics.Renderer;

public interface GApp {
	
	public void render(Renderer r) throws Exception;
	
	public void update() throws Exception;

}

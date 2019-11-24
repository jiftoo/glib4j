package io.x666c.glib4j;

public interface UpdateCallback {
	
	public void update();
	
	// Null update callback
	public static final UpdateCallback NOP = () -> {};
	
}

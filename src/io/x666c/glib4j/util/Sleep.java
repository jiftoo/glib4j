package io.x666c.glib4j.util;

public final class Sleep {
	
	private Sleep() {
	}
	
	public static final void during(long millis) {
		try {
			Thread.sleep(millis);
		} catch (Exception e) {}
	}
	
	public static final void during(long millis, int nanos) {
		try {
			Thread.sleep(millis, nanos);
		} catch (Exception e) {}
	}
}

package io.x666c.glib4j.time;

import java.time.Instant;
import java.time.temporal.ChronoField;
import java.util.Calendar;

public final class Time {
	
	public static final int nanosecond() {
		return Instant.now().get(ChronoField.NANO_OF_SECOND);
	}
	
	public static final int millisecond() {
		return Calendar.getInstance().get(Calendar.MILLISECOND);
	}
	
	public static final int second() {
		return Calendar.getInstance().get(Calendar.SECOND);
	}
	
	public static final int minute() {
		return Calendar.getInstance().get(Calendar.MINUTE);
	}
	
	public static final int hour() {
		return Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
	}
	
	public static final int day() {
		return Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
	}
	
	public static final int month() {
		return Calendar.getInstance().get(Calendar.MONTH);
	}
	
	public static final int year() {
		return Calendar.getInstance().get(Calendar.YEAR);
	}
}

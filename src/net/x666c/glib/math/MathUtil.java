package net.x666c.glib.math;

import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class MathUtil {
	
	private static final Random pRandom = new Random();
	
	public static final void randomSeed(long seedValue) {
		pRandom.setSeed(seedValue);
	}
	
	
	public static final float map(float value, float istart, float istop, float ostart, float ostop) {
		return ostart + (ostop - ostart) * ((value - istart) / (istop - istart));
	}
	
	public static float clamp(float val, float min, float max) {
		return Math.min(Math.max(val, min), max);
	}
	
	public static float gaussian() {
		return (float) pRandom.nextGaussian();
	}
	
	public static float random() {
		return (float) pRandom.nextDouble();
	}
	
	public static int random(int bound) {
		return pRandom.nextInt(bound);
	}
	
	public static int random(int origin, int bound) {
		return pRandom.nextInt((bound - origin) + 1) + origin;
	}
	
	public static float random(float bound) {
		return pRandom.nextFloat() * bound;
	}
	
	public static float random(float origin, float bound) {
		return (origin + (bound - origin) * pRandom.nextFloat());
	}
}
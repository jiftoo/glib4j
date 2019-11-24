package io.x666c.glib4j.math;

import java.util.Random;
import java.util.function.Supplier;

public class MathUtil {
	
	private static final Random pRandom = new Random();
	private static long seed;
	static {
		seed = pRandom.nextLong();
		pRandom.setSeed(seed);
	}
	
	public static final void randomSeed(long seedValue) {
		pRandom.setSeed(seedValue);
		seed = seedValue;
	}
	public static final long getSeed() {
		return seed;
	}
	
	
	public static final float map(float value, float istart, float istop, float ostart, float ostop) {
		return ostart + (ostop - ostart) * ((value - istart) / (istop - istart));
	}
	
	public static float clamp(float val, float min, float max) {
		return Math.min(Math.max(val, min), max);
	}
	
	public synchronized static float gaussian() {
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
	
	public static Object selectO(Supplier<?>... constructor) {
		return constructor[random(constructor.length)].get();
	}
	
	@SafeVarargs
	public static <T> T select(Supplier<T>... constructor) {
		return constructor[random(constructor.length)].get();
	}
}
package io.x666c.glib4j.math;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
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
	
	public static <T> T select(T[] constructor) {
		return constructor[random(constructor.length)];
	}
	
	public static int select(int[] constructor) {
		return constructor[random(constructor.length)];
	}
	
	public static int[] randomArray(int origin, int bound, int count) {
		final int[] set = new int[count];
		for (int i = 0; i < count; i++) {
			set[i] = random(origin, bound);
		}
		return set;
	}
	
	public static int[] randomSet(int origin, int bound, int count) {
		if(origin > bound || (bound - origin + 1) < count) {
			throw new RuntimeException("Invalid arguments");
		}
		
		final HashSet<Integer> set = new HashSet<Integer>();
		while(set.size() < count) {
			set.add(random(origin, bound));
		}
		
		int[] ret = new int[count];
		Integer[] setInt = new Integer[count];
		set.toArray(setInt);
		Collections.shuffle(Arrays.asList(setInt));
		for (int i = 0; i < count; i++) {
			ret[i] = setInt[i];
		}
		
		return ret;
	}
	
	public static float[] randomSet(float origin, float bound, int count) {
		if(origin > bound || (bound - origin + 1) < count) {
			throw new RuntimeException("Invalid arguments");
		}
		
		final HashSet<Float> set = new HashSet<Float>();
		while(set.size() < count) {
			set.add(random(origin, bound));
		}
		
		float[] ret = new float[count];
		Float[] setInt = new Float[count];
		set.toArray(setInt);
		Collections.shuffle(Arrays.asList(setInt));
		for (int i = 0; i < count; i++) {
			ret[i] = setInt[i];
		}
		
		return ret;
	}
}
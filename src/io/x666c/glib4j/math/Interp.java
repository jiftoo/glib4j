package io.x666c.glib4j.math;

public enum Interp {
	Linear, Hermite, Quintic;
	
	public static enum FractalType {
		FBM, Billow, RigidMulti
	}

	public static enum CellularDistanceFunction {
		Euclidean, Manhattan, Natural
	}

	public static enum CellularReturnType {
		CellValue, NoiseLookup, Distance, Distance2, Distance2Add, Distance2Sub, Distance2Mul, Distance2Div
	}

}


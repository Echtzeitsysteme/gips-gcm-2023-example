package org.gips.examples.incrementalp2p.common;

public final class TimeAggregator {
	
	private static double ilpTime = 0;
	private static double gtTime = 0;
	
	private TimeAggregator() {}
	
	public static void reset() {
		ilpTime = 0;
		gtTime = 0;
	}
	
	public static void addToIlpTime(final double t) {
		checkNonNegative(t);
		ilpTime += t;
	}
	
	public static void addToGtTime(final double t) {
		checkNonNegative(t);
		gtTime += t;
	}
	
	private static void checkNonNegative(final double t) {
		if (t < 0) {
			throw new UnsupportedOperationException("Time increment was negative.");
		}
	}
	
	public static double getIlpTime() {
		return ilpTime / 1_000_000;
	}
	
	public static double getGtTime() {
		return gtTime / 1_000_000;
	}

}

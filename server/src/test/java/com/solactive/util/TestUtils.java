package com.solactive.util;

import java.util.Random;

import com.solactive.model.Tick;

public class TestUtils {
	
	private static double leftLimit = 1D;
    private static double rightLimit = 100000D;

	public static Tick getTickRequest() {
		Tick tick = new Tick();
		tick.setInstrument("ins-"+new Random().nextInt(10000));
		double generatedDouble = leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
		tick.setPrice(generatedDouble);
		tick.setTimestamp(System.currentTimeMillis());
		return tick;
	}
	
	public static Tick getTickMinRequest() {
		Tick tick = new Tick();
		tick.setInstrument("min-1");
		tick.setPrice(10.0);
		tick.setTimestamp(System.currentTimeMillis());
		return tick;
	}
	
	public static Tick getTickMaxRequest() {
		Tick tick = new Tick();
		tick.setInstrument("max-1");
		tick.setPrice(50.0);
		tick.setTimestamp(System.currentTimeMillis());
		return tick;
	}
}

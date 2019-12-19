package com.solactive.util;

import java.util.concurrent.atomic.AtomicLong;
import com.google.common.util.concurrent.AtomicDouble;

import lombok.Data;

@Data
public class Statistics {
	
	private AtomicDouble min = new AtomicDouble(Double.POSITIVE_INFINITY);
	private AtomicDouble max = new AtomicDouble(Double.NEGATIVE_INFINITY);
	private AtomicDouble avg = new AtomicDouble(0.0);
	private AtomicDouble sum = new AtomicDouble(0.0);
	private AtomicLong count = new AtomicLong(0);
	
	public Statistics() {
		
	}
	
	public Statistics(double newValue) {
		updateStats(newValue);
	}
	
	public void updateStats(double newValue) {
		count.incrementAndGet();
		min.set(Math.min(min.doubleValue(),newValue));
		max.set(Math.max(max.doubleValue(),newValue));
		sum.addAndGet(newValue);
		avg.set(sum.get()/count.get());
	}
	
	public void updateStats(Statistics stats) {
		count.incrementAndGet();
		min.set(Math.min(min.doubleValue(),stats.getMin().get()));
		max.set(Math.max(max.doubleValue(),stats.getMax().get()));
		sum.addAndGet(stats.getSum().get());
		avg.set(sum.get()/count.get());
	}
 
}

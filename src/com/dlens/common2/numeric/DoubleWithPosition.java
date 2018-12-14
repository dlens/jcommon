package com.dlens.common2.numeric;

public class DoubleWithPosition implements Comparable<DoubleWithPosition> {
	public int pos;
	public double value;
	
	public DoubleWithPosition(int pos, double value) {
		this.pos=pos;
		this.value=value;
	}
	
	/**
	 * This is a reverse comparison
	 */
	@Override
	public int compareTo(DoubleWithPosition arg0) {
		if (arg0==null) {
			return -1;
		}
		return Double.compare(arg0.value, value);
	}

	@Override
	public String toString() {
		return "pos="+pos+" value="+value;
	}
}

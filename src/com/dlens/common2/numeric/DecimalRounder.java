package com.dlens.common2.numeric;

public class DecimalRounder {
	private int precision;
	public DecimalRounder(int precision) {
		this.precision=precision;
	}
	
	public double round(double value) {
		if (precision==0) {
			return value;
		} else {
			double tenPower=tenPower();
			long newVersion = (long) (value * tenPower);
			return newVersion / tenPower;
		}
	}
	
	public double tenPower() {
		return Math.pow(10, precision);
	}
}

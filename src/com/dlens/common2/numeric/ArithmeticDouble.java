package com.dlens.common2.numeric;

import java.math.BigDecimal;

public class ArithmeticDouble implements Arithmetic<Double>{
	private Double val;
	public ArithmeticDouble(double val) {
		this.val=val;
	}
	@Override
	public Double add(Double a) {
		return val + a;
	}

	@Override
	public Double subtract(Double a) {
		return val-a;
	}

	@Override
	public Double multiply(Double a) {
		return val*a;
	}

	@Override
	public Double divide(Double a) {
		return val/a;
	}

	@Override
	public double doubleValue() {
		return val;
	}

	@Override
	public int intValue() {
		return val.intValue();
	}

	@Override
	public BigDecimal bigDecimalValue() {
		return new BigDecimal(val);
	}
	@Override
	public Double add(double a) {
		return val+a;
	}
	
	@Override
	public Double subtract(double a) {
		return val-a;
	}
	@Override
	public Double mulitply(double a) {
		return val*a;
	}
	@Override
	public Double divide(double a) {
		return val/a;
	}

}

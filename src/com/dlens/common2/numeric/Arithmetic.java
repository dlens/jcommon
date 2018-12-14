package com.dlens.common2.numeric;

import java.math.BigDecimal;

public interface Arithmetic<E extends Number> {
	public E add(E a);
	public E subtract(E a);
	public E multiply(E a);
	public E divide(E a);
	public E add(double a);
	public E subtract(double a);
	public E mulitply(double a);
	public E divide(double a);
	public double doubleValue();
	public int intValue();
	public BigDecimal bigDecimalValue();
}

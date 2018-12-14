package com.dlens.common2.numeric;

public interface RealFunctionDiffererntiableCoeffs {
	public double[] coeffsPartial(double evalAtX, double coeffs[]);
	public double eval(double evalAtX, double coeffs[]);
}

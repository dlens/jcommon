package com.dlens.common2.numeric;

public class PolynomialFunction implements RealFunction {
	private double[] coeffs;

	public PolynomialFunction(double coeffs[]) {
		if (coeffs==null) {
			coeffs = new double[0];
		}
		this.coeffs = coeffs;
	}
	
	@Override
	public double eval(double x) {
		double rval=0;
		double xpow=1;
		for(int i=0; i < coeffs.length; i++) {
			rval+=coeffs[i] * xpow;
			xpow *= x;
		}
		return rval;
	}
	
	@Override
	public String toString() {
		String rval = "";
		int deg=coeffs.length-1;
		if (deg < 0) {
			//The zero function
			return "0";
		}
		rval = "";
		String plusSym="";
		double coeff;
		String var;
		for(int i=deg; i >= 0; i--) {
			coeff=Math.abs(coeffs[i]);
			if (i==1) {
				var = "x";
			} else if (i==0) {
				var = "";
			} else {
				var = "x^"+i;
			}
			if (i == deg) {
				if (coeffs[i] < 0) {
					plusSym=" -";
				}
			} else {
				if (coeffs[i] < 0) {
					plusSym=" - ";
				} else {
					plusSym=" + ";
				}
			}
			if (coeffs[i] != 0)
				rval+=plusSym+coeff+var;
		}
		return rval;
	}
}

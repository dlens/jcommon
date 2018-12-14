package com.dlens.common2.numeric;

public class LinLinRationalFx implements RealFunctionDiffererntiableCoeffs, RealFunction {
	private double origCoeffs[];
	
	public LinLinRationalFx() {
		origCoeffs = new double[] {1, 1, 1};
	}
	public LinLinRationalFx(double origCoeffs[]) {
		if ((origCoeffs==null)||(origCoeffs.length!=3))
			throw new IllegalArgumentException();
		this.origCoeffs=origCoeffs;
	}
	
	@Override
	public double[] coeffsPartial(double x, double[] coeffs) {
		double[] rval = new double[3];
		double denom = coeffs[0]*x + coeffs[1];
		rval[0]=-x/(denom*denom);
		rval[1]=-1/(denom*denom);
		rval[2]=1;
		return rval;
	}

	@Override
	public double eval(double x, double[] coeffs) {
		return 1/(coeffs[0]*x+coeffs[1])+coeffs[2];
	}

	@Override
	public double eval(double x) {
		return eval(x, origCoeffs);
	}

	@Override
	public String toString() {
		return "1/("+origCoeffs[0]+"x + "+origCoeffs[1]+") + "+origCoeffs[2];
	}
}

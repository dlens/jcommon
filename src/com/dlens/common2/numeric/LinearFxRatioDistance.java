package com.dlens.common2.numeric;

public class LinearFxRatioDistance implements
		RealFunctionDiffererntiableCoeffs, RealFunction {
	private double m;
	private double b;
	private double[] defaultCoeffs;
	private double NON_POSITIVE_VALUE=1e5;
	private double[] xValues;
	private double[] yValues;
	
	public LinearFxRatioDistance(double slope, double yIntercept, double xValues[], double yValues[]) {
		m = slope;
		b = yIntercept;
		this.xValues=xValues;
		this.yValues=yValues;
		defaultCoeffs = new double[] {b, m};
	}
	
	@Override
	public double eval(double x) {
		return eval(x, defaultCoeffs);
	}

	@Override
	public double[] coeffsPartial(double posDouble, double[] coeffs) {
		int pos = (int) Math.round(posDouble);
		double evalAtX = xValues[pos];
		double e = coeffs[1]*evalAtX + coeffs[0];
		double partialM, partialB;
		if (e <= 0) {
			return coeffsPartialNonpositive(evalAtX, coeffs);
		} else {
			double v = yValues[pos];
			if (v > e) {
				partialB = -v/(e*e) * (1);
				partialM = -v/(e*e) * (evalAtX);
			} else if (v < e) {
				partialB = (1) / v;
				partialM = (evalAtX) / v;
			} else {
				partialB = 0;
				partialM = 0;
			}
			return new double[] {partialB, partialM};
		}
	}

	private double[] coeffsPartialNonpositive(double evalAtX, double[] coeffs) {
		return new double[] {-1, 0};
	}

	@Override
	public double eval(double posDouble, double coeffs[]) {
		int pos = (int) Math.round(posDouble);
		double evalAtX = xValues[pos];
		double e = coeffs[1]*evalAtX + coeffs[0];
		if (e <= 0) {
			return evalNonpositive(evalAtX, coeffs);
		} else {
			double v = yValues[pos];
//			System.out.println("Eval DoublePos="+posDouble+" IntPos="+pos+" e="+e+" v="+v);
			if (v > e) {
				return v/e - 1;
			} else if (v < e) {
				return e/v - 1;
			} else {
				return 0;
			}
		}
	}

	private double evalNonpositive(double evalAtX, double[] coeffs) {
		return NON_POSITIVE_VALUE*evalAtX;
	}

}

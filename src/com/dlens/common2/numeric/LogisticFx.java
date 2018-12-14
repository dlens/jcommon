package com.dlens.common2.numeric;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class LogisticFx implements RealFunctionDiffererntiableCoeffs, RealFunction {
	private double[] origCoeffs;
	public LogisticFx() {
		origCoeffs=new double[] {1, 1, 1, 1};
	}
	
	public LogisticFx(double[] origCoeffs) {
		if ((origCoeffs==null)||(origCoeffs.length!=4))
			throw new IllegalArgumentException();
		this.origCoeffs=origCoeffs;
	}

	@Override
	public double[] coeffsPartial(double evalAtX, double[] coeffs) {
		double rval[] = new double[4];
		double ecx = Math.exp(-coeffs[2]*evalAtX);
		double denom = 1 + coeffs[1]*ecx;
		rval[0]=1/denom;
		rval[1]=-coeffs[0]*ecx / (denom*denom);
		rval[2]=coeffs[0]*coeffs[1]*evalAtX*ecx / (denom*denom);
		rval[3]=1.0;
		return rval;
	}

	@Override
	public double eval(double evalAtX, double[] coeffs) {
		return coeffs[0]/(1+coeffs[1]*Math.exp(-coeffs[2]*evalAtX))+coeffs[3];
	}

	@Override
	public double eval(double x) {
		return eval(x, origCoeffs);
	}

	@Override
	public String toString() {
		double coeffs[]=origCoeffs;
		String ourForm = coeffs[0]+"/(1 + "+coeffs[1]+" * exp("+(-coeffs[2])+" x)) + "+coeffs[3];
		double infl = Math.log(Math.abs(coeffs[1]))/(-coeffs[2]);
		String stdForm =coeffs[0]+"/(1 + exp("+(-coeffs[2])+" (x + "+infl+"))) + "+coeffs[3];
		return "OurForm: "+ourForm+"\n"+"StdForm: "+stdForm;
	}
}

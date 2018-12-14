
package com.dlens.common2.numeric.sets;

/**
 * Represents a polynomial fx f:R \to R
 * @author Bill Adams
 */
public class DOneToOnePolyFx extends NToOneFx<Double> {
    double coeffs[];

    private void init(double coeffs[]) {
        if (coeffs==null)
            throw new IllegalArgumentException("coeffs can't be null");
        this.coeffs=new double[coeffs.length];
        System.arraycopy(coeffs, 0, this.coeffs, 0, coeffs.length);
    }

    public DOneToOnePolyFx(double coeffs[]) {
        super(1);
        init(coeffs);
    }

    public DOneToOnePolyFx(ExistentialSet<Double>dom, ExistentialSet<Double> ran,
            double[] coeffs) {
        super(1, dom, ran);
        init(coeffs);
    }

    @Override
    protected Double internalEval(Double[] pt) {
        double rval=0;
        double x=pt[0];
        double xPow=1;
        for(int deg=0; deg < coeffs.length; deg++) {
            rval+=xPow*coeffs[deg];
            xPow*=x;
        }
        return rval;
    }
}

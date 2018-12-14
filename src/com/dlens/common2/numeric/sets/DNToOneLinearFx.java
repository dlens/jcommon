
package com.dlens.common2.numeric.sets;

/**
 * Represents a f:R^n \to R
 * @author Bill Adams
 */
public class DNToOneLinearFx extends NToOneFx<Double> {
    double coeffs[];
    double constant=0;

    public DNToOneLinearFx(int dim, double coeffs[], double constant) {
        super(dim);
        init(dim, coeffs, constant);
    }

    public DNToOneLinearFx(int dim, ExistentialSet<Double> dom,
            ExistentialSet<Double> ran, double coeffs[], double constant) {
        super(dim, dom, ran);
        init(dim, coeffs, constant);
    }
    
    private void init(int dim, double coeffs[], double constant) throws IllegalArgumentException {
        if (coeffs==null) throw new IllegalArgumentException("coeffs can not be null.");
        if (coeffs.length!=dim)
            throw new IllegalArgumentException("Coeffs length must equal dim.");
        this.coeffs=new double[dim];
        System.arraycopy(coeffs, 0, this.coeffs, 0, dim);
        this.constant=constant;
    }

    @Override
    protected Double internalEval(Double[] pt) {
        if (pt==null) throw new IllegalArgumentException("pt cannot be null.");
        if (pt.length!=coeffs.length)
            throw new IllegalArgumentException("point should have same dimension as fx.");
        double rval=constant;
        for(int i=0; i<coeffs.length; i++) {
            rval+=coeffs[i]*pt[i];
        }
        return rval;
    }

}


package com.dlens.common2.numeric.sets;

/**
 * This represents a simple one input, one output linear fx.
 * @author Bill Adams
 */
public class DOneToOneLinearFx extends NToOneFx<Double>{
    double m;
    double b;

    public DOneToOneLinearFx(double m, double b) {
        super(1);
        this.m=m;
        this.b=b;
    }

    public DOneToOneLinearFx(double m, double b, ExistentialSet<Double> domain, ExistentialSet<Double> range)
    {
        super(1, domain, range);
        this.m=m;
        this.b=b;
    }

    @Override
    protected Double internalEval(Double[] pt) {
        if (pt==null) throw new IllegalArgumentException("pt cannot be null.");
        if (pt.length>1) throw new IllegalArgumentException("pt must be 1 dim.");
        return m*pt[0]+b;
    }

}

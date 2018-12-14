
package com.dlens.common2.numeric.sets;

/**
 * Represents an interval in the set E, which must implement Comparable.
 * An upper bound of null means no upper bound.  Likewise for lowerBound.
 * @author Bill Adams
 */
public class IntervalExSet<E extends Comparable> extends ExistentialSet<E> {
    private E lowerBound;
    private boolean lowerIsStrict=false;
    private boolean upperIsStrict=false;
    private E upperBound;
    public IntervalExSet(E lowerBound, E upperBound) {
        super(1);
        if ((lowerBound!=null) && (upperBound!=null) && (upperBound.compareTo(lowerBound)<0))
            throw new IllegalArgumentException("Upper bound must not be smaller than lower bound.");
        this.lowerBound=lowerBound;
        this.upperBound=upperBound;
    }

    public IntervalExSet(E lowerBound, E upperBound, boolean lowerIsStrict, boolean upperIsStrict) {
        this(lowerBound, upperBound);
        this.lowerIsStrict=lowerIsStrict;
        this.upperIsStrict=upperIsStrict;
    }
    
    @Override
    public boolean contains(E[] pt) throws IllegalArgumentException {
        if (pt==null) throw new IllegalArgumentException("null point not allowed.");
        if (pt.length!=1) throw new IllegalArgumentException("point needed dimension 1, but had dimension "+pt.length);
        return contains(pt[0]);
    }

    @Override
    public boolean contains(E oneDPt) throws IllegalArgumentException {
        int lcomparison, ucomparison;
        if ((lowerBound==null)||((lcomparison=lowerBound.compareTo(oneDPt))<0)) {
           //Bigger than lower
            if ((upperBound==null)||((ucomparison=upperBound.compareTo(oneDPt))>0)) {
                //Smaller than upper
                return true;
            } else {
                //Not smaller than upper, could be equal to
                if (ucomparison==0) {
                    //It was equal, now are we allowing equality?
                    if (upperIsStrict) {
                        //Not allowing equality
                        return false;
                    } else {
                        //Allowing equality
                        return true;
                    }
                } else {
                    //Wasn't equal, so outside of upperBound, bye
                    return false;
                }
            }
        } else {
            //Not smaller than lower, could be equal to
            if (lcomparison==0) {
                //Was equal to lower bound, do we allow this?
                if (lowerIsStrict) {
                    //Do not allow equal to lower bound
                    return false;
                } else {
                    //Do allow equal to lower bound
                    return true;
                }
            } else {
                //Was less than lower bound
                return false;
            }
        }
    }

}

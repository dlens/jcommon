
package com.dlens.common2.numeric.sets;

/**
 * Represent the product of n IntervalExSets
 * @author Bill Adams
 */
public class NIntervalExSet<E extends Comparable> extends ExistentialSet<E>{
    IntervalExSet<E>[] ranges;
    public NIntervalExSet(int dim, E lowerBounds[], E upperBounds[]) {
        super(dim);
        if (lowerBounds==null) throw new IllegalArgumentException("lowerBounds cannot be null.");
        if (upperBounds==null) throw new IllegalArgumentException("upperBounds cannot be null.");
        if (lowerBounds.length!=upperBounds.length) throw new IllegalArgumentException("upper and lower bounds dims have to match.");
        if (lowerBounds.length!=dim) throw new IllegalArgumentException("Must upper and lower bounds must match dim.");
        ranges=new IntervalExSet[dim];
        for(int i=0; i<dim; i++) {
            ranges[i]=new IntervalExSet<E>(lowerBounds[i], upperBounds[i]);
        }
    }

    @Override
    public boolean contains(E[] pt) throws IllegalArgumentException {
        if (pt==null) throw new IllegalArgumentException("pt cannot be null.");
        if (pt.length!=this.ranges.length) throw new IllegalArgumentException("Pt dimension did not match set's dimension.");
        for(int i=0; i<pt.length; i++) {
            if (!ranges[i].contains((E[])(new Comparable[] {pt[i]}))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean contains(E oneDPt) throws IllegalArgumentException {
        if (ranges.length==1)
            return ranges[0].contains(oneDPt);
        else
            throw new UnsupportedOperationException("This set cannot contain a point.");
    }


}

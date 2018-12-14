
package com.dlens.common2.numeric.sets;

/**
 * Represents a fx E^n -> E
 * @author Bill Adams
 */
abstract public class NToOneFx<E> {
    ExistentialSet<E> domain;
    ExistentialSet<E> range;
    int dimension;
    public NToOneFx(int dim) {
        this.dimension=dim;
    }

    public NToOneFx(int dim, ExistentialSet<E> domain, ExistentialSet<E> range) {
        this(dim);
        if ((domain!=null)&&(domain.getDimension()!=dim))
            throw new IllegalArgumentException("Domain's dim doesn't match our dim.");
        if ((range!=null)&&(range.getDimension()!=1))
            throw new IllegalArgumentException("Range's dim isn't 1");
        this.domain=domain;
        this.range=range;
    }

    public E evaluate(E[] pt) {
        if (pt==null)
            throw new IllegalArgumentException("pt cannot be null.");
        if (pt.length!=dimension)
            throw new IllegalArgumentException("pt is of wrong dimension.");
        if ((domain!=null)&&(!domain.contains(pt))) {
            throw new IllegalArgumentException("Point is outside the domain.");
        }
        E rval=internalEval(pt);
        if (rval==null)
            throw new IllegalArgumentException("Rval of null, it is bad.");
        if ((range!=null)&&(!range.contains(rval))) {
            throw new IllegalArgumentException("Rval outside of range.");
        }
        return rval;
    }

    abstract protected E internalEval(E[] pt);
}

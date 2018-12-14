
package com.dlens.common2.numeric.sets;

/**
 * An existential set of type E represents a subset of E^n.  In particular
 * it represents such a set, in the sense that you can test if points are in
 * that set.  The only constraint being if p \in E^n, A is an existential
 * set then A.contains(p) should always return the same value (unless, of course,
 * A has changed between invocations).
 * @author Bill Adams
 */
abstract public class ExistentialSet<E> {
    private int dimension;
    public ExistentialSet(int dim) {
        if (dim < 0)
            throw new IllegalArgumentException("Dimension must be non-negative.");
        this.dimension=dim;
    }

    public int getDimension() {
        return dimension;
    }

    abstract public boolean contains(E pt[]) throws IllegalArgumentException;

    abstract public boolean contains(E oneDPt) throws IllegalArgumentException;
}

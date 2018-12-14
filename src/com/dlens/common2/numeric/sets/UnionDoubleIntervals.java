
package com.dlens.common2.numeric.sets;

import java.util.Vector;

/**
 * Represents a union of double intervals.  Each double interval is guaranteed
 * to have a gap between it and the next one (the initializer guarantees this).
 * @author Bill Adams
 */
public class UnionDoubleIntervals {
    private Vector<DoubleIntervalSet> intervals=new Vector<DoubleIntervalSet>();

    public UnionDoubleIntervals(Vector<DoubleIntervalSet> vals) {
        if (vals==null) throw new IllegalArgumentException("Cannot have null.");
        intervals=DoubleIntervalSet.strictlyIncreasing(vals);
    }

    public UnionDoubleIntervals(DoubleIntervalSet vals[]) {
        if (vals==null) throw new IllegalArgumentException("Cannot have null.");
        intervals=DoubleIntervalSet.strictlyIncreasing(vals);
    }

    public UnionDoubleIntervals(DoubleIntervalSet a) {
        if (a==null) throw new IllegalArgumentException();
        intervals.add(a);
    }

    public Vector<DoubleIntervalSet> getIntervals() {
        return new Vector<DoubleIntervalSet>(intervals);
    }

    public DoubleIntervalSet toDoubleIntervalApprox() {
    	return DoubleIntervalSet.join(intervals);
    }

    @Override
    public String toString() {
        switch (intervals.size()) {
            case 0: return "Empty";
            case 1: return intervals.get(0).toString();
            default:
                String rval=intervals.get(0).toString();
                for(int i=1; i<intervals.size(); i++) {
                    rval+=" U "+intervals.get(i).toString();
                }
                return rval;
        }
    }

    public static UnionDoubleIntervals sum(UnionDoubleIntervals a, UnionDoubleIntervals b) {
    	Vector<DoubleIntervalSet> as=a.getIntervals();
    	Vector<DoubleIntervalSet> bs=b.getIntervals();
    	Vector<DoubleIntervalSet> parts=new Vector<DoubleIntervalSet>();
    	for(int i=0; i<as.size(); i++) {
    		for(int j=0; j<bs.size(); j++) {
    			DoubleIntervalSet.addToKeepStrictlyIncreasing(parts, DoubleIntervalSet.sum(as.get(i), bs.get(j)));
    		}
    	}
    	return new UnionDoubleIntervals(parts);
    }
    
    public static UnionDoubleIntervals sum(Vector<UnionDoubleIntervals> sets) {
    	if (sets==null) return null;
    	return sum(sets.toArray(new UnionDoubleIntervals[0]));
    }

    public static UnionDoubleIntervals sum(UnionDoubleIntervals sets[]) {
    	if ((sets==null)||(sets.length==0)) return null;
    	if (sets.length==1) return sets[0];
    	UnionDoubleIntervals rval=sum(sets[0], sets[1]);
    	for(int i=2; i< sets.length; i++) {
    		rval=sum(rval, sets[i]);
    	}
    	return rval;
    }
    
    @Override
    public boolean equals(Object obj) {
    	if (obj==null) return false;
    	if (obj.getClass().equals(UnionDoubleIntervals.class)) {
    		UnionDoubleIntervals other=(UnionDoubleIntervals)obj;
    		return intervals.equals(other.intervals);
    	} else if (obj.getClass().equals(DoubleIntervalSet.class)) {
    		DoubleIntervalSet other=(DoubleIntervalSet)obj;
    		if (intervals.size()!=1) {
    			return false;
    		} else {
    			return intervals.get(0).equals(other);
    		}
    	} else if (obj.getClass().equals(DoubleIntervalSetAndPts.class)) {
    		//FIXME
    		return false;
    	} else {
    		return false;
    	}
    }

    public boolean intersects(UnionDoubleIntervals other) {
    	DoubleIntervalSet a;
    	for(int i=0; i<intervals.size(); i++) {
    		a=intervals.get(i);
    		for(int j=0; j<other.intervals.size(); j++) {
    			if (a.intersects(other.intervals.get(j))) {
    				return true;
    			}
    		}
    	}
    	return false;
    }
}

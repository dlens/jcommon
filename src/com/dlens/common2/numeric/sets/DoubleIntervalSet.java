
package com.dlens.common2.numeric.sets;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Vector;

import com.dlens.common2.numeric.RealBoundedObject;
import com.dlens.common2.numeric.RealBoundedSet;


/**
 * Represents an interval of doubles.  It may have no lower bound, or no upper
 * bound (by setting lower bound to negative infinity or upper bound to positive
 * infinity respectively).
 * @author Bill Adams
 */
public class DoubleIntervalSet implements Comparable<DoubleIntervalSet>, RealBoundedSet, Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private double lowerBound=Double.NEGATIVE_INFINITY;
    private double upperBound=Double.POSITIVE_INFINITY;
    private boolean lowerHasEquals=true;
    private boolean upperHasEquals=true;
    private static DoubleIntervalSet EmptySet;


    public DoubleIntervalSet(double lowerBound, double upperBound) {
        this(lowerBound, upperBound, true, true);
    }

    public DoubleIntervalSet(DoubleIntervalSet src) {
	    	if (src==null) {
    		return;
    	}
    	lowerBound=src.lowerBound;
    	upperBound=src.upperBound;
    	lowerHasEquals=src.lowerHasEquals;
    	upperHasEquals=src.upperHasEquals;
    }
    
    public DoubleIntervalSet shift(double shiftBy) {
    	DoubleIntervalSet rval=new DoubleIntervalSet(this);
    	rval.lowerBound+=shiftBy;
    	rval.upperBound+=shiftBy;
    	return rval;
    }
    
    public DoubleIntervalSet(double lowerBound, double upperBound, boolean lowerHasEquals, boolean upperHasEquals) {
        if (!legalBounds(lowerBound, upperBound, lowerHasEquals, upperHasEquals)) {
            lowerBound=upperBound=0;
            lowerHasEquals=upperHasEquals=false;
        }
        this.lowerBound=lowerBound;
        this.upperBound=upperBound;
        this.lowerHasEquals=lowerHasEquals;
        this.upperHasEquals=upperHasEquals;
    }

    public static boolean legalBounds(double lower, double upper, boolean lowerEquals, boolean upperEquals) {
        if (lowerEquals && upperEquals) {
            return lower <= upper;
        } else {
            return lower < upper;
        }
    }

    public boolean isCompletelyUnbounded() {
        return (lowerBound==Double.NEGATIVE_INFINITY && upperBound==Double.POSITIVE_INFINITY);
    }

    public boolean isUnbounded() {
        return (lowerBound==Double.NEGATIVE_INFINITY || upperBound==Double.POSITIVE_INFINITY);
    }

    public double getLowerBound() {
        return lowerBound;
    }

    public double getUpperBound() {
        return upperBound;
    }

    public boolean getLowerHasEquals() {
        return lowerHasEquals;
    }

    public boolean getUpperHasEquals() {
        return upperHasEquals;
    }

    @Override
    public int compareTo(DoubleIntervalSet o) {
        //Just compares lower bounds and equality allowed
        if (o==null) return -1;
        if (lowerBound < o.lowerBound) {
            return -1;
        } else if (lowerBound == o.lowerBound) {
            if (lowerHasEquals) {
                if (o.lowerHasEquals) {
                    return 0;
                } else {
                    return -1;
                }
            } else {
                if (!o.lowerHasEquals) {
                    return 0;
                } else {
                    return 1;
                }
            }
        } else {
            return 1;
        }
    }

    public boolean strictlyLess(DoubleIntervalSet di) {
        if (di==null) return false;
        if (upperBound < di.lowerBound) {
            return true;
        } else {
            return false;
        }
    }

    public static DoubleIntervalSet join(DoubleIntervalSet a, DoubleIntervalSet b) {
    	if (a==null) a=DoubleIntervalSet.emptySet();
    	if (b==null) b=DoubleIntervalSet.emptySet();
    	if (a.isEmpty()) {
    		return new DoubleIntervalSet(b);
    	}
    	if (b.isEmpty()) {
    		return new DoubleIntervalSet(a);
    	}
        double lowerBound=Math.min(a.lowerBound, b.lowerBound);
        double upperBound=Math.max(a.upperBound, b.upperBound);
        boolean lowerHasEquals=false, upperHasEquals=false;
        if (lowerBound==a.lowerBound) {
            if (a.lowerHasEquals) {
                lowerHasEquals=true;
            }
        }
        if (lowerBound==b.lowerBound) {
            if (b.lowerHasEquals) {
                lowerHasEquals=true;
            }
        }
        if (upperBound==a.upperBound) {
            if (a.upperHasEquals) {
                upperHasEquals=true;
            }
        }
        if (upperBound==b.upperBound) {
            if (b.upperHasEquals) {
                upperHasEquals=true;
            }
        }
        return new DoubleIntervalSet(lowerBound, upperBound, lowerHasEquals, upperHasEquals);
    }

    public static DoubleIntervalSet join(Vector<DoubleIntervalSet> vec) {
    	if (vec==null) return DoubleIntervalSet.emptySet();
    	switch (vec.size()) {
    	case 0:
    		return DoubleIntervalSet.emptySet();
    	case 1:
    		return vec.get(0);
    	default:
    		DoubleIntervalSet rval=vec.get(0);
    		for(int i=1; i<vec.size(); i++) {
    			rval=DoubleIntervalSet.join(rval, vec.get(i));
    		}
    		return rval;
    	}
    }
    
    public static Vector<DoubleIntervalSet> strictlyIncreasing(DoubleIntervalSet vec[]) {
    	Vector<DoubleIntervalSet> set=new Vector<DoubleIntervalSet>();
    	for(int i=0; i<vec.length; i++)
    		set.add(vec[i]);
    	return strictlyIncreasing(set);
    }
    
    public static Vector<DoubleIntervalSet> strictlyIncreasing(Vector<DoubleIntervalSet> vec) {
        if (vec==null) return null;
        if (vec.size()==0) return new Vector<DoubleIntervalSet>(0);
        //First we need to sort, which will order them by lowest starting point
        Vector<DoubleIntervalSet>work1=new Vector<DoubleIntervalSet>(vec);
        Collections.sort(work1);
        //Now we need to coalesce the ones needing that.
        Vector<DoubleIntervalSet>rval=new Vector<DoubleIntervalSet>();
        DoubleIntervalSet tmp, last;
        last=work1.get(0);
        for(int i=0; i<work1.size(); i++) {
            tmp=work1.get(i);
            if (DoubleIntervalSet.unionIsConnected(tmp, last)) {
                last=DoubleIntervalSet.join(last, tmp);
            } else {
            	rval.add(last);
                last=tmp;
            }
        }
        rval.add(last);
        return rval;
    }

    public static void addToKeepStrictlyIncreasing(Vector<DoubleIntervalSet> strictlyIncreasingVec, DoubleIntervalSet aset) {
    	if (strictlyIncreasingVec==null) throw new IllegalArgumentException();
    	strictlyIncreasingVec.add(aset);
    	Collections.sort(strictlyIncreasingVec);
    	int index=strictlyIncreasingVec.indexOf(aset);
    	int prevIndex=index-1;
    	int nextIndex=index+1;
    	if (prevIndex >= 0) {
    		//Check for overlap below, and fix if needed
    		DoubleIntervalSet prev=strictlyIncreasingVec.get(prevIndex);
    		if (DoubleIntervalSet.unionIsConnected(prev, aset)) {
    			strictlyIncreasingVec.remove(index);
    			strictlyIncreasingVec.set(prevIndex, aset=DoubleIntervalSet.join(prev, aset));
    			nextIndex-=1;
    		}
    	}
    	if (nextIndex < strictlyIncreasingVec.size()) {
    		DoubleIntervalSet next=strictlyIncreasingVec.get(nextIndex);
    		if (DoubleIntervalSet.unionIsConnected(next, aset)) {
    			strictlyIncreasingVec.remove(nextIndex-1);
    			aset=DoubleIntervalSet.join(aset, next);
    			strictlyIncreasingVec.set(nextIndex-1, aset);
    		}
    	}
    }
    
//    public DoubleIntervalSet closureIntersectionRmme(DoubleIntervalSet dv) {
//        if (dv==null) dv=DoubleIntervalSet.emptySet();
//        double newLower=Math.max(dv.lowerBound, lowerBound);
//        double newUpper=Math.min(dv.upperBound, upperBound);
//        boolean newLowerHasEquals=true;
//        boolean newUpperHasEquals=true;
//        if (newLower==dv.lowerBound)
//            newLowerHasEquals = newLowerHasEquals && dv.lowerHasEquals;
//        if (newLower==lowerBound)
//            newLowerHasEquals = newLowerHasEquals && lowerHasEquals;
//        if (newUpper==dv.upperBound)
//            newUpperHasEquals = newUpperHasEquals && dv.upperHasEquals;
//        if (newUpper==upperBound)
//            newUpperHasEquals = newUpperHasEquals && upperHasEquals;
//        if ((newUpper < newLower) || ((newUpper == newLower)&&((!newUpperHasEquals) || (!newLowerHasEquals)))) {
//            //Null set, make sure it is correctly setup
//            newUpper=newLower=0;
//            newUpperHasEquals=newLowerHasEquals=false;
//        }
//        return new DoubleIntervalSet(newLower, newUpper, newLowerHasEquals, newUpperHasEquals);
//    }

//    public boolean closureIntersectsRmme(DoubleIntervalSet dv) {
//        return !closureIntersection(dv).isEmpty();
//    }

    public static boolean unionIsConnected(DoubleIntervalSet a, DoubleIntervalSet b) {
    	if (a.lowerBound > b.lowerBound) {
    		DoubleIntervalSet tmp=a;
    		a=b;
    		b=tmp;
    	}
    	//Now we know a's lower is <= b's.
    	if (a.upperBound > b.lowerBound) {
    		//they intersect.
    		return true;
    	} else if (a.upperBound==b.lowerBound) {
    		return (a.upperHasEquals || b.lowerHasEquals);
    	} else {
    		return false;
    	}
    }
    
    public boolean isEmpty() {
        if (lowerBound > upperBound) {
            return true;
        } else if (lowerBound == upperBound) {
            if (upperHasEquals && lowerHasEquals) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }
    public static DoubleIntervalSet emptySet() {
        return new DoubleIntervalSet(0, 0, false, false);
    }

    @Override
    public boolean equals(Object o) {
        if (o==null) return false;
        if (DoubleIntervalSet.class.isAssignableFrom(o.getClass())) {
            DoubleIntervalSet other=(DoubleIntervalSet)o;
            return ((lowerBound==other.lowerBound)&&(upperBound==other.upperBound) &&
                    (lowerHasEquals==other.lowerHasEquals)&&(upperHasEquals==other.upperHasEquals));
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        if (isEmpty()) {
            return "Empty";
        } else {
            String lb, ub;
            if (lowerHasEquals) {
                lb="[";
            } else {
                lb="(";
            }
            if (upperHasEquals) {
                ub="]";
            } else {
                ub=")";
            }
            return lb+lowerBound+", "+upperBound+ub;
        }
    }

    public boolean contains(double p) {
    	if (p < upperBound) {
    		if (p > lowerBound) {
    			return true;
    		} else if (p == lowerBound) {
    			return lowerHasEquals;
    		} else {
    			return false;
    		}
    	} else if (p == upperBound) {
    		return upperHasEquals;
    	} else {
    		return false;
    	}
    }

    public static DoubleIntervalSet sum(DoubleIntervalSet a, DoubleIntervalSet b) {
    	Vector<DoubleIntervalSet>as=new Vector<DoubleIntervalSet>();
    	as.add(a);
    	as.add(b);
    	return sum(as);
    }
    
    private static DoubleIntervalSet sumOld(Vector<DoubleIntervalSet> sets) {
    	double lowerValue;
    	double upperValue;
    	boolean lowerHasEquals=true;
    	boolean upperHasEquals=true;
    	if (sets==null) return null;
    	if (sets.size()==0) return null;
    	DoubleIntervalSet first=sets.get(0);
    	lowerValue=first.getLowerBound();
    	upperValue=first.getUpperBound();
    	lowerHasEquals &= first.lowerHasEquals;
    	upperHasEquals &= first.upperHasEquals;
    	for(int i=1; i<sets.size(); i++) {
    		DoubleIntervalSet next=sets.get(i);
    		lowerValue+=next.lowerBound;
    		upperValue+=next.upperBound;
    		lowerHasEquals&=next.lowerHasEquals;
    		upperHasEquals&=next.upperHasEquals;
    	}
    	return new DoubleIntervalSet(lowerValue, upperValue, lowerHasEquals, upperHasEquals);
    }
    
    public static DoubleIntervalSet sum(Vector<DoubleIntervalSet> sets) {
    	if (sets==null) return null;
    	return sum(sets.toArray(new DoubleIntervalSet[sets.size()]));
    }
    
    public static DoubleIntervalSet  sum(DoubleIntervalSet[] sets) {
    	double lowerValue;
    	double upperValue;
    	boolean lowerHasEquals=true;
    	boolean upperHasEquals=true;
    	if (sets==null) return null;
    	if (sets.length==0) return null;
    	DoubleIntervalSet first=sets[0];
    	lowerValue=first.getLowerBound();
    	upperValue=first.getUpperBound();
    	lowerHasEquals &= first.lowerHasEquals;
    	upperHasEquals &= first.upperHasEquals;
    	for(int i=1; i<sets.length; i++) {
    		DoubleIntervalSet next=sets[i];
    		lowerValue+=next.lowerBound;
    		upperValue+=next.upperBound;
    		lowerHasEquals&=next.lowerHasEquals;
    		upperHasEquals&=next.upperHasEquals;
    	}
    	return new DoubleIntervalSet(lowerValue, upperValue, lowerHasEquals, upperHasEquals);    	
    }
    
    public DoubleIntervalSet intersect(DoubleIntervalSet other) {
    	if (other==null) return DoubleIntervalSet.emptySet();
    	double newLower=Math.max(lowerBound, other.lowerBound);
    	double newUpper=Math.min(upperBound, other.upperBound);
    	boolean newLowerHasEquals;
    	boolean newUpperHasEquals;
    	if (newLower==lowerBound) {
    		if (newLower==other.lowerBound) {
    			newLowerHasEquals=lowerHasEquals&&other.lowerHasEquals;
    		} else {
    			newLowerHasEquals=lowerHasEquals;
    		}
    	} else {
    		newLowerHasEquals=other.lowerHasEquals;
    	}
    	if (newUpper==upperBound) {
    		if (newUpper==other.upperBound) {
    			newUpperHasEquals=upperHasEquals&&other.upperHasEquals;
    		} else {
    			newUpperHasEquals=upperHasEquals;
    		}
    	} else {
    		newUpperHasEquals=other.upperHasEquals;
    	}
    	return new DoubleIntervalSet(newLower, newUpper, newLowerHasEquals, newUpperHasEquals);
    }
    
    public boolean intersects(DoubleIntervalSet other) {
    	DoubleIntervalSet inter=this.intersect(other);
    	return !inter.isEmpty();
    }

    public boolean hasUpperBound() {
    	if (Double.isInfinite(upperBound) || Double.isNaN(upperBound)) return false;
    	return true;
    }
    
    public boolean hasLowerBound() {
    	if (Double.isInfinite(lowerBound) || Double.isNaN(lowerBound)) return false;
    	return true;
    }
    
    public boolean hasBothBounds() {
    	return hasUpperBound() && hasLowerBound();
    }
	
	public boolean isPoint() {
		if (!hasBothBounds()) return false;
		if (!upperHasEquals) return false;
		if (!lowerHasEquals) return false;
		if (upperBound!=lowerBound) return false;
		return true;
	}

	public static DoubleIntervalSet intervalApprox(RealBoundedObject set) {
		if (set==null) return null;
		if (set.getClass().equals(DoubleIntervalSet.class)) return (DoubleIntervalSet)set;
		return new DoubleIntervalSet(set.getLowerBound(), set.getUpperBound(), true, true);
	}
	
	public DoubleIntervalSet scale(double scale) {
		DoubleIntervalSet rval=new DoubleIntervalSet(this);
		double l=rval.lowerBound*scale;
		double u=rval.upperBound*scale;
		if (scale >=0 ) {
			rval.lowerBound=l;
			rval.upperBound=u;
		} else {
			rval.lowerBound=u;
			rval.upperBound=l;
		}
		return rval;
	}
	
	public DoubleIntervalSet add(DoubleIntervalSet other) {
		DoubleIntervalSet rval=new DoubleIntervalSet(this);
		rval.lowerBound+=other.lowerBound;
		rval.upperBound+=other.upperBound;
		rval.lowerHasEquals=this.lowerHasEquals && other.lowerHasEquals;
		rval.upperHasEquals=this.upperHasEquals && other.upperHasEquals;
		return rval;
	}

	public static DoubleIntervalSet intersect(
			DoubleIntervalSet[][] sets) {
		if (sets==null)
			return null;
		if (sets.length==0) return null;
		DoubleIntervalSet rval=DoubleIntervalSet.allReals();
		for(int row=0; row < sets.length; row++) {
			if (sets[row]!=null) {
				for(int col=0; col < sets[row].length; col++) {
					if (sets[row][col]!=null)
						rval=rval.intersect(sets[row][col]);
				}
			}
		}
		return rval;
	}

	private static DoubleIntervalSet allReals() {
		DoubleIntervalSet rval=new DoubleIntervalSet(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, false, false);
		return rval;
	}

	public double closestPointTo(double point) {
		if (contains(point))
			return point;
		if (hasLowerBound()) {
			if (point < lowerBound) {
				return lowerBound;
			} else {
				return upperBound;
			}
		} else {
			return upperBound;
		}
	}
}

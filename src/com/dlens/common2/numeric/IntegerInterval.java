package com.dlens.common2.numeric;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class IntegerInterval implements Iterable<Integer>{
	private boolean hasUpper;
	private boolean hasLower;
	private boolean upperEquals=true;
	private boolean lowerEquals=true;
	private int upper;
	private int lower;
	
	public IntegerInterval(int lower, int upper, boolean hasLower, boolean hasUpper, boolean lowerEquals, boolean upperEquals) {
		if (hasLower && hasUpper) {
			if (lowerEquals && upperEquals) {
				if (lower > upper)
					throw new IllegalArgumentException("Lower must be less than or equal to upper.");				
			} else {
				if (lower >= upper)
					throw new IllegalArgumentException("Lower must be less than upper.");
			}
		}
		this.lower=lower;
		this.upper=upper;
		this.hasLower=hasLower;
		this.hasUpper=hasUpper;
		this.lowerEquals=lowerEquals;
		this.upperEquals=upperEquals;
	}
	
	public IntegerInterval(IntegerInterval src) {
		this.lower=src.lower;
		this.upper=src.upper;
		this.hasLower=src.hasLower;
		this.hasUpper=src.hasUpper;
		this.lowerEquals=src.lowerEquals;
		this.upperEquals=src.upperEquals;
	}
	public IntegerInterval(int lower, int upper) {
		this(lower, upper, true, true, true, true);
	}

	public IntegerInterval(ArrayList<Integer> i1Pieces) {
		if ((i1Pieces==null) || (i1Pieces.size()==0)) {
			makeEmpty();
			return;
		}
		hasLower=true; lowerEquals=true;
		hasUpper=true; upperEquals=true;
		lower=i1Pieces.get(0);
		upper=i1Pieces.get(i1Pieces.size()-1);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj==null)
			return false;
		else if (obj.getClass().equals(IntegerInterval.class)) {
			IntegerInterval other = (IntegerInterval) obj;
			return
					(lower==other.lower) &&
					(upper==other.upper) &&
					(hasLower==other.hasLower) &&
					(hasUpper==other.hasUpper) &&
					(lowerEquals==other.lowerEquals) &&
					(upperEquals==other.upperEquals);
		} else {
			return false;
		}
	}
	public void negative() {
		boolean tmp;
		tmp=this.hasLower;
		hasLower=hasUpper;
		hasUpper=tmp;
		int tmpI=this.lower;
		this.lower=-this.upper;
		this.upper=-tmpI;
		tmp=lowerEquals;
		lowerEquals=upperEquals;
		upperEquals=tmp;
	}
	
	public void add(int val) {
		this.upper+=val;
		this.lower+=val;
	}

	public boolean contains(int value) {
		if (hasUpper) {
			if (upperEquals) {
				if (value > upper) {
					return false;
				}
			} else {
				if (value >= upper) {
					return false;
				}
			}
		}
		if (hasLower) {
			if (lowerEquals) {
				if (value < lower) {
					return false;
				}
			} else {
				if (value <= lower) {
					return false;
				}
			}
		}
		return true;
	}

	public IntegerInterval intersect(IntegerInterval bounds) {
		boolean newHasUpper, newHasLower, newUpperEquals, newLowerEquals;
		int newUpper, newLower;
		newLower=Math.max(lower, bounds.lower);
		newUpper=Math.min(upper, bounds.upper);
		newHasUpper=hasUpper || bounds.hasUpper;
		newHasLower=hasLower || bounds.hasLower;
		//Figure out the upper/lower equality status
		if (newLower==lower) {
			if (newLower==bounds.lower) {
				//Lower equality is equal iff both
				newLowerEquals=lowerEquals && bounds.lowerEquals;
			} else {
				//Lower equality comes from this
				newLowerEquals=lowerEquals;
			}
		} else {
			//Okay newlower only comes from bounds
			newLowerEquals=bounds.lowerEquals;
		}
		if (newUpper==upper) {
			if (newUpper==bounds.upper) {
				newUpperEquals=upperEquals && bounds.upperEquals;
			} else {
				newUpperEquals=upperEquals;
			}
		} else {
			newUpperEquals=bounds.upperEquals;
		}
		if (newLower > newUpper) {
			return IntegerInterval.factoryEmpty();
		}
		return new IntegerInterval(newLower, newUpper, newHasLower, newHasUpper, newLowerEquals, newUpperEquals);
	}
	
	
	public static IntegerInterval union(IntegerInterval a, IntegerInterval b) {
		if (b == null) {
			return a;
		} else if (a == null) {
			return b;
		}
		if (a.isEmpty()) {
			return b;
		} else if (b.isEmpty()) {
			return a;
		}
		a = a.equivEquals();
		b = b.equivEquals();
		boolean hasUpper = a.hasUpper && b.hasUpper;
		boolean hasLower = a.hasLower && b.hasLower;
		int lb=0, ub=0;
		if (hasUpper)
			ub = Math.max(a.upper, b.upper);
		if (hasLower)
			lb = Math.min(a.lower, b.lower);
		
		return new IntegerInterval(lb, ub, hasLower, hasUpper, true, true);
	}
	
	public IntegerInterval equivEquals() {
		IntegerInterval rval = new IntegerInterval(this);
		if (rval.hasLower) {
			if (!rval.lowerEquals) {
				rval.lower += 1;
				rval.lowerEquals = true;
			}
		}
		if (rval.hasUpper) {
			if (!rval.upperEquals) {
				rval.upper -= 1;
				rval.upperEquals = true;
			}
		}
		return rval;
	}
	
	public static IntegerInterval factoryEmpty() {
		IntegerInterval rval = new IntegerInterval(0, 1);
		rval.makeEmpty();
		return rval;
	}

	private void makeEmpty() {
		// TODO Auto-generated method stub
		hasLower=true;
		hasUpper=true;
		lower=0;
		upper=0;
		lowerEquals=false;
		upperEquals=false;
	}

	public int getLower() {
		return lower;
	}
	
	public int getUpper() {
		return upper;
	}
	
	public boolean getHasLower() {
		return hasLower;
	}
	
	public boolean getHasUpper() {
		return hasUpper;
	}
	
	public boolean getLowerEquals() {
		return lowerEquals;
	}
	
	public boolean getUpperEquals() {
		return upperEquals;
	}

	public int[] toIntArray() {
		int rval[] = new int[6];
		rval[0] = lower;
		rval[1] = upper;
		if (hasLower)
			rval[2] = 1;
		if (hasUpper)
			rval[3] = 1;
		if (lowerEquals)
			rval[4] = 1;
		if (upperEquals)
			rval[5] = 1;
		return rval;
	}
	
	public static IntegerInterval factory(int array[]) {
		if (array==null)
			throw new IllegalArgumentException();
		if (array.length != 6)
			throw new IllegalArgumentException();
		int lower, upper;
		boolean hasLower=false, hasUpper=false, lowerEquals=false, upperEquals=false;
		lower=array[0];
		upper=array[1];
		if (array[2] > 0)
			hasLower=true;
		if (array[3] > 0)
			hasUpper=true;
		if (array[4] > 0)
			lowerEquals=true;
		if (array[5] > 0)
			upperEquals=true;
		return new IntegerInterval(lower, upper, hasLower, hasUpper, lowerEquals, upperEquals);
	}
	
	@Override
	public String toString() {
		String rval="";
		if (!hasLower) {
			rval+="(-infty, ";
		} else {
			if (lowerEquals) {
				rval+="["+lower+", ";
			} else {
				rval+="("+lower+", ";
			}
		}
		if (!hasUpper) {
			rval+="infty)";
		} else {
			if (hasUpper) {
				rval+=upper+"]";
			} else {
				rval+=upper+")";
			}
		}
		return rval;
	}
	
	public static IntegerInterval[] newIntervalsFromDiffConstraint(IntegerInterval i1, IntegerInterval i2, IntegerInterval restrict) {
		IntegerInterval i1new = new IntegerInterval(0, 1);
		IntegerInterval i2new = new IntegerInterval(0, 1);
		add(i2, restrict, i1new);
		i1new=i1new.intersect(i1);
		IntegerInterval negI1 = new IntegerInterval(i1);
		negI1.negative();
		add(negI1, restrict, i2new);
		i2new=i2new.intersect(i2);
		return new IntegerInterval[] {i1new, i2new};
	}
	
	public static IntegerInterval[] newIntervalsFromDiffConstraintNew(IntegerInterval i1, IntegerInterval i2, IntegerInterval restrict) {
		//First half first
		ArrayList<Integer> i1Pieces = new ArrayList<Integer>();
		for (Integer aInt : i1) {
			IntegerInterval tmp = new IntegerInterval(i2);
			tmp.negative();
			tmp.add(aInt);
			IntegerInterval sect = tmp.intersect(restrict);
			if (!sect.isEmpty()) {
				i1Pieces.add(aInt);
			}
		}
		IntegerInterval rval1=new IntegerInterval(i1Pieces);
		//Second helf next
		ArrayList<Integer> i2Pieces = new ArrayList<Integer>();
		for (Integer aInt : i2) {
			IntegerInterval tmp = new IntegerInterval(i1);
			tmp.add(-aInt);
			IntegerInterval sect = tmp.intersect(restrict);
			if (!sect.isEmpty()) {
				i2Pieces.add(aInt);
			}
		}
		IntegerInterval rval2=new IntegerInterval(i2Pieces);
		
		return new IntegerInterval[] {rval1, rval2};
	}
	
	public boolean isEmpty() {
		if (upper < lower) {
			return true;
		} else if ((upper == lower) && (!lowerEquals || !upperEquals)) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean isEverything() {
		return (!hasLower) && (!hasUpper);
	}

	public static void add(IntegerInterval i1, IntegerInterval i2, IntegerInterval result) {
		//First figure out lower bound
		if (!(i1.hasLower && i2.hasLower)) {
			//At least one summand has no lower, so result has no lower
			result.hasLower=false;
		} else {
			//Both summands have lowers
			result.hasLower=true;
			result.lower=i1.lower+i2.lower;
		}
		//Now figure out upper bounds
		if (!(i1.hasUpper && i2.hasUpper)) {
			//At least one summand has no upper, so result has no upper
			result.hasUpper=false;
		} else {
			//Both summands have upper bounds
			result.hasUpper=true;
			result.upper=i1.upper+i2.upper;
		}
	}

	@Override
	public Iterator<Integer> iterator() {
		if (!hasLower)
			throw new IllegalArgumentException("No lower bound, cannot iterate.");
		if (!hasUpper)
			throw new IllegalArgumentException("No upper bound, cannot iterate.");
		return new Iterator<Integer>() {
			int count=lower;
			int max=upper;
			@Override
			public boolean hasNext() {
				return count <= max;
			}

			@Override
			public Integer next() {
				int rval=count;
				count++;
				return rval;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	public int getEffectiveLower() {
		if (!hasLower)
			throw new UnsupportedOperationException("Cannot do this operation without a lower bound.");
		else if (lowerEquals)
			return lower;
		else
			return lower+1;
	}

	public int randomInInterval(Random rng) {
		if (isEmpty())
			throw new IllegalArgumentException("Cannot get something randomly from an empty interval");
		if (hasLower) {
			if (hasUpper) {
				int eUpper = getEffectiveUpper();
				int eLower = getEffectiveLower();
				return eLower+rng.nextInt((eUpper-eLower+1));
			} else {
				return Math.abs(rng.nextInt())+getEffectiveLower();
			}
		} else {
			if (hasUpper) {
				return getEffectiveUpper() - Math.abs(rng.nextInt());
			} else {
				return rng.nextInt();
			}
		}
	}

	private int getEffectiveUpper() {
		if (!hasUpper)
			throw new UnsupportedOperationException("Cannot do this without upper bound.");
		else if (upperEquals)
			return upper;
		else
			return upper-1;
	}

	public double distance(double value) {
		double equalsDiff = 1e-12;
		if (hasUpper) {
			if (upperEquals) {
				if (value > upper) {
					return value - upper;
				}
			} else {
				if (value >= upper) {
					if (value==upper) {
						return equalsDiff;
					} else {
						return value - upper;
					}
				}
			}
		}
		if (hasLower) {
			if (lowerEquals) {
				if (value < lower) {
					return lower - value;
				}
			} else {
				if (value <= lower) {
					if (value==lower) {
						return equalsDiff;
					} else {
						return lower - value;
					}
				}
			}
		}
		return 0;
	}
	
}

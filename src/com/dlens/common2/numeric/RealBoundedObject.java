package com.dlens.common2.numeric;

public interface RealBoundedObject {
	public abstract double getUpperBound();
	public abstract double getLowerBound();
	public abstract boolean hasLowerBound();
	public abstract boolean hasUpperBound();
}

package com.dlens.common2.numeric;

public interface RealBoundedSet extends RealBoundedObject {
	public boolean contains(double point);
	public boolean isEmpty();
	public boolean isPoint();
}

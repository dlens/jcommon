package com.dlens.common2.numeric.sets;

import java.util.Vector;

/**
 * Represents a double interval that also contains some singleton points.
 * @author Bill Adams
 *
 */
public class DoubleIntervalSetAndPts {
	DoubleIntervalSet theInterval;
	Vector<Double> thePoints=new Vector<Double>();
	
	private DoubleIntervalSetAndPts() {		
	}
	
	public DoubleIntervalSetAndPts(DoubleIntervalSet theInterval) {
		if (theInterval==null) throw new IllegalArgumentException();
		this.theInterval=theInterval;
	}
	
	public DoubleIntervalSetAndPts(DoubleIntervalSet theInterval, double extraPt) {
		this(theInterval);
		//Only add the extra point if it is not redundant
		if (!theInterval.contains(extraPt))
			thePoints.add(extraPt);
	}
	
	public double[] getExtraPoints() {
		double rval[]=new double[thePoints.size()];
		for(int i=0; i<rval.length; i++)
			rval[i]=thePoints.get(i);
		return rval;
	}
	
	public DoubleIntervalSet getSet() {
		return theInterval;
	}
}


package com.dlens.common2.numeric;
import java.util.Vector;

import org.apache.log4j.Logger;

/**
 *
 * @author Bill Adams
 */
public class GenericMath {
	//The logger
	private static Logger logger = Logger.getLogger(GenericMath.class);
    private static final double DEFAULT_NEARNESS=1e-7;
    /**
     * Checks the percent difference between correct and other
     * to be less than percent.  correct is the true value, used
     * for percent difference calcs. 
     * @param correct
     * @param other
     * @param percent
     * @return
     */
    public static boolean isNear(double correct, double other, double percent) {
        double diff=Math.abs(correct-other);
        if (correct==0) {
            if (diff < percent) {
                return true;
            } else {
                return false;
            }
        } else {
            double testPer=Math.abs(diff/correct);
            return (testPer < percent);
        }                
    }

    public static double ratioDistance(double correct, double other) {
        double diff=Math.abs(correct-other);
        if (correct==0) {
            return diff;
        } else {
            return diff/correct;
        }
    }
    
    /**
     * Checks the percent difference between correct and other
     * to be less than DEFAULT_NEARNESS.  correct is the true value, used
     * for percent difference calcs. 
     * 
     * @param correct
     * @param other
     * @return
     */
    public static boolean isNear(double correct, double other) {
        return isNear(correct, other, DEFAULT_NEARNESS);
    }
    
    /**
     * Checks the percent difference between correct and other
     * to be less than DEFAULT_NEARNESS for each element of the arrays.
     * The arrays must have the same size.
     * correct is the true value, used
     * for percent difference calcs. 
     * 
     * 
     * @param correct
     * @param other
     * @return
     */
    public static boolean isNear(double correct[], double other[]) {
        return isNear(correct, other, DEFAULT_NEARNESS);
    }
    
    /**
     * Checks the percent difference between correct and other
     * to be less than percent for each element of the arrays.
     * The arrays must have the same size.
     * correct is the true value, used
     * for percent difference calcs. 
     * 
     * @param a
     * @param b
     * @param percent
     * @return
     */
    public static boolean isNear(double a[], double b[], double percent) {
        if (a==null) {
            if (b==null) {
                return true;
            } else {
                return false;
            }
        } else {
            if (b==null) return false;
            if (a.length!=b.length) return false;
            for(int i=0; i<a.length; i++) {
                if (!isNear(a[i], b[i], percent)) {
                    return false;
                }
            }
            return true;
        }        
    }

    /**
     * Calculates the geometric average of vals weighted by weights.
     * @param vals The values to be geometrically averaged.
     * @param weights The weights to use in the geometric average.
     * @return The geometric average.
     * @throws Exception Thrown if vals and weights
     * don't hasdve the same dimensions.
     */
    public static double geometricAverage(double vals[], double weights[]) throws Exception {
        if ((vals==null)||(weights==null))
            throw new Exception();
        if (vals.length!=weights.length)
            throw new Exception("Needed vals.length="+vals.length+" weights.length="+weights.length);
        double rval=1;
        double sum=0;
        double weightFactor=1;
        for(int i=0; i<vals.length; i++) {
            if (vals[i]!=0) {
                rval*=Math.pow(Math.abs(vals[i]), Math.abs(weights[i]));
                sum+=Math.abs(weights[i]);
            }
        }
        if (sum!=0) rval=Math.pow(rval, 1.0/sum);
        return rval;
    }

    /**
     * Gets those positions from the vector, and then normalizes the return
     * val.
     * @param vec
     * @param places
     * @return
     */
    public static double[] getPlacesAndNormalize(double []vec, int places[]) {
        double rval[]=new double[places.length];
        double sum=0;
        for(int i=0; i<rval.length; i++) {
            rval[i]=vec[places[i]];
            sum+=Math.abs(rval[i]);
        }
        if (sum!=0) {
            for(int i=0; i<rval.length; i++) {
                rval[i]/=sum;
            }
        }
        return rval;
    }
    
    /**
     * Translates from Object[] to Vector, really simple, but useful.
     * @param a
     * @return
     */
    public static Vector arrayToVector(Object[]a) {
        if (a==null) return new Vector();
        Vector rval=new Vector();
        for(int i=0; i<a.length; i++) {
            rval.add(a[i]);
        }
        return rval;
    }
    
    /**
     * Translates from Object[] to Vector, really simple, but useful.
     * @param a
     * @return
     */
    public static Vector arrayToVectorReturnNull(Object[]a) {
        if (a==null) return null;
        Vector rval=new Vector();
        for(int i=0; i<a.length; i++) {
            rval.add(a[i]);
        }
        return rval;
    }
    
    public static double interpolate(double x0, double y0, double x1, double y1, double val) {
        if (x0==x1) {
            logger.info("Interpolating with x0=x1, just returning average of y vals.");
            return (y0+y1)/2;
        } else {
            double slope=(y1-y0)/(x1-x0);
            return slope*(val-x0)+y0;
        }
    }
    
    public static void normalize(double []vals) {
        double sum=0;
        for(int i=0; i<vals.length; i++)
            sum+=Math.abs(vals[i]);
        if (sum!=0)
            for(int i=0; i<vals.length; i++)
                vals[i]/=sum;        
    }
    
    public static void idealize(double []vals) {
        double max=0;
        for(int i=0; i<vals.length; i++)
            if (max < Math.abs(vals[i]))
                max=Math.abs(vals[i]);
        if (max!=0)
            for(int i=0; i<vals.length; i++)
                vals[i]/=max;        
    }
    
    public static void idealizeIfNeeded(double []vals) {
        double max=0;
        for(int i=0; i<vals.length; i++)
            if (max < Math.abs(vals[i]))
                max=Math.abs(vals[i]);
        if ((max!=0)&&(max > 1))
            for(int i=0; i<vals.length; i++)
                vals[i]/=max;
    }

    public static boolean isZero(double[][] mat) {
        if (mat==null) return true;
        for(int i=0; i<mat.length; i++) {
            if (mat[i]!=null) {
                for(int j=0; j<mat[i].length; j++) {
                    if (mat[i][j]!=0) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public static boolean isZero(double[] mat) {
        if (mat==null) return true;
        for(int i=0; i<mat.length; i++) {
        	if (mat[i]!=0)
        		return false;
        }
        return true;
    }

    public static void setAllEqualAndNormalize(double vec[]) {
    	if (vec==null) return;
    	int size=vec.length;
    	if (size==0) return;
    	for(int i=0; i<vec.length; i++)
    		vec[i]=1.0/size;    	
    }
    
    public static void perturbPlace(double vec[], int place, double newValue, NormalizerEnum howToNormalize)
    {
        if ((place < 0) || (place >= vec.length)) return;
        if (newValue==vec[place]) return;
        double oldValue=vec[place];
        vec[place]=newValue;
        switch(howToNormalize) {
            case Normalized:
                //Need to renormalize, get sums first
                double oldSum=0, newOthersSum, newSum=0, oldOthersSum, factor;
                for(int i=0; i<vec.length; i++) {
                    newSum+=Math.abs(vec[i]);
                }
                oldOthersSum=newSum-Math.abs(newValue);
                oldSum=oldOthersSum+Math.abs(oldValue);
                newOthersSum=oldSum-Math.abs(newValue);
                //Need the new sum to be the oldSum, and vec[place] is force to
                //newValue.  The rest needs to add up to X, where X+abs(newValue)=oldSum
                //i.e. X=oldSum-abs(newValue);  Now, the others used to add to
                //oldOthersSum, and we want them to add to X, so we need to multiply
                //by X/oldOthersSum.  Now X=newOthersSum, so our multiplication factor
                //is newOthersSum/oldOthersSum
                if (oldOthersSum!=0) {
                    factor=newOthersSum/oldOthersSum;
                    for(int i=0; i<vec.length; i++)
                        if (i!=place)
                            vec[i]*=factor;
                }
                break;
            case Idealized:
            case Raw:
            default:
                //Nothing to do
                return;
        }
    }

    public static boolean myEquals(Object o1, Object o2) {
        if (o1==o2) {
            return true;
        } else if (o1==null) {
            return false;
        } else {
            return o1.equals(o2);
        }
    }

	public static double getUpperBoundNoNaN(RealBoundedObject ob) throws RealBoundedObjectException {
		double rval=ob.getUpperBound();
		if (Double.isNaN(rval))
			throw new RealBoundedObjectException("Unable to find upper bound.");
		return rval;
	}
	
	public static double getLowerBoundNoNaN(RealBoundedObject ob) throws RealBoundedObjectException {
		double rval=ob.getLowerBound();
		if (Double.isNaN(rval))
			throw new RealBoundedObjectException("Unable to find lower bound.");
		return rval;
	}
	
	public static double getUpperBoundFinite(RealBoundedObject ob) throws RealBoundedObjectException {
		double rval=ob.getUpperBound();
		if (Double.isNaN(rval))
			throw new RealBoundedObjectException("Unable to find upper bound.");
		else if (Double.isInfinite(rval))
			throw new RealBoundedObjectException("Upper bound was infinite.");
		return rval;
	}
	
	public static double getLowerBoundFinite(RealBoundedObject ob) throws RealBoundedObjectException {
		double rval=ob.getLowerBound();
		if (Double.isNaN(rval))
			throw new RealBoundedObjectException("Unable to find lower bound.");
		else if (Double.isInfinite(rval))
			throw new RealBoundedObjectException("Lower bound was infinite.");
		return rval;
	}

	public static void scaleSecondCoords(Vector<double[]> pts, double scale) {
		if (pts==null) return;
		double []pt;
		for(int i=0; i<pts.size(); i++) {
			pt=pts.get(i);
			if ((pt==null)||(pt.length!=2)) throw new IllegalArgumentException("pts must be double[2]");
			pt[1]*=scale;
		}
	}
    

}

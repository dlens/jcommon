
package com.dlens.common2.numeric;

import com.dlens.common2.exceptions.XMLFormatException;
import com.dlens.common2.interfaces.JDOMable;
import com.dlens.common2.interfaces.JDOMit;
import org.jdom.Element;

/**
 *
 * @author Bill Adams
 */
public class DoubleInterval implements JDOMable, Comparable<DoubleInterval> {
    private double start;
    private double end;
    
    public DoubleInterval(double start, double end) throws Exception {
        if (end < start)
            throw new Exception("End cannot be less than start");
        this.start=start;
        this.end=end;
    }

    public DoubleInterval() {
        start=0;
        end=0;        
    }
    
    public DoubleInterval(Element elt) throws XMLFormatException {
        fromElement(elt);
    }
    
    public double getStart() {
        return start;
    }

    public double getEnd() {
        return end;
    }

    @Override
    public boolean equals(Object arg0) {
        if (arg0==null) return false;
        if (arg0.getClass().equals(DoubleInterval.class)) {
            DoubleInterval other=(DoubleInterval)arg0;
            return ((start==other.start)&&(end==other.end));
        }
        return false;
    }

    
    public Element toElement(String name) {
        Element rval=new Element(name);
        rval.setAttribute("start", start+"");
        rval.setAttribute("end", end+"");
        return rval;
    }

    public void fromElement(Element elt) throws XMLFormatException {
        double tstart;
        double tend;
        tstart=JDOMit.getDouble(elt, "start");
        tend=JDOMit.getDouble(elt, "end");
        start=tstart;
        end=tend;
    }

    public int compareTo(DoubleInterval o) {
        //Just check if the start value is lower
        if (start < o.start) {
            return -1;
        } else if (start > o.start) {
            return 1;
        } else if (end < o.end) {
            return -1;
        } else if (end > o.end) {
            return 1;
        } else {
            return 0;
        }
    }
    
    /**Compares this interval to a double value.  If that value is contained
     * in the interval, 0 is returned.  If value is outside the interval on the
     * high side, -1 is returned, and if the low side 1 is returned.
     * @param val
     * @return
     */
    public int compareTo(double val) {
        if (val < start) {
            return 1;
        } else if (val > end) {
            return -1;
        } else {
            return 0;
        }
    }
    
    public boolean contains(double val) {
        return ((val <=end) && (val >=start));
    }
    
    /**Gets the first interval in the list containing this value.
     * It assumes the intervals are already ordered.
     * @param ints
     * @param val
     * @return
     */
    public static DoubleInterval firstIntervalContaining(DoubleInterval[] ints, double val) {
        if (ints==null) return null;
        for(int i=0; i<ints.length; i++) {
            if (ints[i].contains(val)) {
                return ints[i];
            }
        }
        //None contained return null;
        return null;
    }
    
    /**
     * Gets the intervals bounding a value.  If it is higher than the highest it returns
     * {highest, null} if it is lower than the lowest it returns {null, lowest}.  If there
     * were no intervals, {null, null} is returned.  We assume the intervals are already
     * ordered.  If one of the intervals actually contains the value, then we return
     * {intervalContainingThatVal}, i.e. it only has length 1.  Notice that we
     * simply use the intervals sent to us to create the return value, so if
     * those are actually a subclass, the return value can be safely cast to
     * Subclass[].
     */
    public static DoubleInterval[] getIntervalsBounding(DoubleInterval[] ints, double val) {
        DoubleInterval[] rval=new DoubleInterval[2];
        if ((ints==null)||(ints.length==0))
            return rval;
        int firstBigger=ints.length;
        for(int i=0; i<ints.length; i++) {
            if (ints[i].compareTo(val)>-1) {
                //The interval now either contains or is bigger.
                firstBigger=i;
            }
        }
        //Okay now handle two extremes
        if (firstBigger==(ints.length)) {
            //There was none bigger, so return {lastInterval, null}
            rval[0]=ints[ints.length-1];
            rval[1]=null;
        } else if (firstBigger==0) {
            //We were below the first one, so return {null, firstInterval}
            rval[0]=null;
            rval[1]=ints[0];
        } else {
            //We were somewhere in between, we need to see if the first bigger
            //actually contained.
            if (ints[firstBigger].contains(val)) {
                return new DoubleInterval[] {ints[firstBigger]};
            } else {
                //We are between firstBigger-1 and firstBigger
                rval[0]=ints[firstBigger-1];
                rval[1]=ints[firstBigger];
            }
        }
        return rval;
    }

    @Override
    public String toString() {
        return "["+start+", "+end+"]";
    }
    
    
}

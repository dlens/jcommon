
package com.dlens.common2.numeric;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.dlens.common2.exceptions.XMLFormatException;
import com.dlens.common2.interfaces.JDOMable;
import org.jdom.Element;

/**
 *
 * @author Bill Adams
 */
public class DoubleIntervalWithObject<E> extends DoubleInterval {
    E object;
    public DoubleIntervalWithObject(double start, double end, E val) throws Exception {
        super(start,end);
        if (val==null) throw new Exception("Need non-null parameter.");
        object=val;
    }

    public DoubleIntervalWithObject(DoubleInterval di, E val) throws Exception {
        super(di.getStart(),di.getEnd());
        if (val==null) throw new Exception("Need non-null parameter.");
        object=val;
    }

    public E getObject() {
        return object;
    }

    public void setObject(E object) {
        this.object = object;
    }

    @Override
    public Element toElement(String name) {
        Element rval=super.toElement(name);
        try {
        	JDOMable jdom = (JDOMable)this;
        	rval.addContent(jdom.toElement("object"));
        } catch (ClassCastException e) {
        	throw new UnsupportedOperationException("Object not jdomable");
        }
        return rval;
    }

    @Override
    public void fromElement(Element elt) throws XMLFormatException {
        super.fromElement(elt);
        Element sub=elt.getChild("object");
        try {
        	JDOMable jdom = (JDOMable)this;
        	jdom.fromElement(sub);
        } catch (ClassCastException e) {
        	throw new UnsupportedOperationException("Not jdomable object.");
        }
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
    public static DoubleIntervalWithObject[] getIntervalsBounding(DoubleIntervalWithObject[] ints, double val) {
        DoubleIntervalWithObject[] rval=new DoubleIntervalWithObject[2];
        if ((ints==null)||(ints.length==0))
            return rval;
        int firstBigger=ints.length;
        for(int i=0; i<ints.length; i++) {
            if (ints[i].compareTo(val)>-1) {
                //The interval now either contains or is bigger.
                firstBigger=i;
                if (ints[i].compareTo(val)==0) {
                    return new DoubleIntervalWithObject[] {ints[i]};
                }
                break;
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
                return new DoubleIntervalWithObject[] {ints[firstBigger]};
            } else {
                //We are between firstBigger-1 and firstBigger
                rval[0]=ints[firstBigger-1];
                rval[1]=ints[firstBigger];
            }
        }
        return rval;
    }
    

}

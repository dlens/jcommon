
package com.dlens.common2.numeric;

import com.dlens.common2.exceptions.XMLFormatException;
import org.jdom.Element;

/**
 *
 * @author Bill Adams
 */
public enum NormalizerEnum {
    Idealized,
    Normalized,
    Raw;
    public int toUniqueInt() {
        switch (this) {
            case Idealized: return 0;
            case Normalized: return 1;
            case Raw: return 2;
            default: return -1;
        }
    }
    
    public static NormalizerEnum factory(int uniqueInt) throws Exception {
        switch (uniqueInt) {
            case 0: return Idealized;
            case 1: return Normalized;
            case 2: return Raw;
            default: throw new Exception("Unknown vote type.");
        }
    }
    
    public static NormalizerEnum factory(Element elt, String attributeName) throws XMLFormatException
    {
        String sInt=elt.getAttributeValue(attributeName);
        if ((sInt==null)||(sInt.length()==0))
            throw new XMLFormatException("PriorityType attribute not set.");
        try {
            int type=Integer.parseInt(sInt);
            try {
                return factory(type);
            } catch (Exception e) {
                throw new XMLFormatException(e);
            }
        } catch (NumberFormatException e) {
            throw new XMLFormatException(e);
        }
    }
    
    public static Element toElement(NormalizerEnum type, String name) {
        Element rval=new Element(name);
        rval.setAttribute("type", ""+type.toUniqueInt());
        return rval;
    }
    
    public static NormalizerEnum fromElement(Element elt) throws XMLFormatException {
        return factory(elt,"type");
    }

    /**
     * Uses the normalizer type to normalize the values.
     * @param vals The values to normalize.  These will be changed.
     */
    public void normalize(double vals[]) {
        switch (this) {
            case Idealized:
                GenericMath.idealize(vals);
                break;
            case Normalized:
                GenericMath.normalize(vals);
                break;
            case Raw:
            default:
                return;
        }
    }
}

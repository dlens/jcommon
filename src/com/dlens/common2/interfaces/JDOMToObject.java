
package com.dlens.common2.interfaces;
import org.jdom.*;

import com.dlens.common2.exceptions.XMLFormatException;

import java.lang.reflect.*;
/**
 *
 * @author wjadams
 */
public abstract class JDOMToObject {
    public abstract Object objectFromJDOM(Element elt) throws XMLFormatException;
    public static JDOMToObject stringFromJDOM() {
        return new JDOMToObject() {
            public Object objectFromJDOM(Element elt) throws XMLFormatException {
                if (JDOMit.getBoolean(elt, "isNullObject")) {
                    return null;
                }
                return JDOMit.fromElement((String)null, elt);
            }
        };
    }
    public static JDOMToObject doubleFromJDOM() {
        return new JDOMToObject() {
            public Object objectFromJDOM(Element elt) throws XMLFormatException {
                return JDOMit.fromElement((Double)null, elt);
            }
        };
    }
    
    /**This takes an argument of a class that has an empty constructor, and is JDOMable.*/
    public static JDOMToObject jdomableFromJDOM(final Class cl) {
        return new JDOMToObject() {
            public Object objectFromJDOM(Element elt) throws XMLFormatException {
                Constructor con;
                if (JDOMit.getBoolean(elt, "isNullObject")) {
                    return null;
                } else if (JDOMit.getBoolean(elt, "isLong")) {
                	return JDOMit.getLLong(elt, "longValue");
                } else
                try {
                    con=cl.getConstructor((Class[])null);
                } catch (Exception e) {
                    throw new XMLFormatException(e);
                }
                JDOMable rval;
                try {
                    rval = (JDOMable)con.newInstance((Object[])null);
                } catch (Exception e) {
                    throw new XMLFormatException(e);
                }
                rval.fromElement(elt);
                return rval;
            }
        };
    }
}

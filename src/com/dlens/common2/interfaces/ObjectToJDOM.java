
package com.dlens.common2.interfaces;
import org.jdom.*;
import java.util.*;

/**
 *
 * @author wjadams
 */
public abstract class ObjectToJDOM {
    public abstract Element objectToJDOM(Object object, String name);
    
    public static ObjectToJDOM stringToJDOM() {
        return new ObjectToJDOM() {
            public Element objectToJDOM(Object object, String name) {
                return JDOMit.toElement(object.toString(), name);
            }
        };
    }

    public static ObjectToJDOM jdomableToJDOM() {
        return new ObjectToJDOM() {
            public Element objectToJDOM(Object object, String name) {
                if (object==null) {
                    Element rval=new Element(name);
                    rval.setAttribute("isNullObject", true+"");
                    return rval;
                } else if (object.getClass().equals(Long.class)) {
                	Element rval=new Element(name);
                	rval.setAttribute("longValue", object.toString());
                	rval.setAttribute("isLong", "true");
                	return rval;
                }
                JDOMable j=(JDOMable)object;
                return j.toElement(name);
            }
        };
    }
    
    public static ObjectToJDOM vectorToJDOM(ObjectToJDOM elementJDOMer) {
        final ObjectToJDOM jdomer=elementJDOMer;
        return new ObjectToJDOM() {
            public Element objectToJDOM(Object object, String name) {
                return JDOMit.toElement((Vector)object, name, jdomer);
            }
        };
    }
    
    public static ObjectToJDOM vectorOfJDOMablesToJDOM() {
        return vectorToJDOM(jdomableToJDOM());
    }
    
    public static ObjectToJDOM doubleToJDOM() {
    	return new ObjectToJDOM() {
    		public Element objectToJDOM(Object object, String name) {
    			return JDOMit.toElement((Double) object, name);
    		}
    	};
    }
}

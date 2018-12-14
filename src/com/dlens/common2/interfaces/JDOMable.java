
package com.dlens.common2.interfaces;

/**
 *
 * @author  William Adams
 */
public interface JDOMable {
    public org.jdom.Element toElement(String name);
    public void fromElement(org.jdom.Element elt) 
        throws com.dlens.common2.exceptions.XMLFormatException;
}

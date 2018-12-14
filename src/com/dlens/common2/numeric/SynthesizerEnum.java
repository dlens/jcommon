
package com.dlens.common2.numeric;

import com.dlens.common2.exceptions.XMLFormatException;
import com.dlens.common2.interfaces.JDOMable;
import com.dlens.common2.interfaces.JDOMit;
import org.jdom.Element;

/**
 * The types of synthesizers available.
 * @author Bill Adams
 */
public enum SynthesizerEnum {
    ADDITIVE,
    ADDITIVE_SD,
    ADD_PROB,
    MULTIPLICATIVE,
    MULTIPLICATIVE_SD;
    public Element toElement(String name) {
        Element rval=new Element(name);
        rval.setAttribute("type", toUniqueInt()+"");
        return rval;
    }

    public int toUniqueInt() {
        switch (this) {
            case ADDITIVE: return 0;
            case ADDITIVE_SD: return 1;
            case ADD_PROB: return 2;
            case MULTIPLICATIVE: return 3;
            case MULTIPLICATIVE_SD: return 4;
            default: return 0;
        }
    }
    
    public static SynthesizerEnum factory(int type) {
        switch (type) {
            case 0: return ADDITIVE;
            case 1: return ADDITIVE_SD;
            case 2: return ADD_PROB;
            case 3: return MULTIPLICATIVE;
            case 4: return MULTIPLICATIVE_SD;
            default: return ADDITIVE;
        }
    }
    
    public static SynthesizerEnum fromElement(Element elt) throws XMLFormatException {
        int type=JDOMit.getInt(elt, "type");
        return factory(type);
    }    
}

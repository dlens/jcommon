
package com.dlens.common2.numeric;

import org.jdom.Element;

/**
 *
 * @author Bill Adams
 */
public enum BSynthesizerFormula {
    //Simple multiply and add, the inversion details
    //are handled by the individual BSynthesizerCriteriaInverters
    ADDITIVE_SIMPLE,
    //Simple raise to power and multiply, inversion details
    //are handled by the individual BSynthesizerCriteriaInverters
    MULTIPLICATION_SIMPLE,
    UNKNOWN1,
    UNKNOWN2,
    UNKNOWN3,
    UNKNOWN4;

    public int toUniqueInt() {
        switch (this) {
            case ADDITIVE_SIMPLE:
                return 0;
            case MULTIPLICATION_SIMPLE:
                return 1;
            case UNKNOWN1:
                return 2;
            case UNKNOWN2:
                return 3;
            case UNKNOWN3:
                return 4;
            case UNKNOWN4:
                return 5;
            default:
                return 0;
        }
    }

    public static BSynthesizerFormula fromUniqueInt(int val) {
        switch(val) {
            case 0:
                return ADDITIVE_SIMPLE;
            case 1:
                return MULTIPLICATION_SIMPLE;
            case 2:
                return UNKNOWN1;
            case 3:
                return UNKNOWN2;
            case 4:
                return UNKNOWN3;
            case 5:
                return UNKNOWN4;
            default:
                return ADDITIVE_SIMPLE;
        }
    }

    public void toAttribute(Element elt, String att) {
        elt.setAttribute(att, toUniqueInt()+"");
    }

    public static BSynthesizerFormula fromAttribute(Element elt, String att) {
        try {
            String val=elt.getAttributeValue(att);
            int ival=Integer.parseInt(val);
            return fromUniqueInt(ival);
        } catch (Exception e) {
            //we are not picky here, if something failed, fall back to the
            //additive simple formula
            return ADDITIVE_SIMPLE;
        }
    }
}

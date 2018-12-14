
package com.dlens.common2.numeric;

/**
 *
 * @author Bill Adams
 */
public class BSynthesizer2Criteria {
    BSynthesizerCriteriaInverter inverter=BSynthesizerCriteriaInverter.EMPTY;
    public BSynthesizer2Criteria(BSynthesizerCriteriaInverter inverter) {
        this.inverter=inverter;
    }

    public void setInverter(BSynthesizerCriteriaInverter inverter) {
        this.inverter = inverter;
    }

    public BSynthesizerCriteriaInverter getInverter() {
        return inverter;
    }


}

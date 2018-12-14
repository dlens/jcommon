
package com.dlens.common2.numeric;

/**
 * Contains all of the information about a criteria
 * used in synthesis.
 * @author Bill Adams
 */
public class BSynthesizerCriteria {
    //How should this invert alts, EMPTY means do not invert.
    BSynthesizerCriteriaInverter inverter=BSynthesizerCriteriaInverter.EMPTY;
    //The column of the supermatrix where priorities are stored.
    int column;
    //The priority for this criteria (normalized by cluster)
    double priority;
    public BSynthesizerCriteria(int column, double priority, BSynthesizerCriteriaInverter inverter) {
        this.column=column;
        this.inverter=inverter;
        this.priority=priority;
    }
}

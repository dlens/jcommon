
package com.dlens.common2.numeric;

/**
 * Represents how structural adjust happens.
 * @author Bill Adams
 */
public enum BSynthesizerStructuralAdjust {
    //No structural adjust happens
    EMPTY,
    //Adjust per cluster
    BY_CLUSTER,
    //Adjust per column
    BY_COLUMN;
}

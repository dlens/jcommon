package com.dlens.common2.numeric.gtree;

/**
 * How should values cascade through a SumTree
 * @author Bill Adams
 *
 */
public enum SumTreeProportionalityEnum {
	//Cascade like priorities in a tree.
	GloablPriorities,
	//Cascade like costs.
	Costs;
	public static final SumTreeProportionalityEnum Default=Costs;
}

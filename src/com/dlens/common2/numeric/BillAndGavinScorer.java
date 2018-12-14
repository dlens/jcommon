package com.dlens.common2.numeric;

/**
 * An interface used to adjust scoring in a linear optimization system.
 *
 */
public interface BillAndGavinScorer {
	
	public double[] score(double origScores[], boolean requireds[][],
			boolean deads[][]);
			

}

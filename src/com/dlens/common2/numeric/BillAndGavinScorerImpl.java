package com.dlens.common2.numeric;

public class BillAndGavinScorerImpl implements BillAndGavinScorer {
	private double percentDeadsCost;
	public BillAndGavinScorerImpl(double percentDeadsCosts) {
		this.percentDeadsCost=percentDeadsCosts;
	}
	public BillAndGavinScorerImpl() {
		this(1);
	}
	public double getPercentDeadsCost() {
		return percentDeadsCost;
	}


	public double[] score(double[] origScores, boolean[][] requireds,
			boolean[][] deads) {
		if(origScores == null) 
			throw new IllegalArgumentException("Original scores can't be null");
		if(requireds == null)
			throw new IllegalArgumentException("Requireds can't be null");
		if(deads == null)
			throw new IllegalArgumentException("Deads can't be null");
		
		sanitytest(origScores, requireds, deads);
		
		double rval[] = new double[origScores.length];
		
		for (int i = 0; i < rval.length; i++) {
			rval[i] =score(i, origScores, requireds, deads);
		} 
		
		
		return rval;
	}
	

	private void sanitytest(double[] origScores, boolean[][] requireds,
			boolean[][] deads) {
		if(origScores.length != requireds.length) throw new IllegalArgumentException("Original Scores do not match Requireds");
		if(origScores.length != deads.length) throw new IllegalArgumentException("Original Scores do not match Deads");
		for (int i = 0; i < deads.length; i++) {
			if(requireds[i].length != requireds.length) throw new IllegalArgumentException("Requireds must be a square matrix");
			if(deads[i].length != deads.length) throw new IllegalArgumentException("Deads must be a square matrix");
			if(!requireds[i][i]) throw new IllegalArgumentException("Each project must require itself");
 }
		
	}


	private double score(int i, double[] origScores, boolean[][] requireds,
			boolean[][] deads) {
		double requiredsum = 0;
		int requiredcount = 0;
		double deadsum = 0;
		int deadcount = 0;
		for (int j = 0; j < deads.length; j++) {
			if(requireds[j][i]) { 
				requiredsum += origScores[j];
				requiredcount++;
			}
			if (deads[j][i]) {
				deadsum += origScores[j];
				deadcount++;
			}
		}
		double requiredaverage = 0;
		double deadaverage = 0;
		if(requiredcount != 0) requiredaverage = requiredsum/requiredcount;
		if(deadcount != 0) deadaverage = deadsum/deadcount;
		return requiredaverage - percentDeadsCost*deadaverage;
	}

}

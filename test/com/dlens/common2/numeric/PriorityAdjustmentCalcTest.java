package com.dlens.common2.numeric;

import static org.junit.Assert.*;

import org.junit.Test;

public class PriorityAdjustmentCalcTest {
	private double[] costs;
	private double[] priorities;
	private PriorityAdjustmentCalc pac;
	

	@Test
	public void test() {
		costs = new double[]{25, 30, 3, 300, 30};
		priorities = new double[]{0.3, 0.4, 0.6, 0.3, 0.4};
		pac = new PriorityAdjustmentCalc(costs, priorities);
		double[] yay = pac.transformation(); 
		System.out.println(yay[0]);
	}

}

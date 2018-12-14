package com.dlens.common2.numeric;

import org.junit.Test;


public class IntegerIntervalTest {

	@Test
	public void diffTimeTest() throws Exception {
		IntegerInterval i1 = new IntegerInterval(1, 4);
		IntegerInterval i2 = new IntegerInterval(2, 5);
		IntegerInterval restrict = new IntegerInterval(1, 4);
		IntegerInterval inew[] = IntegerInterval.newIntervalsFromDiffConstraintNew(i1, i2, restrict);
		System.out.println(inew[0].toString());
		System.out.println(inew[1].toString());
	}
}

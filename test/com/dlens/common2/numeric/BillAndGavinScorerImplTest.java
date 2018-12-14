package com.dlens.common2.numeric;

import static org.junit.Assert.*;

import org.junit.Test;

public class BillAndGavinScorerImplTest {

	@Test
	public void test1() throws Exception {
		double percentDeadscost = 1;
		BillAndGavinScorerImpl impl1 = new BillAndGavinScorerImpl(percentDeadscost);
		double[] origScores={1.0, .5};
		boolean[][] requireds = {{true, false}, {false, true}};
		boolean[][] deads = {{false, false}, {false, false}};
		double[] newscore = impl1.score(origScores, requireds, deads);
		assertNotNull(newscore);
		assertFalse(Double.isNaN(newscore[0]));
		assertFalse(Double.isNaN(newscore[1]));
		assertEquals(newscore.length, origScores.length);
		assertEquals(origScores[0], newscore[0], 0);
		assertEquals(origScores[1], newscore[1], 0);
	}
	
	@Test
	public void testRequires() throws Exception {
		double percentDeadscost = 1;
		BillAndGavinScorerImpl impl1 = new BillAndGavinScorerImpl(percentDeadscost);
		double[] origScores={1.0, .5};
		boolean[][] requireds = {{true, true}, {false, true}};
		boolean[][] deads = {{false, false}, {false, false}};
		double[] newscore = impl1.score(origScores, requireds, deads);
		assertNotNull(newscore);
		assertFalse(Double.isNaN(newscore[0]));
		assertFalse(Double.isNaN(newscore[1]));
		assertEquals(newscore.length, origScores.length);
		assertEquals(origScores[0], newscore[0], 0);
		assertEquals(.75, newscore[1], 0);
	}
	
	@Test
	public void testDeads() throws Exception {
		double percentDeadscost = 1;
		BillAndGavinScorerImpl impl1 = new BillAndGavinScorerImpl(percentDeadscost);
		double[] origScores={1.0, .5};
		boolean[][] requireds = {
				{true, false}, 
				{false, true}
				};
		boolean[][] deads = {
				{false, false}, 
				{true, false}
				};
		double[] newscore = impl1.score(origScores, requireds, deads);
		assertNotNull(newscore);
		assertFalse(Double.isNaN(newscore[0]));
		assertFalse(Double.isNaN(newscore[1]));
		assertEquals(newscore.length, origScores.length);
		assertEquals(.5, newscore[0], 0);
		assertEquals(origScores[1], newscore[1], 0);
	}

}

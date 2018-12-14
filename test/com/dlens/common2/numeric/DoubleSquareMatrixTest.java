package com.dlens.common2.numeric;

import static org.junit.Assert.*;

import java.util.Random;

import org.junit.Test;

import com.dlens.common2.exceptions.ConvergenceException;

public class DoubleSquareMatrixTest {
	private static double error = DoubleSquareMatrix.DEFAULT_EIGEN_ERROR;

	@Test
	public void testAdamsPriorityWithSpanning() throws ConvergenceException {
		Random rng = new Random(0L);
		int size=10;
		DoubleSquareMatrix a = randomReflexiveFull(size, rng);
		
		double[] adamsPriority = a.adamsByrnesPriority(error);
		double[] largestEigenvectorWithFix = a.largestEigenvectorWithFix(error);
		assertArrayEquals(largestEigenvectorWithFix, adamsPriority, error*100);
	}
	
	@Test
	public void testAdamsPriority2Sets() throws ConvergenceException {
		DoubleSquareMatrix dsm = new DoubleSquareMatrix(new double[][]
				{
				{1.0, 2.0, 6.0, 0.0, 0.0, 0.0},
				{0.5, 1.0, 3.0, 0.0, 0.0, 0.0},
				{1/6.0, 1/3.0, 1.0, 0.0, 0.0, 0.0},
				{0.0, 0.0, 0.0, 1.0, 4.0, 0.0},
				{0.0, 0.0, 0.0, 0.25, 1.0, 0.0},
				{0.0, 0.0, 0.0, 0.0, 0.0, 0.0}
				}
		);
		double[] adamsPriority = dsm.adamsByrnesPriority(error);
		assertEquals(2.0, adamsPriority[0]/adamsPriority[1], error);
		assertEquals(3.0, adamsPriority[1]/adamsPriority[2], error);
		assertEquals(4.0, adamsPriority[3]/adamsPriority[4], error);
		assertEquals(adamsPriority[0]/adamsPriority[3],  9.0/8.0, error);
		assertEquals(0.0, adamsPriority[5], error);
		assertEquals(adamsPriority[6], 6, error);
	}
	
	private static DoubleSquareMatrix randomReflexiveFull(int size, Random rng) {
		DoubleSquareMatrix rval = new DoubleSquareMatrix(size);
		for(int row=0; row < size; row++) {
			for(int col=row+1; col < size; col++) {
				rval.setReflexive(row, col, rng.nextDouble());
			}
		}
		return rval;
	}

	@Test
	public void testAdamsPriorityId() throws Exception {
		int size=10;
		DoubleSquareMatrix id = new DoubleSquareMatrix(size);
		id.setDiagoanlTo1(false);
		double[] priority = id.adamsByrnesPriority(error);
		assertEquals(size+1, priority.length);
		assertEquals(size+0.0, priority[size], 1e-15);
		double actual[] = new double[size+1];
		for(int i=0; i < size; i++)
			actual[i]=1.0/size;
		actual[size]=size;
		assertArrayEquals(actual, priority, 1e-15);
	}
	@Test
	public void testAdamsPriorityZero() throws Exception {
		int size=10;
		DoubleSquareMatrix zero = new DoubleSquareMatrix(size);
		double[] priority = zero.adamsByrnesPriority(error);
		assertEquals(size+1, priority.length);
		assertEquals(0.0, priority[size], 1e-15);
		double actual[] = new double[size+1];
		for(int i=0; i < size; i++)
			actual[i]=1.0/size;
		actual[size]=0;
		assertArrayEquals(actual, priority, 1e-15);
	}
	
	@Test
	public void testLargestEigenNoSpanning() throws Exception {
		DoubleSquareMatrix dsm = new DoubleSquareMatrix(new double[][] {
				{1.0, 2.0, 6.0, 0, 0},
				{0.5, 1.0, 3.0, 0, 0},
				{1/6.0,1/3.0,1.0, 0, 0},
				{0, 0, 0, 1, 4},
				{0, 0, 0, 1/4.0, 1}
		});
		double[] v1 = dsm.largestEigenvectorWithFix(error);
		double[] v2 = dsm.largestEigenvectorUsing2Powers(error);
		double[] v3 = dsm.adamsByrnesPriority(error);
		assertArrayEquals(new double[] {0.6, 0.3, 0.1, 0, 0, 3.0}, v2, 1e-10);
	}
}

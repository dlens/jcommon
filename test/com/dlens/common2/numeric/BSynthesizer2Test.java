package com.dlens.common2.numeric;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;


public class BSynthesizer2Test {
    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }



    @Test
    public void test1() throws Exception {
        BSynthesizer2 syn=new BSynthesizer2(new double[] {.6, .4});
        DoubleMatrix data=new DoubleMatrix(new double[][] {
            {1, .1},
            {.5, .7},
            {.2, .4},
            {.6, .8}
        });
        int[][] rowNodesInCluster={{0, 1},{2,3}};
        double rval[]=syn.eval(data, rowNodesInCluster);
        assertEquals(4, rval.length);
        assertEquals(.6*1 + .4*.1, rval[0], 1e-8);
        syn.setInverter(1, BSynthesizerCriteriaInverter.PROBABILISTIC);
        rval=syn.eval(data, rowNodesInCluster);
        assertEquals(.6*1 + .4*(1-.1), rval[0], 1e-10);
        syn.setInverter(1, BSynthesizerCriteriaInverter.NEGATIVE);
        rval=syn.eval(data, rowNodesInCluster);
        assertEquals(.6*1 + .4*(-.1), rval[0], 1e-10);

    }

    @Test
    public void test1Mult() throws Exception {
        BSynthesizer2 syn=new BSynthesizer2(new double[] {.6, .4});
        syn.setFormula(BSynthesizerFormula.MULTIPLICATION_SIMPLE);
        syn.setAdjust(BSynthesizerStructuralAdjust.EMPTY);
        DoubleMatrix data=new DoubleMatrix(new double[][] {
            {1, .1},
            {.5, .7},
            {.2, .4},
            {.6, .8}
        });
        int[][] rowNodesInCluster={{0, 1},{2,3}};
        double rval[]=syn.eval(data, rowNodesInCluster);
        assertEquals(4, rval.length);
        assertEquals(Math.pow(.1,.4), rval[0], 1e-8);
        syn.setInverter(1, BSynthesizerCriteriaInverter.PROBABILISTIC);
        rval=syn.eval(data, rowNodesInCluster);
        assertEquals(Math.pow((1-.1), .4), rval[0], 1e-10);
        syn.setInverter(1, BSynthesizerCriteriaInverter.NEGATIVE);
        rval=syn.eval(data, rowNodesInCluster);
        assertEquals(-Math.pow(.1, .4), rval[0], 1e-10);

    }

}

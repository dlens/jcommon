package com.dlens.common2.numeric;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;


public class BSynthesizerHandTest {
    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    private DoubleMatrix fourBy4Ideal() {
        return new DoubleMatrix(new double[][] {
            {.75, .65, 0, 0},
            {.5, .3, 0, 0},
            {.85, .25, 0 ,0},
            {.25, .9, 0, 0}
        });
    }
    @Test
    public void testInit() throws Exception {
        DoubleMatrix ideal=fourBy4Ideal();
        DoubleMatrix clusterMatrix=new DoubleMatrix(new double[][] {
            {.6, .3},
            {.4, .7}
        });
        int clusterPositions[][]={{0, 1}, {2, 3}};
        int clusterRow=0;
        int nodeColumn=0;
        BSynthesizer syn=new BSynthesizer(clusterPositions, clusterRow, nodeColumn, ideal, clusterMatrix);
        double [][]alts=ideal.submatrix(0, 3, 0, 1).getDataUnsafe();
        double synthesized[]=syn.synthesize(alts);
        assertEquals(synthesized.length, 4);
        assertEquals( .71, synthesized[0], 0);
        assertEquals(.42, synthesized[1], 0);
        assertEquals(.61, synthesized[2], 0);
        assertEquals(.51, synthesized[3], 0);
    }

    @Test
    public void testSubtract1() throws Exception {
        DoubleMatrix ideal=fourBy4Ideal();
        DoubleMatrix clusterMatrix=new DoubleMatrix(new double[][] {
            {.6, .3},
            {.4, .7}
        });
        int clusterPositions[][]={{0, 1}, {2, 3}};
        int clusterRow=0;
        int nodeColumn=0;
        BSynthesizer syn=new BSynthesizer(clusterPositions, clusterRow, nodeColumn, ideal, clusterMatrix);
        syn.setCriteriaInvertType(1, BSynthesizerCriteriaInverter.NEGATIVE);
        double [][]alts=ideal.submatrix(0, 3, 0, 1).getDataUnsafe();
        double synthesized[]=syn.synthesize(alts);
        assertEquals(synthesized.length, 4);
        assertEquals( .18999999999999, synthesized[0], 1e-6);
        assertEquals(.18, synthesized[1], 0);
        assertEquals(.41, synthesized[2], 1e-8);
        assertEquals(-.21, synthesized[3], 1e-8);
    }

    @Test
    public void testProb1() throws Exception {
        DoubleMatrix ideal=fourBy4Ideal();
        DoubleMatrix clusterMatrix=new DoubleMatrix(new double[][] {
            {.6, .3},
            {.4, .7}
        });
        int clusterPositions[][]={{0, 1}, {2, 3}};
        int clusterRow=0;
        int nodeColumn=0;
        BSynthesizer syn=new BSynthesizer(clusterPositions, clusterRow, nodeColumn, ideal, clusterMatrix);
        syn.setCriteriaInvertType(1, BSynthesizerCriteriaInverter.PROBABILISTIC);
        double [][]alts=ideal.submatrix(0, 3, 0, 1).getDataUnsafe();
        double synthesized[]=syn.synthesize(alts);
        assertEquals(synthesized.length, 4);
        assertEquals( .59, synthesized[0], 0);
        assertEquals(.58, synthesized[1], 0);
        assertEquals(.81, synthesized[2], 0);
        assertEquals(.19, synthesized[3], 0);
    }
}

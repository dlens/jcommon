package com.dlens.common2.numeric;

import com.dlens.common2.exceptions.ConvergenceException;
import java.util.HashSet;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Vector;


public class BSynthesizer2MatrixTest {
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
    private DoubleMatrix fourBy4Normal() {
        return new DoubleMatrix(new double[][] {
            {.6, .65/.95, 0, 0},
            {.4, .3/.95, 0, 0},
            {.85/1.05, .25/1.15, 0 ,0},
            {.25/1.05, .9/1.15, 0, 0}
        });
    }

    private DoubleMatrix twoBy2Normal() {
        return new DoubleMatrix(new double[][] {
            {.6, .3},
            {.4, .7}
        });
    }


    @Test
    public void testInit() throws Exception {
        int nodesInCluster[][]={{0,1},{2,3}};
        DoubleMatrix unscaledSupermatrix=fourBy4Normal();
        DoubleMatrix clusterMatrix=twoBy2Normal();
        BSynthesizer2Matrix syn=new BSynthesizer2Matrix(unscaledSupermatrix, clusterMatrix, nodesInCluster);
        syn.setInverter(0, 0, 1, BSynthesizerCriteriaInverter.PROBABILISTIC);
    }

    /**
     * This test works against the BSynthesisTest case
     * @throws Exception
     */
    @Test
    public void testEvalAgainstBSynthesisData() throws Exception {
        int nodesInCluster[][]={{0,1},{2,3}};
        DoubleMatrix ideal=fourBy4Ideal();
        DoubleMatrix unscaledSupermatrix=fourBy4Normal();
        DoubleMatrix clusterMatrix=twoBy2Normal();
        BSynthesizer2Matrix syn=new BSynthesizer2Matrix(unscaledSupermatrix, clusterMatrix, nodesInCluster);
        DoubleMatrix result=syn.eval(ideal, nodesInCluster, 0);
        assertEquals(4, result.getRows());
        assertEquals(2, result.getCols());
        result=syn.eval(ideal, nodesInCluster);
        assertEquals(4, result.getRows());
        assertEquals(4, result.getCols());
        assertTrue(result.isNear(new DoubleMatrix(new double[][] {
            {0.71, 0.718421052631579, 0.0, 0.0},
            {0.42, 0.4368421052631579, 0.0, 0.0},
            {0.61, 0.6605263157894737, 0.0, 0.0},
            {0.51, 0.4552631578947368, 0.0, 0.0}
        }), 1e-6));
    }

    @Test
    public void testLimitV2AgainstBSynthesisData() throws Exception {
        DoubleMatrix ideal=fourBy4Ideal();
        DoubleMatrix clusterMatrix=new DoubleMatrix(new double[][] {
            {.6, .3},
            {.4, .7}
        });
        DoubleMatrix unscaledSupermatrix=fourBy4Normal();
        int nodesInCluster[][]={{0, 1}, {2, 3}};
        int clusterRow=0;
        int nodeColumn=0;
        BSynthesizer2Matrix data=new BSynthesizer2Matrix(unscaledSupermatrix, clusterMatrix, nodesInCluster);
        DoubleMatrix limit=data.limitV2ANP(ideal, nodesInCluster, 1e-10, 100);
        assertNotNull(limit);
        assertEquals(4, limit.getSize());
        assertTrue(limit.isNear(new DoubleMatrix(new double[][] {
            {0.7131067950085781, 0.7131067980116428, 0.0, 0.0},
            {0.42621359001715625, 0.4262135960232854, 0.0, 0.0},
            {0.6286407700514687, 0.6286407880698561, 0.0, 0.0},
            {0.4898058324442423, 0.4898058129243225, 0.0, 0.0}
        }), 1e-6));
    }

    @Test
    public void testLimitWithNegHandCalcCheck() throws Exception {
        DoubleMatrix ideal=new DoubleMatrix(new double[][] {{.5, .3}, {.25, .9}});
        DoubleMatrix unscaledSupermatrix=new DoubleMatrix(ideal);
        unscaledSupermatrix.normalizeCols();
        DoubleMatrix clusterMatrix=new DoubleMatrix(new double[][] {{1}});
        int nodesInCluster[][]={{0,1}};
        BSynthesizer2Matrix data=new BSynthesizer2Matrix(unscaledSupermatrix, clusterMatrix, nodesInCluster);
        data.setInverter(0, 0, 1, BSynthesizerCriteriaInverter.PROBABILISTIC);
        data.setInverter(0, 1, 0, BSynthesizerCriteriaInverter.PROBABILISTIC);
        DoubleMatrix limit=data.limitV2ANP(ideal, nodesInCluster, 1e-10, 1000);
        assertNotNull(limit);
        assertEquals(2, limit.getSize());
        assertTrue(limit.isNear(new DoubleMatrix(new double[][] {
            {0.6142857142708377, 0.3857142857031285},
            {0.16428571429687136, 0.835714285722654}
        }), 1e-8));
    }


    @Test
    public void testLimitWithNegHandCalcCheckExtension1() throws Exception {
        DoubleMatrix ideal=new DoubleMatrix(new double[][] {{.5, .3, 0, 0}, {.25, .9, 0, 0}, {.9, .3, 0, 0}, {.5, .6, 0, 0}});
        DoubleMatrix clusterMatrix=new DoubleMatrix(new double[][] {{.5, .5}, {.5, .5}});
        int nodesInCluster[][]={{0,1}, {2, 3}};
        BSynthesizer2Matrix data = BSynthesizer2Matrix.factorFromIdeal(ideal, clusterMatrix, nodesInCluster);
        data.setInverter(0, 0, 1, BSynthesizerCriteriaInverter.PROBABILISTIC);
        data.setInverter(0, 1, 0, BSynthesizerCriteriaInverter.PROBABILISTIC);
        DoubleMatrix limit=data.limitV2ANP(ideal, nodesInCluster, 1e-10, 1000);
        assertNotNull(limit);
        assertEquals(4, limit.getSize());
        assertTrue(limit.isNear(new DoubleMatrix(new double[][] {
            {0.6142857142708377, 0.3857142857031285, 0.0, 0.0},
            {0.16428571429687136, 0.835714285722654, 0.0, 0.0},
            {0.7857142857291614, 0.21428571429687238, 0.0, 0.0},
            {0.44285714286458094, 0.5571428571484359, 0.0, 0.0}
        }), 1e-8));
    }

    @Test
    public void testLimitWithNegDan1() throws Exception {
        DoubleMatrix ideal=new DoubleMatrix(new double[][] {{.5, .3}, {.25, .9}});
        DoubleMatrix clusterMatrix=new DoubleMatrix(new double[][] {{1}});
        int nodesInCluster[][]={{0,1}};
        BSynthesizer2Matrix data=BSynthesizer2Matrix.factorFromIdeal(ideal, clusterMatrix, nodesInCluster);
        data.setInverter(0, 0, 1, BSynthesizerCriteriaInverter.PROBABILISTIC);
        //data.setCriteriaInverter(0, 1, 0, BSynthesizerCriteriaInverter.PROBABILISTIC);
        DoubleMatrix limit=data.limitV2ANP(ideal, nodesInCluster, 1e-10, 1000);
        assertNotNull(limit);
        assertEquals(2, limit.getSize());
        assertTrue(limit.isNear(new DoubleMatrix(new double[][] {
            {0.5, 0.5},
            {0.5, 0.5}
        }), 1e-8));
    }

    @Test
    public void testLimitWithNegDan1kExtension1() throws Exception {
        DoubleMatrix ideal=new DoubleMatrix(new double[][] {{.5, .3, 0, 0}, {.25, .9, 0, 0}, {.9, .3, 0, 0}, {.5, .6, 0, 0}});
        DoubleMatrix clusterMatrix=new DoubleMatrix(new double[][] {{.5, .5}, {.5, .5}});
        int nodesInCluster[][]={{0,1}, {2, 3}};
        BSynthesizer2Matrix data=BSynthesizer2Matrix.factorFromIdeal(ideal, clusterMatrix, nodesInCluster);
        data.setInverter(0, 0, 1, BSynthesizerCriteriaInverter.PROBABILISTIC);
        //data.setCriteriaInverter(0, 1, 0, BSynthesizerCriteriaInverter.PROBABILISTIC);
        DoubleMatrix limit=data.limitV2ANP(ideal, nodesInCluster, 1e-10, 1000);
        assertNotNull(limit);
        assertEquals(4, limit.getSize());
        assertTrue(limit.isNear(new DoubleMatrix(new double[][] {
            {0.5, 0.5, 0.0, 0.0},
            {0.5, 0.5, 0.0, 0.0},
            {0.5, 0.5, 0.0, 0.0},
            {0.5, 0.5, 0.0, 0.0}
        }), 1e-8));
    }

    @Test
    public void testLimitPriority1() throws Exception {
        DoubleMatrix ideal=new DoubleMatrix(new double[][] {{.5, .3}, {.25, .9}});
        DoubleMatrix clusterMatrix=new DoubleMatrix(new double[][] {{1}});
        int nodesInCluster[][]={{0,1}};
        BSynthesizer2Matrix data=BSynthesizer2Matrix.factorFromIdeal(ideal, clusterMatrix, nodesInCluster);
        data.setInverter(0, 0, 1, BSynthesizerCriteriaInverter.PROBABILISTIC);
        data.setInverter(0, 1, 0, BSynthesizerCriteriaInverter.PROBABILISTIC);
        DoubleMatrix limit=data.limitV2ANP(ideal, nodesInCluster, 1e-10, 1000);
        assertNotNull(limit);
        assertEquals(2, limit.getSize());
        assertTrue(limit.isNear(new DoubleMatrix(new double[][] {
            {0.6142857142708377, 0.3857142857031285},
            {0.16428571429687136, 0.835714285722654}
        }), 1e-8));
        double limitPriorityV1[]=data.limitPrioritiesV1();
        double limitPriorityV2[]=data.limitPriorities(ideal, nodesInCluster);
        assertNotNull(limitPriorityV1);
        assertNotNull(limitPriorityV2);
        assertEquals(2, limitPriorityV1.length);
        assertEquals(3.0/7, limitPriorityV1[0], 1e-8);
        assertEquals(4.0/7, limitPriorityV1[1], 1e-8);
        //This is testing averaging all columns
        assertEquals(limitPriorityV1[0]*limit.get(0, 0)+limitPriorityV1[1]*limit.get(0, 1), limitPriorityV2[0], 1e-8);
        assertEquals(limitPriorityV1[0]*limit.get(1, 0)+limitPriorityV1[1]*limit.get(1, 1), limitPriorityV2[1], 1e-8);
        //Now test only getting second
        data.setGoalColumns(new int[] {1});
        limitPriorityV2=data.limitPriorities(ideal, nodesInCluster);
        assertEquals(limit.get(0, 1), limitPriorityV2[0], 1e-8);
        assertEquals(limit.get(1, 1), limitPriorityV2[1], 1e-8);
        //Now test only getting first
        data.setGoalColumns(new int[] {0});
        limitPriorityV2=data.limitPriorities(ideal, nodesInCluster);
        assertEquals(limit.get(0, 0), limitPriorityV2[0], 1e-8);
        assertEquals(limit.get(1, 0), limitPriorityV2[1], 1e-8);
    }

    /**
     * The method DoubleMatrix.getLevelsV2() was solely added for v2 synthesis
     * thus we will test it here.
     */
    @Test
    public void testGetLevelsV2() throws Exception {
        DoubleMatrix t1=new DoubleMatrix(new double[][] {
            {0, 0, 0, 0},
            {1, 0, 0, 0},
            {1, 0, 0, 0},
            {1, 1, 1, 0}
        });
        int levels[][]=t1.getLevelsV2();
        assertEquals(3, levels.length);
        assertEquals(1, levels[0].length);
        assertEquals(3, levels[0][0]);
        assertEquals(2, levels[1].length);
        assertEquals(2, levels[1][0]);
        assertEquals(1, levels[1][1]);
        assertEquals(1, levels[2].length);
        assertEquals(0, levels[2][0]);
        //Check that no bottom level causes exception
        DoubleMatrix t2=new DoubleMatrix(new double[][] {
            {0, 0},
            {1, 1}
        });
        try {
            t2.getLevelsV2();
            fail("Should have thrown exception");
        } catch (IllegalArgumentException e) {
        }
    }

    /**
     * Tests the new limit priorities calculation for a hierarchy
     */
    @Test
    public void testLimiPrioritiestHierarchyV2() throws Exception {
        DoubleMatrix idealMatrix=new DoubleMatrix(new double[][] {
            {0, 0, 0, 0, 0},
            {.75, 0, 0, 0, 0},
            {.5, 0, 0, 0, 0},
            {0, .85, .65, 0, 0},
            {0, .4, .1, 0, 0}
        });
        DoubleMatrix clusterMatrix=new DoubleMatrix(new double[][] {
            {0, 0, 0},
            {1, 0, 0},
            {0, 1, 0}
        });
        int nodesInCluster[][]={{0},{1,2},{3,4}};
        BSynthesizer2Matrix data=BSynthesizer2Matrix.factorFromIdeal(idealMatrix, clusterMatrix, nodesInCluster);
        DoubleMatrix limit=data.limitV2Hierarchy(idealMatrix, nodesInCluster);
        DoubleMatrix actual=new DoubleMatrix(new double[][] {
            {0.0, 0.0, 0.0, 0.0, 0.0},
            {0.75, 0.0, 0.0, 0.0, 0.0},
            {0.5, 0.0, 0.0, 0.0, 0.0},
            {0.77, 0.85, 0.65, 0.0, 0.0},
            {0.28, 0.4, 0.1, 0.0, 0.0}
        });
        assertTrue(actual.isNear(limit, 1e-8));
        DoubleMatrix limit2=data.limitV2(idealMatrix, nodesInCluster);
        assertTrue(actual.isNear(limit2, 1e-8));
        double []global=data.limitPriorities(idealMatrix, nodesInCluster);
        assertEquals(5, global.length);
        assertEquals(0.0, global[0], 1e-8);
        assertEquals(0.75, global[1], 1e-8);
        assertEquals(0.5, global[2], 1e-8);
        assertEquals(0.77, global[3], 1e-8);
        assertEquals(0.28, global[4], 1e-8);
    }

    /**
     * Tests the new limitHierarchyV2 command in simple cases.
     * Calls that method directly, and tests limitV2() to make
     * sure it calls the hierarchy method.
     */
    @Test
    public void testLimitHierarchyV2() throws Exception {
        DoubleMatrix idealMatrix=new DoubleMatrix(new double[][] {
            {0, 0, 0, 0, 0},
            {.75, 0, 0, 0, 0},
            {.5, 0, 0, 0, 0},
            {0, .85, .65, 0, 0},
            {0, .4, .1, 0, 0}
        });
        DoubleMatrix clusterMatrix=new DoubleMatrix(new double[][] {
            {0, 0, 0},
            {1, 0, 0},
            {0, 1, 0}
        });
        int nodesInCluster[][]={{0},{1,2},{3,4}};
        BSynthesizer2Matrix data=BSynthesizer2Matrix.factorFromIdeal(idealMatrix, clusterMatrix, nodesInCluster);
        DoubleMatrix limit=data.limitV2Hierarchy(idealMatrix, nodesInCluster);
        DoubleMatrix actual=new DoubleMatrix(new double[][] {
            {0.0, 0.0, 0.0, 0.0, 0.0},
            {0.75, 0.0, 0.0, 0.0, 0.0},
            {0.5, 0.0, 0.0, 0.0, 0.0},
            {0.77, 0.85, 0.65, 0.0, 0.0},
            {0.28, 0.4, 0.1, 0.0, 0.0}
        });
        assertTrue(actual.isNear(limit, 1e-8));
        DoubleMatrix limit2=data.limitV2(idealMatrix, nodesInCluster);
        assertTrue(actual.isNear(limit2, 1e-8));
    }

    /**
     * Tests the structural adjust in a multi-level setup
     */
    @Test
    @Ignore
    public void testHierarchyAdjust() throws Exception {
        DoubleMatrix idealMatrix=new DoubleMatrix(new double[][] {
            {0, 0, 0, 0, 0, 0, 0, 0, 0},
            {1, 0, 0, 0, 0, 0, 0, 0, 0},
            {.5, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, .8, 0, 0, 0, 0, 0, 0, 0},
            {0, .3, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, .5, 0, 0, 0, 0, 0, 0},
            {0, 0, .9, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, .8, 0, 0, .1, 0, 0},
            {0, 0, 0, 0, .2, .6, 0, 0, 0}
        });
        DoubleMatrix clusterMatrix=new DoubleMatrix(new double[][] {
            {0, 0, 0, 0, 0},
            {1, 0, 0, 0, 0},
            {0, 1, 0, 0, 0},
            {0, 1, 0, 0, 0},
            {0, 0, 1, 1, 0}
        });
        int nodesInCluster[][]={{0},{1,2},{3,4},{5,6},{7,8}};
        BSynthesizer2Matrix data=BSynthesizer2Matrix.factorFromIdeal(idealMatrix, clusterMatrix, nodesInCluster);
        BSynthesizerStructuralAdjust adjust=BSynthesizerStructuralAdjust.EMPTY;
//        data.setAdjust(1, 0, adjust);
//        data.setAdjust(2, 1, adjust);
//        data.setAdjust(3, 1, adjust);
//        data.setAdjust(0, adjust);
//        data.setAdjust(1, adjust);
//        data.setAdjust(2, adjust);
        //int rowNodesInCluster[][]={{0}, {1,2,3,4,5,6}, {7,8}};
        DoubleMatrix limit=data.limitV2(idealMatrix, nodesInCluster);
        fail("Not done.");
    }

    public DoubleMatrix distanceMatrix(double []seq) {
        DoubleMatrix rval=new DoubleMatrix(seq.length);
        for(int i=0; i<seq.length; i++) {
            rval.set(i, i, 0);
            for(int j=i+1; j<seq.length; j++) {
                double dist=Math.abs(seq[i]-seq[j]);
                rval.set(i,j,dist);
                rval.set(j,i,dist);
            }
        }
        return rval;
    }
    @Test
    public void testConvergentSubsequence() throws Exception {
        DoubleMatrix dist=distanceMatrix(new double[] {1.1, 1.07, 2, 1.05, 1.04, 1.03, 1.02, 1.01, 1.001, 3, 4});
        Vector<Integer>conv=BSynthesizer2Matrix.extractAConvergentSubsequence(dist, .04, null);
        Vector<Vector<Integer>>subseqs=BSynthesizer2Matrix.convergentSubsequences(dist, .04);
        HashSet<Integer>correct=new HashSet();
        correct.add(0); correct.add(1); correct.add(3);
        correct.add(4); correct.add(5); correct.add(6);
        correct.add(7); correct.add(8);
        assertEquals(correct, new HashSet(conv));
        assertEquals(1, subseqs.size());
        assertEquals(correct, new HashSet(subseqs.get(0)));
    }

    private Vector<Vector<Integer>> getVec(int [][]data) {
        Vector<Vector<Integer>>rval=new Vector<Vector<Integer>>();
        for(int i=0; i<data.length; i++) {
            Vector<Integer>level=new Vector<Integer>();
            for(int j=0; j<data[i].length; j++) {
                level.add(new Integer(data[i][j]));
            }
            rval.add(level);
        }
        return rval;
    }

    @Test
    public void testGetMinMax() throws Exception {
        Vector<Vector<Integer>>list=getVec(new int[][] {
            {5, 3, 7}, {2, 1 ,5}, {10, 11, 5}
        });
        int minMax[]=BSynthesizer2Matrix.getMinMax(list);
        assertEquals(2, minMax.length);
        assertEquals(1, minMax[0]);
        assertEquals(11, minMax[1]);
    }
    @Test
    public void test2StateLimit() throws Exception {
        DoubleMatrix idealMatrix=new DoubleMatrix(new double[][] {
            {0, 1},
            {1, 0}
        });
        DoubleMatrix clusterMatrix=new DoubleMatrix(new double[][] {{1}});
        int nodesInCluster[][]={{0,1}};
        BSynthesizer2Matrix data=BSynthesizer2Matrix.factorFromIdeal(idealMatrix, clusterMatrix, nodesInCluster);
        DoubleMatrix limit=data.limitV2(idealMatrix, nodesInCluster);
        DoubleMatrix actual=new DoubleMatrix(new double[][] {{.5, .5}, {.5, .5}});
        assertEquals(limit, actual);
    }

    @Test
    public void test2StateLimit2() throws Exception {
        DoubleMatrix idealMatrix=new DoubleMatrix(new double[][] {
            {.75, .5},
            {.5, .8}
        });
        DoubleMatrix normal=idealMatrix.normalizeCols();
        DoubleMatrix clusterMatrix=new DoubleMatrix(new double[][] {{1}});
        int nodesInCluster[][]={{0,1}};
        BSynthesizer2Matrix data=BSynthesizer2Matrix.factorFromIdeal(idealMatrix, clusterMatrix, nodesInCluster);
        DoubleMatrix limit=data.limitV2(idealMatrix, nodesInCluster);
        DoubleMatrix limitOld=normal.limitPowerIntelligent(1e-10);
        limit.normalizeCols();
        assertTrue(limit.isNear(limitOld, 1e-8));

    }

    private DoubleMatrix randomMatrix(int size, double zeroDistrib, double min, double max, boolean normalizeCols) {
        DoubleMatrix rval=new DoubleMatrix(size);
        for(int row=0; row < size; row++) {
            for(int col=0; col < size; col++) {
                double t=Math.random();
                if (t < zeroDistrib) {
                    rval.set(row, col, 0);
                } else {
                    double tmp=Math.random();
                    tmp=min+(max-min)*tmp;
                    rval.set(row, col, tmp);
                }
            }
        }
        if (normalizeCols)
            rval.normalizeCols();
        return rval;
    }

    /**
     * Checking that the v1 and v2 limits agree when used in the same way.
     * @throws Exception
     */
    @Test
    public void testV1ToV2Limit() throws Exception {
        int testCount=10;
        for(int i=0; i<testCount; i++) {
            DoubleMatrix idealMatrix=randomMatrix(10, .3, 0, 1, true);
            DoubleMatrix normalMatrix=new DoubleMatrix(idealMatrix);
            normalMatrix.normalizeCols();
            DoubleMatrix cluserMatrix=new DoubleMatrix(new double[][] {{1}});
            int nodesInCluster[][]={{0,1,2,3,4,5,6,7,8,9}};
            BSynthesizer2Matrix data=BSynthesizer2Matrix.factorFromIdeal(idealMatrix, cluserMatrix, nodesInCluster);
            data.setAllAdjust(BSynthesizerStructuralAdjust.EMPTY);
            DoubleMatrix limit=data.limitV2(idealMatrix, nodesInCluster);
            DoubleMatrix limitOld=normalMatrix.limitPowerIntelligent(1e-10);
            limit.normalizeCols();
            if (!limit.isNear(limitOld, 1e-8)) {
                String info="Failed on pass "+i+"\nWith input matrix\n"+
                        idealMatrix+"\n"+"New limit:\n"+limit+"\nOld limit:\n"+limitOld;
                fail(info);
            }
        }
    }
}

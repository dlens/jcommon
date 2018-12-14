package com.dlens.common2.numeric;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;


public class BSynthesisDataTest {
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
        BSynthesisData data=new BSynthesisData(ideal, clusterMatrix, clusterPositions);
    }

    @Test
    public void testSynthesisStepOnCol() throws Exception {
        DoubleMatrix ideal=fourBy4Ideal();
        DoubleMatrix clusterMatrix=new DoubleMatrix(new double[][] {
            {.6, .3},
            {.4, .7}
        });
        int clusterPositions[][]={{0, 1}, {2, 3}};
        int clusterRow=0;
        int nodeColumn=0;
        BSynthesisData data=new BSynthesisData(ideal, clusterMatrix, clusterPositions);
        double syn[]=data.synthesisStep(ideal, 0);
        assertNotNull(syn);
        assertEquals(4, syn.length);
        assertEquals(.71, syn[0], 0);
        assertEquals(.42, syn[1], 0);
        assertEquals(.61, syn[2], 0);
        assertEquals(.51, syn[3], 0);
    }

    @Test
    public void testSynthesisStep() throws Exception {
        DoubleMatrix ideal=fourBy4Ideal();
        DoubleMatrix clusterMatrix=new DoubleMatrix(new double[][] {
            {.6, .3},
            {.4, .7}
        });
        int clusterPositions[][]={{0, 1}, {2, 3}};
        int clusterRow=0;
        int nodeColumn=0;
        BSynthesisData data=new BSynthesisData(ideal, clusterMatrix, clusterPositions);
        DoubleMatrix syn=data.synthesisStep(ideal);
        assertNotNull(syn);
        assertEquals(4, syn.getSize());
        assertTrue(syn.isNear(new DoubleMatrix(new double[][] {
            {0.71, 0.718421052631579, 0.0, 0.0},
            {0.42, 0.4368421052631579, 0.0, 0.0},
            {0.61, 0.6605263157894737, 0.0, 0.0},
            {0.51, 0.4552631578947368, 0.0, 0.0}
        }), 1e-6));
    }

    @Test
    public void testLimitV2FirstAttempt() throws Exception {
        DoubleMatrix ideal=fourBy4Ideal();
        DoubleMatrix clusterMatrix=new DoubleMatrix(new double[][] {
            {.6, .3},
            {.4, .7}
        });
        int clusterPositions[][]={{0, 1}, {2, 3}};
        int clusterRow=0;
        int nodeColumn=0;
        BSynthesisData data=new BSynthesisData(ideal, clusterMatrix, clusterPositions);
        DoubleMatrix limit=data.limitV2FirstAttempt(1e-8, 1000);
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
        DoubleMatrix clusterMatrix=new DoubleMatrix(new double[][] {{1}});
        int clusterPositions[][]={{0,1}};
        BSynthesisData data=new BSynthesisData(ideal, clusterMatrix, clusterPositions);
        data.setCriteriaInverter(0, 0, 1, BSynthesizerCriteriaInverter.PROBABILISTIC);
        data.setCriteriaInverter(0, 1, 0, BSynthesizerCriteriaInverter.PROBABILISTIC);
        DoubleMatrix limit=data.limitV2FirstAttempt(1e-10, 1000);
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
        int clusterPositions[][]={{0,1}, {2, 3}};
        BSynthesisData data=new BSynthesisData(ideal, clusterMatrix, clusterPositions);
        data.setCriteriaInverter(0, 0, 1, BSynthesizerCriteriaInverter.PROBABILISTIC);
        data.setCriteriaInverter(0, 1, 0, BSynthesizerCriteriaInverter.PROBABILISTIC);
        DoubleMatrix limit=data.limitV2FirstAttempt(1e-10, 1000);
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
        int clusterPositions[][]={{0,1}};
        BSynthesisData data=new BSynthesisData(ideal, clusterMatrix, clusterPositions);
        data.setCriteriaInverter(0, 0, 1, BSynthesizerCriteriaInverter.PROBABILISTIC);
        //data.setCriteriaInverter(0, 1, 0, BSynthesizerCriteriaInverter.PROBABILISTIC);
        DoubleMatrix limit=data.limitV2FirstAttempt(1e-10, 1000);
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
        int clusterPositions[][]={{0,1}, {2, 3}};
        BSynthesisData data=new BSynthesisData(ideal, clusterMatrix, clusterPositions);
        data.setCriteriaInverter(0, 0, 1, BSynthesizerCriteriaInverter.PROBABILISTIC);
        //data.setCriteriaInverter(0, 1, 0, BSynthesizerCriteriaInverter.PROBABILISTIC);
        DoubleMatrix limit=data.limitV2FirstAttempt(1e-10, 1000);
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
    public void testScaledNormalMatrix() throws Exception {
        DoubleMatrix ideal=new DoubleMatrix(new double[][] {
            {0, .5, .7, 0, 0, 0},
            {1, .6, .3, .8, 1, .2},
            {.5, .4, .7, .2, .5, 1},
            {.2, .2, .5, 0, 0, 0},
            {.4, .3, .3, 0, 0, 0},
            {.6, .5, .2, 0, 0, 0}
        });
        DoubleMatrix cluster=new DoubleMatrix(new double[][] {
            {0, .3, 0},
            {.6, .1, 1},
            {.4, .6, 0}
        });
        int[][] clusterPositions={{0}, {1,2},{3,4,5}};
        BSynthesisData data=new BSynthesisData(ideal, cluster, clusterPositions);
        DoubleMatrix clusterNormal=data.clusterScaledNormalSupermatrix();
        DoubleMatrix handCalcdAnswer=new DoubleMatrix(new double[][] {
            {0, .3, .3, 0, 0, 0},
            {1.2/3, .06, .03, .8, 2.0/3, 1.0/6},
            {.6/3, .04, .07, .2, 1.0/3, 5.0/6},
            {.4/6, .12, .3, 0, 0, 0},
            {.4/3, .18, .18, 0, 0, 0},
            {.4/2, .3, .12, 0, 0, 0}
        });
        //System.out.println(clusterNormal.toString());
        //System.out.println(handCalcdAnswer.toString());
        assertTrue(handCalcdAnswer.isNear(clusterNormal, 1e-8));
    }


    @Test
    public void testLimitPriority1() throws Exception {
        DoubleMatrix ideal=new DoubleMatrix(new double[][] {{.5, .3}, {.25, .9}});
        DoubleMatrix clusterMatrix=new DoubleMatrix(new double[][] {{1}});
        int clusterPositions[][]={{0,1}};
        BSynthesisData data=new BSynthesisData(ideal, clusterMatrix, clusterPositions);
        data.setCriteriaInverter(0, 0, 1, BSynthesizerCriteriaInverter.PROBABILISTIC);
        data.setCriteriaInverter(0, 1, 0, BSynthesizerCriteriaInverter.PROBABILISTIC);
        DoubleMatrix limit=data.limitV2FirstAttempt(1e-10, 1000);
        assertNotNull(limit);
        assertEquals(2, limit.getSize());
        assertTrue(limit.isNear(new DoubleMatrix(new double[][] {
            {0.6142857142708377, 0.3857142857031285},
            {0.16428571429687136, 0.835714285722654}
        }), 1e-8));
        double limitPriorityV1[]=data.limitPrioritiesV1();
        double limitPriorityV2[]=data.limitPriorities();
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
        limitPriorityV2=data.limitPriorities();
        assertEquals(limit.get(0, 1), limitPriorityV2[0], 1e-8);
        assertEquals(limit.get(1, 1), limitPriorityV2[1], 1e-8);
        //Now test only getting first
        data.setGoalColumns(new int[] {0});
        limitPriorityV2=data.limitPriorities();
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
        int clusterPositions[][]={{0},{1,2},{3,4}};
        BSynthesisData data=new BSynthesisData(idealMatrix, clusterMatrix, clusterPositions);
        DoubleMatrix limit=data.limitV2Hierarchy();
        DoubleMatrix actual=new DoubleMatrix(new double[][] {
            {0.0, 0.0, 0.0, 0.0, 0.0},
            {0.75, 0.0, 0.0, 0.0, 0.0},
            {0.5, 0.0, 0.0, 0.0, 0.0},
            {0.77, 0.85, 0.65, 0.0, 0.0},
            {0.28, 0.4, 0.1, 0.0, 0.0}
        });
        assertTrue(actual.isNear(limit, 1e-8));
        DoubleMatrix limit2=data.limitV2();
        assertTrue(actual.isNear(limit2, 1e-8));
        double []global=data.limitPriorities();
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
        int clusterPositions[][]={{0},{1,2},{3,4}};
        BSynthesisData data=new BSynthesisData(idealMatrix, clusterMatrix, clusterPositions);
        DoubleMatrix limit=data.limitV2Hierarchy();
        DoubleMatrix actual=new DoubleMatrix(new double[][] {
            {0.0, 0.0, 0.0, 0.0, 0.0},
            {0.75, 0.0, 0.0, 0.0, 0.0},
            {0.5, 0.0, 0.0, 0.0, 0.0},
            {0.77, 0.85, 0.65, 0.0, 0.0},
            {0.28, 0.4, 0.1, 0.0, 0.0}
        });
        assertTrue(actual.isNear(limit, 1e-8));
        DoubleMatrix limit2=data.limitV2();
        assertTrue(actual.isNear(limit2, 1e-8));
    }
}

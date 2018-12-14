
package com.dlens.common2.numeric;

import java.util.Date;
import java.util.Vector;
import org.jdom.Element;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author wjadams
 */
public class DoubleMatrixTest {
    private double ERR=1e-6;
    public DoubleMatrixTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void testSynthesisStep() {
        System.out.println("testSynthesisStep");
        DoubleMatrix me=new DoubleMatrix(new double[][] {
            {0, 0, 0, 0, 0},
            {.6, 0, 0, 0, 0},
            {.4, 0, 0, 0, 0},
            {0, .3, .1, 0,0},
            {0, .7, .9, 0,0}
        });
        int clusterStarts[]={0, 1, 3};
        double clusterPriorities[][]={
            {0, 0, 0},
            {1, 0, 0},
            {0, 1, 0}
        };
        CalcSynInfo s1=new CalcSynInfo(SynthesizerEnum.ADDITIVE, 1);
        CalcSynInfo s2=new CalcSynInfo(SynthesizerEnum.ADDITIVE, 2);
        CalcSynInfo nodeSyns[][]={
            {s1,s1,s1,s1,s1},
            {s2,s2,s2,s2,s2},
            {s2,s2,s2,s2,s2}
        };
        DoubleMatrix rval=new DoubleMatrix(me);
        CalcSynInfo c3=new CalcSynInfo(SynthesizerEnum.ADDITIVE, 3);
        CalcSynInfo []clusterSyns={c3,c3,c3};
        me.synthesisStep(me, rval, nodeSyns, clusterSyns, clusterStarts, clusterPriorities, NormalizerEnum.Normalized);
        DoubleMatrix correct=new DoubleMatrix(new double[][] {
            {0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0},
            {.22, 0, 0, 0, 0},
            {.78, 0, 0, 0, 0}
        });
        assertTrue(correct.isNear(rval));
    }

    @Test
    public void testSyn2LevelInvert() {
        System.out.println("testSyn2LevelInvert");
        DoubleMatrix me=new DoubleMatrix(new double[][] {
            {0, 0, 0, 0, 0},
            {.6, 0, 0, 0, 0},
            {.4, 0, 0, 0, 0},
            {0, .3, .1, 0,0},
            {0, .7, .9, 0,0}
        });
        int clusterStarts[]={0, 1, 3};
        double clusterPriorities[][]={
            {0, 0, 0},
            {1, 0, 0},
            {0, 1, 0}
        };
        CalcSynInfo s1=new CalcSynInfo(SynthesizerEnum.ADDITIVE, 1);
        CalcSynInfo s2=new CalcSynInfo(SynthesizerEnum.ADDITIVE, 2);
        s2.setInverted(1, true);
        CalcSynInfo nodeSyns[][]={
            {s1,s1,s1,s1,s1},
            {s2,s2,s2,s2,s2},
            {s2,s2,s2,s2,s2}
        };
        DoubleMatrix rval=new DoubleMatrix(me);
        CalcSynInfo c3=new CalcSynInfo(SynthesizerEnum.ADDITIVE, 3);
        CalcSynInfo []clusterSyns={c3,c3,c3};
        me.synthesisStep(me, rval, nodeSyns, clusterSyns, clusterStarts, clusterPriorities, NormalizerEnum.Normalized);
        DoubleMatrix correct=new DoubleMatrix(new double[][] {
            {0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0},
            {.54, 0, 0, 0, 0},
            {.46, 0, 0, 0, 0}
        });
        //System.out.println(rval.toString()+"\n\n");
        assertTrue(correct.isNear(rval));
    }

    @Test
    public void testSyn2LevelInvert3Alts() {
        System.out.println("testSyn2LevelInvert3Alts");
        DoubleMatrix me=new DoubleMatrix(new double[][] {
            {0, 0, 0, 0, 0, 0},
            {.6, 0, 0, 0, 0, 0},
            {.4, 0, 0, 0, 0, 0},
            {0, .2, .1, 0,0, 0},
            {0, .7, .6, 0,0, 0},
            {0, .1, .3, 0, 0, 0}
        });
        int clusterStarts[]={0, 1, 3};
        double clusterPriorities[][]={
            {0, 0, 0},
            {1, 0, 0},
            {0, 1, 0}
        };
        CalcSynInfo s1=new CalcSynInfo(SynthesizerEnum.ADDITIVE, 1);
        CalcSynInfo s2=new CalcSynInfo(SynthesizerEnum.ADDITIVE, 2);
        CalcSynInfo s3=new CalcSynInfo(SynthesizerEnum.ADDITIVE, 3);
        s2.setInverted(1, true);
        CalcSynInfo nodeSyns[][]={
            {s1,s1,s1,s1,s1},
            {s2,s2,s2,s2,s2},
            {s3,s3,s3,s3,s3}
        };
        DoubleMatrix rval=new DoubleMatrix(me);
        CalcSynInfo c3=new CalcSynInfo(SynthesizerEnum.ADDITIVE, 3);
        CalcSynInfo []clusterSyns={c3,c3,c3};
        me.synthesisStep(me, rval, nodeSyns, clusterSyns, clusterStarts, clusterPriorities, NormalizerEnum.Normalized);
        DoubleMatrix correct=new DoubleMatrix(new double[][] {
            {0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0},
            {.3, 0, 0, 0, 0, 0},
            {.5, 0, 0, 0, 0, 0},
            {.2, 0, 0, 0 ,0, 0}
        });
        //System.out.println("Normalized:\n"+rval.toString()+"\n\n");
        assertTrue(correct.isNear(rval));
        me.synthesisStep(me, rval, nodeSyns, clusterSyns, clusterStarts, clusterPriorities, NormalizerEnum.Raw);
        //System.out.println("Unnormalized:\n"+rval.toString()+"\n\n");
        correct = new DoubleMatrix(new double[][]{
                    {0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0},
                    {.48, 0, 0, 0, 0, 0},
                    {.58, 0, 0, 0, 0, 0},
                    {.34, 0, 0, 0, 0, 0}
                });
        assertTrue(correct.isNear(rval));
    }

    @Test
    public void testSynStep3LevelHier() throws Exception {
        System.out.println("testSynStep3LevelHier");
        DoubleMatrix me=new DoubleMatrix(new double[][] {
            {0, 0, 0, 0, 0, 0, 0, 0, 0},
            {.6, 0, 0, 0, 0, 0, 0, 0, 0},
            {.4, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, .3, .1, 0, 0, 0, 0, 0, 0},
            {0, .2, .3, 0, 0, 0, 0, 0, 0},
            {0, .5, .6, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, .3, .1, .3, 0, 0, 0},
            {0, 0, 0, .15, .2, .5, 0, 0, 0},
            {0, 0, 0, .55, .7, .2, 0, 0, 0}
        });
        int clusterStarts[]={0, 1, 3, 6};
        double clusterPriorities[][]={
            {0, 0, 0, 0},
            {1, 0, 0, 0},
            {0, 1, 0, 0},
            {0, 0, 1, 0}
        };
        CalcSynInfo s1=new CalcSynInfo(SynthesizerEnum.ADDITIVE, 1);
        CalcSynInfo s2=new CalcSynInfo(SynthesizerEnum.ADDITIVE, 2);
        CalcSynInfo s3=new CalcSynInfo(SynthesizerEnum.ADDITIVE, 3);
        CalcSynInfo nodeSyns[][]={
            {s1,s1,s1,s1,s1,s1,s1,s1},
            {s2,s2,s2,s2,s2,s2,s2,s2},
            {s3,s3,s3,s3,s3,s3,s3,s3},
            {s3,s3,s3,s3,s3,s3,s3,s3}
        };
        DoubleMatrix rval=new DoubleMatrix(me);
        CalcSynInfo c4=new CalcSynInfo(SynthesizerEnum.ADDITIVE, 4);
        CalcSynInfo []clusterSyns={c4,c4,c4, c4};
        me.synthesisStep(me, rval, nodeSyns, clusterSyns, clusterStarts, clusterPriorities, NormalizerEnum.Normalized);
        DoubleMatrix rval2=new DoubleMatrix(rval.getSize());
        me.synthesisStep(rval, rval2, nodeSyns, clusterSyns, clusterStarts, clusterPriorities, NormalizerEnum.Normalized);
        System.out.println(rval.toString()+"\n");
        System.out.println(rval2.toString()+"\n");
        DoubleMatrix correct = new DoubleMatrix(new double[][]{
                    {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
                    {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
                    {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
                    {0.22, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
                    {0.24, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
                    {0.54, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
                    {0.0, 0.26, 0.24, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
                    {0.0, 0.335, 0.375, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
                    {0.0, 0.405, 0.385, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0}
                });
        DoubleMatrix correct2 = new DoubleMatrix(new double[][]{
                    {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
                    {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
                    {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
                    {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
                    {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
                    {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
                    {0.252, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
                    {0.35100000000000003, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
                    {0.397, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0}
                });
        assertTrue(correct.isNear(rval));
        assertTrue(correct2.isNear(rval2));
    }

    @Test
    public void testSynStepMult1() throws Exception {
        System.out.println("testSynStepMult1");
        DoubleMatrix me=new DoubleMatrix(new double[][] {
            {0, 0, 0, 0, 0},
            {.6, 0, 0, 0, 0},
            {.4, 0, 0, 0, 0},
            {0, .3, .1, 0,0},
            {0, .7, .9, 0,0}
        });
        int clusterStarts[]={0, 1, 3};
        double clusterPriorities[][]={
            {0, 0, 0},
            {1, 0, 0},
            {0, 1, 0}
        };
        CalcSynInfo s1=new CalcSynInfo(SynthesizerEnum.ADDITIVE, 1);
        CalcSynInfo s2=new CalcSynInfo(SynthesizerEnum.ADDITIVE, 2);
        s2.setType(SynthesizerEnum.MULTIPLICATIVE);
        CalcSynInfo nodeSyns[][]={
            {s1,s1,s1,s1,s1},
            {s2,s2,s2,s2,s2},
            {s2,s2,s2,s2,s2}
        };
        DoubleMatrix rval=new DoubleMatrix(me);
        CalcSynInfo c3=new CalcSynInfo(SynthesizerEnum.ADDITIVE, 3);
        CalcSynInfo []clusterSyns={c3,c3,c3};
        me.synthesisStep(me, rval, nodeSyns, clusterSyns, clusterStarts, clusterPriorities, NormalizerEnum.Raw);
        DoubleMatrix correct=new DoubleMatrix(new double[][] {
            {0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0},
            {0.19331820449317627, 0, 0, 0, 0},
            {0.7740264966144386, 0, 0, 0, 0}
        });
        assertTrue(correct.isNear(rval));
        //System.out.println(rval.toString());
    }

    @Test
    public void testSynStepMult1Inv() throws Exception {
        System.out.println("testSynStepMult1Inv");
        DoubleMatrix me=new DoubleMatrix(new double[][] {
            {0, 0, 0, 0, 0},
            {.6, 0, 0, 0, 0},
            {.4, 0, 0, 0, 0},
            {0, .3, .1, 0,0},
            {0, .7, .9, 0,0}
        });
        int clusterStarts[]={0, 1, 3};
        double clusterPriorities[][]={
            {0, 0, 0},
            {1, 0, 0},
            {0, 1, 0}
        };
        CalcSynInfo s1=new CalcSynInfo(SynthesizerEnum.ADDITIVE, 1);
        CalcSynInfo s2=new CalcSynInfo(SynthesizerEnum.ADDITIVE, 2);
        s2.setType(SynthesizerEnum.MULTIPLICATIVE);
        s2.setInverted(1, true);
        CalcSynInfo nodeSyns[][]={
            {s1,s1,s1,s1,s1},
            {s2,s2,s2,s2,s2},
            {s2,s2,s2,s2,s2}
        };
        DoubleMatrix rval=new DoubleMatrix(me);
        CalcSynInfo c3=new CalcSynInfo(SynthesizerEnum.ADDITIVE, 3);
        CalcSynInfo []clusterSyns={c3,c3,c3};
        me.synthesisStep(me, rval, nodeSyns, clusterSyns, clusterStarts, clusterPriorities, NormalizerEnum.Raw);
        DoubleMatrix correct=new DoubleMatrix(new double[][] {
            {0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0},
            {1.2197554094669347, 0, 0, 0, 0},
            {0.8420964184267019, 0, 0, 0, 0}
        });
        assertTrue(correct.isNear(rval));
        //System.out.println(rval.toString());
        //Try normalized.
        me.synthesisStep(me, rval, nodeSyns, clusterSyns, clusterStarts, clusterPriorities, NormalizerEnum.Normalized);
        correct=new DoubleMatrix(new double[][] {
            {0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0},
            {0.5915824759885983, 0, 0, 0, 0},
            {0.40841752401140174, 0, 0, 0, 0}
        });
        assertTrue(correct.isNear(rval));
        //System.out.println(rval.toString());
        //Try idealized
        me.synthesisStep(me, rval, nodeSyns, clusterSyns, clusterStarts, clusterPriorities, NormalizerEnum.Idealized);
        correct=new DoubleMatrix(new double[][] {
            {0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0},
            {1.0, 0, 0, 0, 0},
            {0.6903813763734159, 0, 0, 0, 0}
        });
        assertTrue(correct.isNear(rval));
        //System.out.println(rval.toString());
    }
    
    @Test
    public void testSynHier2Level() throws Exception {
        System.out.println("testSynHier2Level");
        DoubleMatrix me=new DoubleMatrix(new double[][] {
            {0, 0, 0, 0, 0},
            {.6, 0, 0, 0, 0},
            {.4, 0, 0, 0, 0},
            {0, .3, .1, 0,0},
            {0, .7, .9, 0,0}
        });
        int clusterStarts[]={0, 1, 3};
        double clusterPriorities[][]={
            {0, 0, 0},
            {1, 0, 0},
            {0, 1, 0}
        };
        CalcSynInfo s1=new CalcSynInfo(SynthesizerEnum.ADDITIVE, 1);
        CalcSynInfo s2=new CalcSynInfo(SynthesizerEnum.ADDITIVE, 2);
        CalcSynInfo nodeSyns[][]={
            {s1,s1,s1,s1,s1},
            {s2,s2,s2,s2,s2},
            {s2,s2,s2,s2,s2}
        };
        DoubleMatrix rval;
        CalcSynInfo c3=new CalcSynInfo(SynthesizerEnum.ADDITIVE, 3);
        CalcSynInfo []clusterSyns={c3,c3,c3};
        rval=me.synthesis(nodeSyns, clusterSyns, clusterStarts, clusterPriorities, NormalizerEnum.Normalized, ERR);
        DoubleMatrix correct=new DoubleMatrix(new double[][] {
            {0, 0, 0, 0, 0},
            {0.3, 0, 0, 0, 0},
            {0.2, 0, 0, 0, 0},
            {.11, 0.3, 0.1, 0, 0},
            {.39, 0.7, 0.9, 0, 0}
        });
        //System.out.println(rval.toString());
        assertTrue(correct.isNear(rval));        
    }
    
    @Test
    public void testSynANP2Cluster() throws Exception {
        System.out.println("testSynANP2Cluster");
        DoubleMatrix me=new DoubleMatrix(new double[][] {
            {.3, .1, .17, .35},
            {.2, .4, .33, .15},
            {.1, .15, .32, .2},
            {.4, .35, .18, .3}
        });
        int clusterStarts[]={0, 2};
        double clusterPriorities[][]={
            {.5, .5},
            {.5, .5}
        };
        CalcSynInfo s2a=new CalcSynInfo(SynthesizerEnum.ADDITIVE, 2);
        CalcSynInfo s2b=new CalcSynInfo(SynthesizerEnum.ADDITIVE, 2);
        CalcSynInfo nodeSyns[][]={
            {s2a,s2a,s2a,s2a},
            {s2a,s2a,s2a,s2a}
        };
        DoubleMatrix rval;
        CalcSynInfo c2=new CalcSynInfo(SynthesizerEnum.ADDITIVE, 2);
        CalcSynInfo []clusterSyns={c2,c2};
        rval=me.synthesis(nodeSyns, clusterSyns, clusterStarts, clusterPriorities, NormalizerEnum.Normalized, ERR);
        //System.out.println("Synthesis matrix:\n"+rval.toString());
        DoubleMatrix correct=me.limitPowerIntelligent(ERR);
        assertTrue(correct.isNear(rval, ERR));
        //assertTrue(correct.isNear(rval));                
    }

    @Test
    public void testSynANP2ClusterInv() throws Exception {
        System.out.println("testSynANP2Cluster");
        DoubleMatrix meOld=new DoubleMatrix(new double[][] {
            {.3, .1, .17, .35},
            {.2, .4, .33, .15},
            {.1, .15, .32, .2},
            {.4, .35, .18, .3}
        });
        DoubleMatrix me=new DoubleMatrix(new double[][] {
            {.3, .1, .17, .15},
            {.2, .4, .33, .35},
            {.1, .15, .32, .3},
            {.4, .35, .18, .2}
        });
        int clusterStarts[]={0, 2};
        double clusterPriorities[][]={
            {.5, .5},
            {.5, .5}
        };
        CalcSynInfo s2a=new CalcSynInfo(SynthesizerEnum.ADDITIVE, 2);
        CalcSynInfo s2b=new CalcSynInfo(SynthesizerEnum.ADDITIVE, 2);
        s2b.setInverted(1, true);
        //s2b.setType(SynthesizerEnum.MULTIPLICATIVE);
        CalcSynInfo nodeSyns[][]={
            {s2a,s2a,s2a,s2a},
            {s2b,s2a,s2a,s2a}
        };
        DoubleMatrix rval;
        CalcSynInfo c2=new CalcSynInfo(SynthesizerEnum.ADDITIVE, 2);
        CalcSynInfo []clusterSyns={c2,c2};
        rval=me.synthesis(nodeSyns, clusterSyns, clusterStarts, clusterPriorities, NormalizerEnum.Normalized, ERR);
        System.out.println("Synthesis matrix:\n"+rval.toString());
        //DoubleMatrix correct=me.limitPowerIntelligent(ERR);
        //assertTrue(correct.isNear(rval, ERR));
        //assertTrue(correct.isNear(rval));                
    }
    
    @Test
    public void testAgainstSD1() throws Exception
    {
        System.out.println("testAgainstSD1");
        DoubleMatrix me=new DoubleMatrix(new double[][] {
            {0,0,0,0,0},
            {.7,0,0,0,0},
            {.3,0,0,0,0},
            {0,.6,.2,0,0},
            {0,.4,.8,0,0}
        });
        int clusterStarts[]={0, 1,3};
        double clusterPriorities[][]={
            {0, 0, 0},
            {1, 0, 0},
            {0, 1, 0}
        };
        CalcSynInfo s1=new CalcSynInfo(SynthesizerEnum.ADDITIVE, 1);
        CalcSynInfo s2=new CalcSynInfo(SynthesizerEnum.ADDITIVE, 2);
        CalcSynInfo s2i=new CalcSynInfo(SynthesizerEnum.ADDITIVE, 2);
        s2i.setInverted(1, true);
        s2i.setType(SynthesizerEnum.MULTIPLICATIVE_SD);
        CalcSynInfo nodeSyns[][]={
            {s1, s1, s1, s1, s1},
            {s2i, s2, s2, s2, s2},
            {s2, s2, s2, s2, s2}
        };
        DoubleMatrix rval;
        CalcSynInfo c3=new CalcSynInfo(SynthesizerEnum.ADDITIVE, 3);
        CalcSynInfo []clusterSyns={c3,c3, c3};
        rval=me.synthesis(nodeSyns, clusterSyns, clusterStarts, clusterPriorities, NormalizerEnum.Normalized, ERR);
        //System.out.println("Synthesis matrix vs sd:\n"+rval.toString());
        DoubleMatrix correct=new DoubleMatrix(new double[][] {
            {0.0, 0.0, 0.0, 0.0, 0.0}, 
            {0.15555555555555556, 0.0, 0.0, 0.0, 0.0}, 
            {0.06666666666666667, 0.0, 0.0, 0.0, 0.0}, 
            {0.6666666666666666, 0.6, 0.2, 0.0, 0.0}, 
            {0.1111111111111111, 0.4, 0.8, 0.0, 0.0}             
        });
        assertTrue(correct.isNear(rval, ERR));
        //Try sd additive,so we must idealize first
        me.idealizeCols();
        s2i.setType(SynthesizerEnum.ADDITIVE_SD);
        rval=me.synthesis(nodeSyns, clusterSyns, clusterStarts, clusterPriorities, NormalizerEnum.Raw, ERR);
        correct=new DoubleMatrix(new double[][] {
            {0.0, 0.0, 0.0, 0.0, 0.0}, 
            {1.0, 0.0, 0.0, 0.0, 0.0}, 
            {0.4285714285714286, 0.0, 0.0, 0.0, 0.0}, 
            {0.625, 1.0, 0.25, 0.0, 0.0}, 
            {0.16666666666666669, 0.6666666666666667, 1.0, 0.0, 0.0}
        });
        assertTrue(correct.isNear(rval,ERR));
        //System.out.println("SuperDecisions additive gives:\n"+rval.toString());
    }

    @Test
    /**
     * This matrix comes from the BigBurger model.
     */
    public void testLimitmatrixTime1() throws Exception {
        DoubleMatrix mat = new DoubleMatrix(new double[][] {
            { 0.00000 , 0.15210 , 0.14078 , 0.15135 , 0.14715 , 0.14715 , 0.13994 , 0.11216 , 0.11361 , 0.20956 , 0.12556 , 0.20618 , 0.19015 , 0.18051 , 0.15340 , 0.20236 , 0.19936 , 0.21265 , 0.17694 , 0.00000 , 0.00000 , 0.00000 , 0.24987 , 0.06427 , 0.13516 , 0.12982 , 0.12062 , 0.12982 , 0.12062 , 0.11850 , 0.28894 , 0.31570},
            { 0.08450 , 0.00000 , 0.02822 , 0.03764 , 0.04144 , 0.04144 , 0.04745 , 0.03690 , 0.03400 , 0.06272 , 0.03961 , 0.05895 , 0.04000 , 0.04643 , 0.04840 , 0.04991 , 0.05127 , 0.07078 , 0.06738 , 0.00000 , 0.00000 , 0.00000 , 0.00137 , 0.06427 , 0.03721 , 0.04353 , 0.04593 , 0.04353 , 0.04593 , 0.05172 , 0.11703 , 0.10387},
            { 0.08450 , 0.01690 , 0.00000 , 0.01121 , 0.01161 , 0.01161 , 0.01281 , 0.01209 , 0.01354 , 0.02497 , 0.01654 , 0.02243 , 0.01679 , 0.02000 , 0.02020 , 0.02046 , 0.02209 , 0.03539 , 0.03850 , 0.00000 , 0.00000 , 0.00000 , 0.00075 , 0.06427 , 0.02044 , 0.01945 , 0.02625 , 0.01945 , 0.02625 , 0.02258 , 0.04763 , 0.03402},
            { 0.05729 , 0.05561 , 0.02641 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.01614 , 0.04937 , 0.06094 , 0.02361 , 0.06873 , 0.04149 , 0.04939 , 0.04440 , 0.05290 , 0.05099 , 0.06572 , 0.11313 , 0.00000 , 0.00000 , 0.10881 , 0.07510 , 0.02970 , 0.04357 , 0.01974 , 0.03556 , 0.05020 , 0.05459 , 0.05144 , 0.00000 , 0.00000},
            { 0.01879 , 0.02567 , 0.05134 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.02824 , 0.03804 , 0.11232 , 0.04094 , 0.08081 , 0.04889 , 0.04939 , 0.04440 , 0.02262 , 0.01744 , 0.02108 , 0.05657 , 0.00000 , 0.00000 , 0.10881 , 0.05292 , 0.05921 , 0.03130 , 0.04837 , 0.03556 , 0.02652 , 0.02116 , 0.02296 , 0.00000 , 0.00000},
            { 0.01004 , 0.00949 , 0.01116 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.09144 , 0.01806 , 0.13784 , 0.07603 , 0.04026 , 0.09754 , 0.09878 , 0.08880 , 0.02262 , 0.01744 , 0.02108 , 0.05657 , 0.00000 , 0.00000 , 0.10881 , 0.06199 , 0.05921 , 0.04997 , 0.06437 , 0.07113 , 0.01335 , 0.01227 , 0.01264 , 0.00000 , 0.00000},
            { 0.09988 , 0.09523 , 0.09709 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.05629 , 0.08664 , 0.04323 , 0.07603 , 0.09777 , 0.05902 , 0.04939 , 0.04440 , 0.09354 , 0.10580 , 0.11618 , 0.05657 , 0.00000 , 0.00000 , 0.10881 , 0.06199 , 0.02970 , 0.05299 , 0.04535 , 0.03556 , 0.08775 , 0.08980 , 0.09078 , 0.00000 , 0.00000},
            { 0.04782 , 0.03614 , 0.04184 , 0.03497 , 0.04294 , 0.03891 , 0.03573 , 0.00000 , 0.17289 , 0.00000 , 0.04874 , 0.17366 , 0.16769 , 0.16769 , 0.10050 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.05987 , 0.02725 , 0.03916 , 0.02294 , 0.01880 , 0.01701 , 0.02688 , 0.02372 , 0.01824 , 0.05656 , 0.04434},
            { 0.02669 , 0.03614 , 0.05115 , 0.08189 , 0.02482 , 0.01919 , 0.02865 , 0.04322 , 0.00000 , 0.00000 , 0.09747 , 0.08670 , 0.05590 , 0.05590 , 0.10050 , 0.17196 , 0.17196 , 0.20102 , 0.24467 , 0.13333 , 0.16667 , 0.02824 , 0.02725 , 0.02417 , 0.02104 , 0.02215 , 0.01309 , 0.01299 , 0.02159 , 0.05482 , 0.05656 , 0.07035},
            { 0.05421 , 0.05296 , 0.02780 , 0.02591 , 0.01431 , 0.03421 , 0.01632 , 0.04322 , 0.00000 , 0.00000 , 0.04874 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.53333 , 0.50000 , 0.21348 , 0.16350 , 0.01253 , 0.01611 , 0.02674 , 0.03245 , 0.02072 , 0.01600 , 0.02585 , 0.00000 , 0.00000},
            { 0.01029 , 0.01376 , 0.01821 , 0.03841 , 0.09911 , 0.08887 , 0.10047 , 0.08645 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.07493 , 0.00000 , 0.03603 , 0.05180 , 0.04420 , 0.04934 , 0.05130 , 0.05057 , 0.01298 , 0.11311 , 0.11153},
            { 0.06448 , 0.06644 , 0.06942 , 0.07217 , 0.06357 , 0.05458 , 0.06775 , 0.02588 , 0.10352 , 0.00000 , 0.11673 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.04770 , 0.04595 , 0.05248 , 0.05248 , 0.04591 , 0.05248 , 0.05248 , 0.00000 , 0.00000},
            { 0.02050 , 0.01649 , 0.01978 , 0.01946 , 0.02251 , 0.03119 , 0.02036 , 0.02588 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.07063 , 0.04229 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.01379 , 0.01532 , 0.01314 , 0.01314 , 0.01533 , 0.01314 , 0.01314 , 0.00000 , 0.00000},
            { 0.00659 , 0.00553 , 0.00670 , 0.00724 , 0.01584 , 0.01593 , 0.01504 , 0.02588 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.04704 , 0.00000 , 0.08471 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.01783 , 0.01532 , 0.01314 , 0.01314 , 0.01533 , 0.01314 , 0.01314 , 0.00000 , 0.00000},
            { 0.01143 , 0.01455 , 0.00711 , 0.01425 , 0.01120 , 0.01141 , 0.00995 , 0.02588 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.09423 , 0.07063 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.01259 , 0.01532 , 0.01314 , 0.01314 , 0.01533 , 0.01314 , 0.01314 , 0.00000 , 0.00000},
            { 0.08233 , 0.08809 , 0.08815 , 0.08044 , 0.07956 , 0.08606 , 0.08606 , 0.06048 , 0.04862 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.24096 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.07122 , 0.07259 , 0.07259 , 0.07259 , 0.07259 , 0.07259 , 0.07259 , 0.00000 , 0.00000},
            { 0.05194 , 0.05556 , 0.05548 , 0.05074 , 0.05197 , 0.05428 , 0.05428 , 0.06048 , 0.11158 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.12048 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.08994 , 0.07259 , 0.07259 , 0.07259 , 0.07259 , 0.07259 , 0.07259 , 0.00000 , 0.00000},
            { 0.03273 , 0.02336 , 0.02337 , 0.03198 , 0.03163 , 0.02282 , 0.02282 , 0.06048 , 0.02123 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.12048 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.05662 , 0.07259 , 0.07259 , 0.07259 , 0.07259 , 0.07259 , 0.07259 , 0.00000 , 0.00000},
            { 0.04983 , 0.05150 , 0.04396 , 0.03944 , 0.05135 , 0.04853 , 0.02389 , 0.01516 , 0.07577 , 0.00000 , 0.08544 , 0.00000 , 0.00000 , 0.00000 , 0.05050 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.05561 , 0.00000 , 0.02391 , 0.02725 , 0.02297 , 0.03002 , 0.02703 , 0.02919 , 0.02355 , 0.02261 , 0.02261 , 0.00000 , 0.00000},
            { 0.01671 , 0.01695 , 0.01843 , 0.01853 , 0.02307 , 0.02918 , 0.02446 , 0.01516 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.08451 , 0.00000 , 0.00000 , 0.00000 , 0.04198 , 0.02725 , 0.02048 , 0.01882 , 0.00978 , 0.01103 , 0.02056 , 0.02261 , 0.02261 , 0.00000 , 0.00000},
            { 0.00747 , 0.00555 , 0.01162 , 0.01593 , 0.01029 , 0.00984 , 0.01880 , 0.01516 , 0.00000 , 0.03354 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.13453 , 0.00000 , 0.09187 , 0.02725 , 0.01592 , 0.01136 , 0.01260 , 0.01351 , 0.01542 , 0.02261 , 0.02261 , 0.00000 , 0.00000},
            { 0.00000 , 0.00000 , 0.00000 , 0.01209 , 0.02206 , 0.01765 , 0.01325 , 0.01516 , 0.00000 , 0.07687 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.02525 , 0.00000 , 0.00000 , 0.01409 , 0.00000 , 0.06360 , 0.16667 , 0.00000 , 0.02725 , 0.01053 , 0.01136 , 0.02214 , 0.01567 , 0.01501 , 0.00754 , 0.00754 , 0.00000 , 0.00000},
            { 0.00000 , 0.00000 , 0.00000 , 0.02712 , 0.00633 , 0.00792 , 0.03272 , 0.01516 , 0.00000 , 0.02935 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.02525 , 0.00000 , 0.00000 , 0.01409 , 0.00000 , 0.07959 , 0.16667 , 0.03050 , 0.00000 , 0.01302 , 0.01136 , 0.01136 , 0.01351 , 0.00838 , 0.00754 , 0.00754 , 0.00000 , 0.00000},
            { 0.04151 , 0.02122 , 0.04541 , 0.01307 , 0.05181 , 0.05404 , 0.01559 , 0.00611 , 0.01222 , 0.01982 , 0.00765 , 0.00741 , 0.02018 , 0.02018 , 0.01814 , 0.01752 , 0.03468 , 0.02049 , 0.02710 , 0.00000,  0.00000 , 0.00000 , 0.02414 , 0.03584 , 0.02085 , 0.01613 , 0.02052 , 0.01099 , 0.00799 , 0.00937 , 0.04574 , 0.04574},
            { 0.02222 , 0.03791 , 0.02919 , 0.04080 , 0.03714 , 0.04099 , 0.04149 , 0.01302 , 0.02025 , 0.01544 , 0.01212 , 0.01316 , 0.02018 , 0.02018 , 0.01814 , 0.01752 , 0.01814 , 0.02049 , 0.02710 , 0.00000 , 0.00000 , 0.00000 , 0.02414 , 0.01948 , 0.02747 , 0.02687 , 0.02014 , 0.02323 , 0.01424 , 0.00937 , 0.04574 , 0.04574},
            { 0.06032 , 0.05767 , 0.04589 , 0.02430 , 0.08734 , 0.06572 , 0.01582 , 0.00951 , 0.01505 , 0.02650 , 0.01390 , 0.00751 , 0.02018 , 0.02018 , 0.01814 , 0.01752 , 0.04007 , 0.02049 , 0.02710 , 0.00000 , 0.00000 , 0.00000 , 0.02414 , 0.02373 , 0.02523 , 0.02687 , 0.02302 , 0.02036 , 0.01061 , 0.00937 , 0.04574 , 0.04574},
            { 0.01703 , 0.02333 , 0.01622 , 0.01788 , 0.01192 , 0.02611 , 0.01284 , 0.00611 , 0.00724 , 0.01419 , 0.00944 , 0.07970 , 0.02018 , 0.02018 , 0.01814 , 0.01752 , 0.00711 , 0.02049 , 0.02710 , 0.00000 , 0.00000 , 0.00000 , 0.02414 , 0.01286 , 0.01124 , 0.01038 , 0.02815 , 0.00937 , 0.00774 , 0.00937 , 0.04574 , 0.04574},
            { 0.00746 , 0.00972 , 0.01119 , 0.04516 , 0.01536 , 0.02107 , 0.05272 , 0.01970 , 0.02817 , 0.03297 , 0.02168 , 0.01891 , 0.02018 , 0.02018 , 0.01814 , 0.01752 , 0.00649 , 0.02049 , 0.02710 , 0.00000 , 0.00000 , 0.00000 , 0.02414 , 0.01274 , 0.01561 , 0.01713 , 0.01101 , 0.02023 , 0.02847 , 0.02587 , 0.04574 , 0.04574},
            { 0.00713 , 0.00680 , 0.00811 , 0.05272 , 0.01673 , 0.01191 , 0.05066 , 0.02140 , 0.03020 , 0.04987 , 0.02679 , 0.01891 , 0.02018 , 0.02018 , 0.01814 , 0.01752 , 0.00600 , 0.02049 , 0.02710 , 0.00000 , 0.00000 , 0.00000 , 0.02414 , 0.01012 , 0.01224 , 0.01375 , 0.01101 , 0.02036 , 0.02735 , 0.02587 , 0.04574 , 0.04574},
            { 0.00632 , 0.00535 , 0.00600 , 0.03530 , 0.00894 , 0.00939 , 0.04011 , 0.03726 , 0.00000 , 0.04987 , 0.03597 , 0.01891 , 0.02018 , 0.02018 , 0.01814 , 0.01752 , 0.01017 , 0.02049 , 0.02710 , 0.00000 , 0.00000 , 0.00000 , 0.02414 , 0.01012 , 0.01224 , 0.01375 , 0.01101 , 0.02036 , 0.02847 , 0.03562 , 0.04574 , 0.04574},
            { 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.03851 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000},
            { 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.03851 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000 , 0.00000}
        });
        long start=new Date().getTime();
        DoubleMatrix limit=mat.limitPowerIntelligent(1e-10);
        long end=new Date().getTime();
        long diff=end - start;
        if (diff > 150) {
            fail("Took "+diff+" milliseconds to compute limit matrix for BigBurger model, which was longer than 150 milliseconds, this could be an issue.");
        }
        System.out.println("Took "+diff+" milliseconds to limit matrix the BigBurger weighted supermatrix.");
    }
}

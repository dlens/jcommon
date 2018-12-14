
package com.dlens.common2.numeric.sets;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author wjadams
 */
public class UnionDoubleIntervalsTest {

    public UnionDoubleIntervalsTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }


    @Test
    public void testToDoubleIntervalApprox() {
        UnionDoubleIntervals un=new UnionDoubleIntervals(new DoubleIntervalSet[] {
            new DoubleIntervalSet(-1, 0, false, false),
            new DoubleIntervalSet(0, 1, true, true),
            new DoubleIntervalSet(2, 4, true, true)
        });
        String uns=un.toString();
        assertEquals("(-1.0, 1.0] U [2.0, 4.0]", uns);
        assertEquals("Empty", (new DoubleIntervalSet(5, 1, true, false)).toString());
        assertEquals(2, un.getIntervals().size());
        assertEquals(new DoubleIntervalSet(-1, 4, false, true), un.toDoubleIntervalApprox());
    }


    @Test
    public void testSum2() throws Exception {
    	UnionDoubleIntervals a=new UnionDoubleIntervals(new DoubleIntervalSet(1, 2));
    	UnionDoubleIntervals b=new UnionDoubleIntervals(new DoubleIntervalSet(5, 7));
    	UnionDoubleIntervals aPb=UnionDoubleIntervals.sum(a, b);
    	assertEquals(1, aPb.getIntervals().size());
    	DoubleIntervalSet aPbS=aPb.getIntervals().get(0);
    	assertEquals(aPbS, new DoubleIntervalSet(6, 9));
    	a=new UnionDoubleIntervals(new DoubleIntervalSet[] {
    		new DoubleIntervalSet(0, 0, true, true),
    		new DoubleIntervalSet(3, 4, true, true)
    	});
    	b=new UnionDoubleIntervals(new DoubleIntervalSet[] {
    		new DoubleIntervalSet(1, 1, true, true),
    		new DoubleIntervalSet(6, 8, true, true)
    	});
    	aPb=UnionDoubleIntervals.sum(a, b);
    	DoubleIntervalSet[] ints=aPb.getIntervals().toArray(new DoubleIntervalSet[0]);
    	DoubleIntervalSet[] correct={new DoubleIntervalSet(1.0, 1.0, true, true),
    			new DoubleIntervalSet(4.0, 5.0, true, true),
    			new DoubleIntervalSet(6.0, 8.0, true, true),
    			new DoubleIntervalSet(9.0, 12.0, true, true)};
    	assertArrayEquals(correct, ints);
    }
    
    @Test
    public void testSum() throws Exception {
    	UnionDoubleIntervals a=new UnionDoubleIntervals(new DoubleIntervalSet[] {
        		new DoubleIntervalSet(0, 0, true, true),
        		new DoubleIntervalSet(3, 4, true, true)
        	});
    	UnionDoubleIntervals b=new UnionDoubleIntervals(new DoubleIntervalSet[] {
        		new DoubleIntervalSet(1, 1, true, true),
        		new DoubleIntervalSet(6, 8, true, true)
        	});
    	UnionDoubleIntervals c=new UnionDoubleIntervals(new DoubleIntervalSet[] {
        		new DoubleIntervalSet(0, 0, true, true),
        		new DoubleIntervalSet(1, 2, true, true)
        	});
    	UnionDoubleIntervals aPb=UnionDoubleIntervals.sum(new UnionDoubleIntervals[] {a, b,c});
    	UnionDoubleIntervals correct=new UnionDoubleIntervals(new DoubleIntervalSet[] {
    			new DoubleIntervalSet(1.0, 1.0, true, true),
    			new DoubleIntervalSet(2.0, 3.0, true, true),
    			new DoubleIntervalSet(4.0, 14.0, true, true)
    	});
    	assertEquals(aPb, correct);
    	assertNotSame(aPb, a);
    }

    @Test
    public void testIntersects() throws Exception {
    	UnionDoubleIntervals a=new UnionDoubleIntervals(new DoubleIntervalSet[] {
    			new DoubleIntervalSet(-1, 1, true, true),
    			new DoubleIntervalSet(3, 10, false, true)
    	});
    	UnionDoubleIntervals b=new UnionDoubleIntervals(new DoubleIntervalSet[] {
    			new DoubleIntervalSet(-5, -3, true, true),
    			new DoubleIntervalSet(-2, -1, false, true)
    	});
    	UnionDoubleIntervals c=new UnionDoubleIntervals(new DoubleIntervalSet[] {
    			new DoubleIntervalSet(-5, -3, true, true),
    			new DoubleIntervalSet(-2, 0, false, true)
    	});
    	UnionDoubleIntervals d=new UnionDoubleIntervals(new DoubleIntervalSet[] {
    			new DoubleIntervalSet(11, 12, true, true),
    			new DoubleIntervalSet(3, 10, false, true)
    	});
    	assertFalse(b.intersects(d));
    	assertTrue(b.intersects(a));
    	assertTrue(a.intersects(b));
    	assertTrue(a.intersects(d));
    }
}

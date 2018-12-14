
package com.dlens.common2.numeric.sets;

import java.util.Vector;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author wjadams
 */
public class DoubleIntervalSetTest {

    public DoubleIntervalSetTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void testEmpty() {
        DoubleIntervalSet empty=DoubleIntervalSet.emptySet();
        assertTrue(empty.isEmpty());
        assertFalse(empty.isUnbounded());
    }

    @Test
    public void testLegalBounds() {
        double x1=1, x2=2;
        assertFalse(DoubleIntervalSet.legalBounds(x1, x1, false, false));
        assertFalse(DoubleIntervalSet.legalBounds(x1, x1, true, false));
        assertFalse(DoubleIntervalSet.legalBounds(x1, x1, false, true));
        assertTrue(DoubleIntervalSet.legalBounds(x1, x1, true, true));
        assertFalse(DoubleIntervalSet.legalBounds(x2, x1, true, true));
        assertFalse(DoubleIntervalSet.legalBounds(x2, x1, false, true));
        assertFalse(DoubleIntervalSet.legalBounds(x2, x1, true, false));
        assertFalse(DoubleIntervalSet.legalBounds(x2, x1, false, false));

        assertTrue(DoubleIntervalSet.legalBounds(x1, x2, true, true));
        assertTrue(DoubleIntervalSet.legalBounds(x1, x2, false, true));
        assertTrue(DoubleIntervalSet.legalBounds(x1, x2, true, false));
        assertTrue(DoubleIntervalSet.legalBounds(x1, x2, false, false));
    }

    @Test
    public void testIsUnbounded() {
        DoubleIntervalSet ul = new DoubleIntervalSet(Double.NEGATIVE_INFINITY, 2, true, true);
        DoubleIntervalSet uu = new DoubleIntervalSet(3, Double.POSITIVE_INFINITY, true, false);
        DoubleIntervalSet both=new DoubleIntervalSet(Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY,false,false);
        DoubleIntervalSet ok=new DoubleIntervalSet(3, 4, true, true);
        assertTrue(ul.isUnbounded());
        assertTrue(uu.isUnbounded());
        assertTrue(both.isUnbounded());
        assertFalse(ok.isUnbounded());
        assertFalse(ul.isCompletelyUnbounded());
        assertFalse(uu.isCompletelyUnbounded());
        assertTrue(both.isCompletelyUnbounded());
        assertFalse(ok.isCompletelyUnbounded());
    }

    @Test
    public void testGetLowerBound() {
    }

    @Test
    public void testGetUpperBound() {
    }

    @Test
    public void testGetLowerHasEquals() {
    }

    @Test
    public void testGetUpperHasEquals() {
    }

    @Test
    public void testCompareTo() {
        DoubleIntervalSet small=new DoubleIntervalSet(-1, 2, true, false);
        DoubleIntervalSet smallPlus=new DoubleIntervalSet(-1, 2, false, true);
        assertTrue(small.compareTo(smallPlus)<0);
        int val=smallPlus.compareTo(small);
        assertTrue(val>0);
    }

    @Test
    public void testStrictlyLess() {
    }

    @Test
    public void testStrictlyIncreasing() {
    }

    @Test
    public void testClosureIntersection() {
    }

    @Test
    public void testClosureIntersects() {
    }

    @Test
    public void testIsEmpty() {
    }

    @Test
    public void testEmptySet() {
    }

    @Test
    public void testIsCompletelyUnbounded() {
    }

    @Test
    public void testJoin() {
        DoubleIntervalSet a=DoubleIntervalSet.join(
                new DoubleIntervalSet(1,2, false, true), new DoubleIntervalSet(1, 2, true, false));
        assertEquals(new DoubleIntervalSet(1,2,true,true), a);
        assertNotSame(new DoubleIntervalSet(1,2,false,true), a);
        a=DoubleIntervalSet.join(
                new DoubleIntervalSet(3, 6, false, false),
                new DoubleIntervalSet(5, 8, false, true));
        assertEquals(new DoubleIntervalSet(3,8,false, true),a);
    }
    
    @Test
    public void testStrinctlyIncreasing() throws Exception {
    	Vector<DoubleIntervalSet> a =
    		DoubleIntervalSet.strictlyIncreasing(new DoubleIntervalSet[] {
    				new DoubleIntervalSet(-1, 0, false, true),
    				new DoubleIntervalSet(0, 2, false, false),
    				new DoubleIntervalSet(2, 3, true, false),
    				new DoubleIntervalSet(3, 4, false, true),
    				new DoubleIntervalSet(3.5, 7, false, true)
    		});
    	assertEquals(2, a.size());
    	assertEquals(a.get(0), new DoubleIntervalSet(-1, 3, false, false));
    	assertEquals(a.get(1), new DoubleIntervalSet(3, 7, false, true));
    	a=
    		DoubleIntervalSet.strictlyIncreasing(new DoubleIntervalSet[] {
    				new DoubleIntervalSet(3.5, 7, false, true),
    				new DoubleIntervalSet(0, 2, false, false),
    				new DoubleIntervalSet(-1, 0, false, true),
    				new DoubleIntervalSet(3, 4, false, true),
    				new DoubleIntervalSet(2, 3, true, false),
    		});
    	assertEquals(2, a.size());
    	assertEquals(a.get(0), new DoubleIntervalSet(-1, 3, false, false));
    	assertEquals(a.get(1), new DoubleIntervalSet(3, 7, false, true));
    		
    }

    @Test
    public void testJoinEmpty() {
    	DoubleIntervalSet a=new DoubleIntervalSet(0, 1, true, false);
    	DoubleIntervalSet b=new DoubleIntervalSet(.5, 1.5, false, false);
    	DoubleIntervalSet test=DoubleIntervalSet.join(null, a);
    	DoubleIntervalSet empty=DoubleIntervalSet.emptySet();
    	assertEquals(a, test);
    	assertEquals(b, DoubleIntervalSet.join(b, null));
    	assertEquals(DoubleIntervalSet.emptySet(), DoubleIntervalSet.join(null, null));
    	assertEquals(DoubleIntervalSet.emptySet(), DoubleIntervalSet.join(empty, null));
    	assertEquals(DoubleIntervalSet.emptySet(), DoubleIntervalSet.join(empty, empty));
    	assertEquals(DoubleIntervalSet.emptySet(), DoubleIntervalSet.join(null, empty));
    	
    }
    
    @Test
    public void testAddToNicely() {
    	Vector<DoubleIntervalSet> sets=new Vector<DoubleIntervalSet>();
    	DoubleIntervalSet.addToKeepStrictlyIncreasing(sets, new DoubleIntervalSet(10, 20, true, true));
    	DoubleIntervalSet.addToKeepStrictlyIncreasing(sets, new DoubleIntervalSet(1, 2, true, true));
    	DoubleIntervalSet.addToKeepStrictlyIncreasing(sets, new DoubleIntervalSet(3, 4, true, true));
    	DoubleIntervalSet.addToKeepStrictlyIncreasing(sets, new DoubleIntervalSet(3, 4, true, true));
    	System.out.println(sets.toString());
    }
    
    @Test
    public void testIntersect() {
    	DoubleIntervalSet a=new DoubleIntervalSet(1, 3, true, true);
    	DoubleIntervalSet b=new DoubleIntervalSet(2, 5, true, true);
    	assertEquals(new DoubleIntervalSet(2,3, true, true), a.intersect(b));
    	a=new DoubleIntervalSet(1, 3, false, true);
    	assertEquals(new DoubleIntervalSet(2,3, true, true), a.intersect(b));
    	a=new DoubleIntervalSet(1, 3, true, false);
    	assertEquals(new DoubleIntervalSet(2,3, true, false), a.intersect(b));
    	DoubleIntervalSet c=new DoubleIntervalSet(1, 3, false, true);
    	assertEquals(new DoubleIntervalSet(1,3,false,false), a.intersect(c));
    	a=new DoubleIntervalSet(Double.NEGATIVE_INFINITY, 3, false, true);
    	b=new DoubleIntervalSet(2, Double.POSITIVE_INFINITY, true, false);
    	c=new DoubleIntervalSet(Double.NEGATIVE_INFINITY, 2, false, false);
    	assertNotSame(new DoubleIntervalSet(2, 3, false, false), a.intersect(b));
    	assertEquals(new DoubleIntervalSet(2, 3, true, true), a.intersect(b));
    	assertEquals(new DoubleIntervalSet(Double.NEGATIVE_INFINITY, 2, false, false), a.intersect(c));
    	DoubleIntervalSet d=new DoubleIntervalSet(3, Double.POSITIVE_INFINITY, false, true);
    	DoubleIntervalSet e=new DoubleIntervalSet(3, Double.POSITIVE_INFINITY, true, true);
    	DoubleIntervalSet f=new DoubleIntervalSet(4, 5, false, true);
    	DoubleIntervalSet g=new DoubleIntervalSet(4, 5, false, true);
    	assertTrue(a.intersects(c));
    	assertFalse(a.intersects(d));
    	assertTrue(a.intersects(e));
    	assertFalse(a.intersects(f));
    	assertFalse(a.intersects(g));
    }
}
